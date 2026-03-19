import SwiftUI
import Shared

class GameViewModelWrapper: ObservableObject {
    private let viewModel: GameViewModel
    private let stateCollector: FlowCollector<GameUiState>
    private let eventCollector: FlowCollector<GameEvent>

    @Published var gameState: GameState? = nil
    @Published var isLoading: Bool = false
    @Published var error: String? = nil
    @Published var lastGameOverScore: GameOverInfo? = nil
    @Published var lastMoveAnim: MoveAnimData? = nil
    @Published var isInvalidMove: Bool = false

    init() {
        viewModel = KoinHelper().getGameViewModel()
        stateCollector = FlowCollector(flow: viewModel.state)
        eventCollector = FlowCollector(flow: viewModel.events)

        stateCollector.collect { [weak self] uiState in
            guard let self = self, let uiState = uiState else { return }
            self.gameState = uiState.gameState
            self.isLoading = uiState.isLoading
            self.error = uiState.error

            if let move = uiState.pendingMove {
                let newAnim = MoveAnimData(
                    fromRow: move.from.row,
                    fromCol: move.from.col,
                    toRow: move.to.row,
                    toCol: move.to.col,
                    capturedRow: move.captured.row,
                    capturedCol: move.captured.col
                )
                if self.lastMoveAnim != newAnim {
                    self.lastMoveAnim = newAnim
                }
            } else if self.lastMoveAnim != nil {
                self.lastMoveAnim = nil
            }

            if uiState.pendingInvalidMove != self.isInvalidMove {
                self.isInvalidMove = uiState.pendingInvalidMove
            }
        }

        eventCollector.collect { [weak self] event in
            guard let self = self, let event = event else { return }
            if let gameOver = event as? GameEvent.GameOver {
                self.lastGameOverScore = GameOverInfo(
                    scoreText: gameOver.scoreText,
                    stars: gameOver.stars as NSNumber?
                )
            }
        }
    }

    func startClassicGame(boardType: BoardType) {
        viewModel.startClassicGame(boardType: boardType)
    }

    func startChallengeLevel(levelNumber: Int32) {
        viewModel.startChallengeLevel(levelNumber: levelNumber)
    }

    func onCellClicked(row: Int32, col: Int32) {
        viewModel.onCellClicked(row: row, col: col)
    }

    func undo() {
        viewModel.onUndoClicked()
    }

    func reset() {
        lastMoveAnim = nil
        isInvalidMove = false
        viewModel.resetGame()
        lastGameOverScore = nil
    }

    func clearPendingMove() {
        lastMoveAnim = nil
        viewModel.clearPendingMove()
    }

    func clearPendingInvalidMove() {
        isInvalidMove = false
        viewModel.clearPendingInvalidMove()
    }

    func pauseTimer() {
        viewModel.pauseTimer()
    }

    func resumeTimer() {
        viewModel.resumeTimer()
    }

    deinit {
        stateCollector.cancel()
        eventCollector.cancel()
        viewModel.onCleared()
    }
}
