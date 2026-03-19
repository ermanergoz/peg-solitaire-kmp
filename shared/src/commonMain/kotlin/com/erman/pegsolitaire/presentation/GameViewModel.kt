package com.erman.pegsolitaire.presentation

import com.erman.pegsolitaire.domain.model.GameMode
import com.erman.pegsolitaire.domain.model.GameState
import com.erman.pegsolitaire.domain.usecase.CellClickEvent
import com.erman.pegsolitaire.domain.usecase.CellClickResult
import com.erman.pegsolitaire.domain.usecase.CreateBoardUseCase
import com.erman.pegsolitaire.domain.usecase.GenerateLevelUseCase
import com.erman.pegsolitaire.domain.usecase.ProcessCellClickUseCase
import com.erman.pegsolitaire.domain.usecase.SaveLevelProgressUseCase
import com.erman.pegsolitaire.domain.usecase.SaveScoreUseCase
import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.engine.BoardType
import com.erman.pegsolitaire.engine.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.TimeSource

private const val TIMER_INTERVAL_MILLIS = 1000L
private const val EVENT_BUFFER_SIZE = 8

class GameViewModel(
    private val processCellClickUseCase: ProcessCellClickUseCase,
    private val createBoardUseCase: CreateBoardUseCase,
    private val saveScoreUseCase: SaveScoreUseCase,
    private val saveLevelProgressUseCase: SaveLevelProgressUseCase,
    private val generateLevelUseCase: GenerateLevelUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = EVENT_BUFFER_SIZE)
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    private val moveHistory = mutableListOf<Board>()
    private var pendingGameState: GameState? = null
    private var timerJob: Job? = null
    private var timerStartMark: TimeSource.Monotonic.ValueTimeMark? = null
    private var timerBaseMillis: Long = 0L

    fun startClassicGame(boardType: BoardType) {
        try {
            pendingGameState = null
            val board = createBoardUseCase(boardType)
            moveHistory.clear()
            stopTimer()
            _state.value = GameUiState(
                gameState = GameState(
                    board = board,
                    boardType = boardType,
                    gameMode = GameMode.CLASSIC,
                    remainingPegs = board.countPegs(),
                    totalPegs = board.countPegs()
                )
            )
            startTimer()
        } catch (e: Exception) {
            _state.value = GameUiState(error = GENERIC_ERROR_MESSAGE)
        }
    }

    fun startChallengeLevel(levelNumber: Int) {
        pendingGameState = null
        _state.value = GameUiState(isLoading = true)
        scope.launch {
            try {
                val level = withContext(Dispatchers.Default) {
                    generateLevelUseCase(levelNumber)
                }
                moveHistory.clear()
                stopTimer()
                _state.value = GameUiState(
                    gameState = GameState(
                        board = level.board,
                        boardType = level.boardType,
                        gameMode = GameMode.CHALLENGE,
                        remainingPegs = level.totalPegs,
                        totalPegs = level.totalPegs,
                        levelNumber = levelNumber
                    )
                )
                startTimer()
            } catch (e: Exception) {
                _state.value = GameUiState(error = GENERIC_ERROR_MESSAGE)
            }
        }
    }

    fun onCellClicked(row: Int, col: Int) {
        if (_state.value.pendingMove != null) return
        val gameState = _state.value.gameState ?: return
        if (gameState.isGameOver) return

        val target = Position(row, col)
        val clickResult = processCellClickUseCase(gameState.board, gameState.selectedCell, target)
        applyClickResult(gameState, clickResult)
    }

    fun onUndoClicked() {
        if (_state.value.pendingMove != null) return
        if (moveHistory.isEmpty()) return
        val previousBoard = moveHistory.removeLast()
        val gameState = _state.value.gameState ?: return

        updateGameState(
            gameState.copy(
                board = previousBoard,
                selectedCell = null,
                remainingPegs = previousBoard.countPegs(),
                isGameOver = false,
                canUndo = moveHistory.isNotEmpty()
            )
        )
    }

    fun resetGame() {
        val gameState = _state.value.gameState ?: return
        if (gameState.gameMode == GameMode.CHALLENGE && gameState.levelNumber != null) {
            startChallengeLevel(gameState.levelNumber)
        } else {
            startClassicGame(gameState.boardType)
        }
    }

    fun clearPendingMove() {
        val deferred = pendingGameState
        pendingGameState = null
        if (deferred != null) {
            _state.value = _state.value.copy(gameState = deferred, pendingMove = null)
        } else {
            _state.value = _state.value.copy(pendingMove = null)
        }
    }

    fun clearPendingInvalidMove() {
        _state.value = _state.value.copy(pendingInvalidMove = false)
    }

    fun pauseTimer() {
        val mark = timerStartMark ?: return
        timerBaseMillis += mark.elapsedNow().inWholeMilliseconds
        timerStartMark = null
        timerJob?.cancel()
        timerJob = null
    }

    fun resumeTimer() {
        val gameState = _state.value.gameState ?: return
        if (!gameState.isGameOver) startTimer()
    }

    fun onCleared() {
        scope.cancel()
    }

    private fun applyClickResult(gameState: GameState, result: CellClickResult) {
        when (val event = result.event) {
            is CellClickEvent.Selected -> applySelectionChange(gameState, result)
            is CellClickEvent.Deselected -> applySelectionChange(gameState, result)
            is CellClickEvent.Moved -> applyMoveResult(gameState, result, event)
            is CellClickEvent.Invalid -> {
                _events.tryEmit(GameEvent.InvalidMove)
                _state.value = _state.value.copy(pendingInvalidMove = true)
            }
        }
    }

    private fun applySelectionChange(gameState: GameState, result: CellClickResult) {
        updateGameState(
            gameState.copy(
                board = result.board,
                selectedCell = result.selectedCell
            )
        )
    }

    private fun applyMoveResult(gameState: GameState, result: CellClickResult, event: CellClickEvent.Moved) {
        result.boardSnapshot?.let { moveHistory.add(it) }

        _events.tryEmit(GameEvent.PegMoved(event.move.from, event.move.to, event.move.captured))

        pendingGameState = gameState.copy(
            board = result.board,
            selectedCell = null,
            remainingPegs = result.remainingPegs,
            isGameOver = result.isGameOver,
            canUndo = true
        )

        _state.value = _state.value.copy(
            gameState = gameState.copy(selectedCell = null),
            pendingMove = event.move
        )

        if (result.isGameOver) {
            stopTimer()
            saveGameResult(pendingGameState!!, result.remainingPegs)
        }
    }

    private fun saveGameResult(gameState: GameState, remainingPegs: Int) {
        val scoreText = "$remainingPegs$SCORE_SEPARATOR${gameState.totalPegs}"
        val levelNumber = gameState.levelNumber

        scope.launch {
            try {
                if (gameState.gameMode == GameMode.CHALLENGE && levelNumber != null) {
                    saveChallengeResult(levelNumber, remainingPegs, gameState.elapsedTimeMillis, scoreText)
                } else {
                    saveClassicResult(gameState.boardType, remainingPegs, gameState.elapsedTimeMillis, scoreText)
                }
            } catch (e: Exception) {
                _events.tryEmit(GameEvent.GameOver(scoreText))
            }
        }
    }

    private suspend fun saveChallengeResult(
        levelNumber: Int,
        remainingPegs: Int,
        elapsedTimeMillis: Long,
        scoreText: String
    ) {
        val stars = saveLevelProgressUseCase(levelNumber, remainingPegs, elapsedTimeMillis)
        _events.emit(GameEvent.GameOver(scoreText, stars))
    }

    private suspend fun saveClassicResult(
        boardType: BoardType,
        remainingPegs: Int,
        elapsedTimeMillis: Long,
        scoreText: String
    ) {
        saveScoreUseCase(boardType, remainingPegs, elapsedTimeMillis)
        _events.emit(GameEvent.GameOver(scoreText))
    }

    private fun updateGameState(gameState: GameState) {
        _state.value = _state.value.copy(gameState = gameState)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerStartMark = TimeSource.Monotonic.markNow()
        timerJob = scope.launch {
            while (true) {
                delay(TIMER_INTERVAL_MILLIS)
                val gameState = _state.value.gameState ?: break
                if (gameState.isGameOver) break
                val elapsed = timerBaseMillis + (timerStartMark?.elapsedNow()?.inWholeMilliseconds ?: 0L)
                updateGameState(gameState.copy(elapsedTimeMillis = elapsed))
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        timerStartMark = null
        timerBaseMillis = 0L
    }
}
