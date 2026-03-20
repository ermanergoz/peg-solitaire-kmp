package com.erman.pegsolitaire.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.erman.pegsolitaire.R
import com.erman.pegsolitaire.domain.model.GameState
import com.erman.pegsolitaire.presentation.GameEvent
import com.erman.pegsolitaire.presentation.GameViewModel
import com.erman.pegsolitaire.presentation.SCORE_SEPARATOR
import com.erman.pegsolitaire.ui.component.BoardCanvas
import com.erman.pegsolitaire.ui.component.GameBottomBar
import com.erman.pegsolitaire.ui.component.GameOverDialog
import com.erman.pegsolitaire.ui.component.GameTopBar
import com.erman.pegsolitaire.ui.component.MoveAnimationData
import com.erman.pegsolitaire.engine.Position
import com.erman.pegsolitaire.ui.theme.boardBackgroundColor

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    onQuit: () -> Unit
) {
    BackHandler { onQuit() }

    val uiState by gameViewModel.state.collectAsState()
    var gameOverEvent by remember { mutableStateOf<GameEvent.GameOver?>(null) }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            if (event is GameEvent.GameOver) gameOverEvent = event
        }
    }

    DisposableEffect(gameViewModel) {
        onDispose { gameViewModel.onCleared() }
    }

    val pendingMove = uiState.pendingMove
    val moveAnimation = pendingMove?.let {
        MoveAnimationData(from = it.from, to = it.to, captured = it.captured)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val gameState = uiState.gameState

        when {
            uiState.error != null -> ErrorContent(
                message = uiState.error!!,
                onRetry = gameViewModel::resetGame,
                onQuit = onQuit
            )
            uiState.isLoading || gameState == null -> LoadingContent()
            else -> GameContent(
                gameState = gameState,
                gameOverEvent = gameOverEvent,
                moveAnimation = moveAnimation,
                onMoveAnimationFinished = gameViewModel::clearPendingMove,
                isShaking = uiState.pendingInvalidMove,
                onShakeFinished = gameViewModel::clearPendingInvalidMove,
                hintPositions = uiState.hintPositions,
                hintsEnabled = uiState.hintsEnabled,
                onCellClicked = gameViewModel::onCellClicked,
                onUndoClicked = gameViewModel::onUndoClicked,
                onResetClicked = gameViewModel::resetGame,
                onPauseClicked = gameViewModel::pauseTimer,
                onResumeClicked = gameViewModel::resumeTimer,
                onHintClicked = gameViewModel::toggleHints,
                onRestart = {
                    gameOverEvent = null
                    gameViewModel.resetGame()
                },
                onQuit = {
                    gameOverEvent = null
                    onQuit()
                },
                onNextLevel = { levelNumber ->
                    gameOverEvent = null
                    gameViewModel.startChallengeLevel(levelNumber)
                }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, onQuit: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = onQuit) { Text("Quit") }
            TextButton(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun GameContent(
    gameState: GameState,
    gameOverEvent: GameEvent.GameOver?,
    moveAnimation: MoveAnimationData?,
    onMoveAnimationFinished: () -> Unit,
    isShaking: Boolean,
    onShakeFinished: () -> Unit,
    hintPositions: Set<Position>,
    hintsEnabled: Boolean,
    onCellClicked: (Int, Int) -> Unit,
    onUndoClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onHintClicked: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit,
    onNextLevel: (Int) -> Unit
) {
    val scoreText = "${gameState.remainingPegs}$SCORE_SEPARATOR${gameState.totalPegs}"
    val isDark = isSystemInDarkTheme()
    val backgroundColor = boardBackgroundColor(gameState.boardType, isDark)
    var isPaused by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameTopBar(
                scoreText = scoreText,
                elapsedTimeMillis = gameState.elapsedTimeMillis
            )

            BoardCanvas(
                board = gameState.board,
                onCellClicked = onCellClicked,
                moveAnimation = moveAnimation,
                onMoveAnimationFinished = onMoveAnimationFinished,
                isShaking = isShaking,
                onShakeFinished = onShakeFinished,
                hintPositions = hintPositions,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            )

            GameBottomBar(
                canUndo = gameState.canUndo,
                isPaused = isPaused,
                hintsEnabled = hintsEnabled,
                onUndoClicked = onUndoClicked,
                onResetClicked = onResetClicked,
                onPauseClicked = {
                    isPaused = !isPaused
                    if (isPaused) onPauseClicked() else onResumeClicked()
                },
                onHintClicked = onHintClicked
            )
        }

        if (isPaused) {
            PauseOverlay(onResume = {
                isPaused = false
                onResumeClicked()
            })
        }
    }

    if (gameOverEvent != null) {
        val nextLevelNumber = gameState.levelNumber?.let { it + 1 }

        GameOverDialog(
            scoreText = gameOverEvent.scoreText,
            stars = gameOverEvent.stars,
            onRestart = onRestart,
            onQuit = onQuit,
            onNextLevel = if (gameOverEvent.stars != null && nextLevelNumber != null) {
                { onNextLevel(nextLevelNumber) }
            } else null
        )
    }
}

private const val OVERLAY_ALPHA = 0.5f
private val PLAY_ICON_SIZE = 80.dp

@Composable
private fun PauseOverlay(onResume: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = OVERLAY_ALPHA))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onResume
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .size(PLAY_ICON_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onResume
                )
        )
    }
}
