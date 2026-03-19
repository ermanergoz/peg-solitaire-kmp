import SwiftUI
import Shared

private let scoreSeparator = " / "

struct GameView: View {
    let boardType: BoardType?
    let levelNumber: Int32?
    let onQuit: () -> Void

    @StateObject private var viewModel = GameViewModelWrapper()
    @State private var gameOverInfo: GameOverInfo? = nil
    @Environment(\.scenePhase) private var scenePhase

    private var nextLevelNumber: Int32? {
        if let level = levelNumber, gameOverInfo?.stars != nil {
            return level + 1
        }
        return nil
    }

    private var isGameOverPresented: Binding<Bool> {
        Binding(
            get: { gameOverInfo != nil },
            set: { if !$0 { gameOverInfo = nil } }
        )
    }

    var body: some View {
        VStack {
            if let error = viewModel.error {
                ErrorContentView(
                    message: error,
                    onRetry: viewModel.reset,
                    onQuit: onQuit
                )
            } else if let state = viewModel.gameState {
                GameContentView(
                    state: state,
                    onCellClicked: viewModel.onCellClicked,
                    onUndo: viewModel.undo,
                    onReset: viewModel.reset
                )
            } else {
                ProgressView()
            }
        }
        .onAppear(perform: startGame)
        .onDisappear { viewModel.pauseTimer() }
        .onChange(of: viewModel.lastGameOverScore) { _, newValue in
            gameOverInfo = newValue
        }
        .onChange(of: scenePhase) { _, newPhase in
            if newPhase == .active {
                viewModel.resumeTimer()
            } else {
                viewModel.pauseTimer()
            }
        }
        .alert("Game Over", isPresented: isGameOverPresented) {
            GameOverAlertButtons(
                onQuit: {
                    gameOverInfo = nil
                    onQuit()
                },
                onRestart: {
                    gameOverInfo = nil
                    viewModel.reset()
                },
                onNextLevel: nextLevelNumber.map { nextLevel in
                    {
                        gameOverInfo = nil
                        viewModel.startChallengeLevel(levelNumber: nextLevel)
                    }
                }
            )
        } message: {
            GameOverAlertMessage(gameOverInfo: gameOverInfo)
        }
    }

    private func startGame() {
        if let boardType = boardType {
            viewModel.startClassicGame(boardType: boardType)
        } else if let level = levelNumber {
            viewModel.startChallengeLevel(levelNumber: level)
        }
    }
}

private struct ErrorContentView: View {
    let message: String
    let onRetry: () -> Void
    let onQuit: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text(message)
                .foregroundColor(.red)
            HStack(spacing: 16) {
                Button("Quit", action: onQuit)
                Button("Retry", action: onRetry)
            }
        }
    }
}

private struct GameContentView: View {
    let state: GameState
    let onCellClicked: (Int32, Int32) -> Void
    let onUndo: () -> Void
    let onReset: () -> Void

    @Environment(\.colorScheme) private var colorScheme

    private var scoreText: String {
        "\(state.remainingPegs)\(scoreSeparator)\(state.totalPegs)"
    }

    private var timeText: String {
        let totalSeconds = state.elapsedTimeMillis / 1000
        return String(format: "%02d:%02d", totalSeconds / 60, totalSeconds % 60)
    }

    var body: some View {
        VStack {
            GameTopBarView(
                scoreText: scoreText,
                timeText: timeText,
                canUndo: state.canUndo,
                onUndo: onUndo,
                onReset: onReset
            )

            BoardView(board: state.board, onCellClicked: onCellClicked)
                .padding()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(boardBackgroundColor(boardType: state.boardType, colorScheme: colorScheme))
    }
}

private struct GameTopBarView: View {
    let scoreText: String
    let timeText: String
    let canUndo: Bool
    let onUndo: () -> Void
    let onReset: () -> Void

    var body: some View {
        HStack {
            Button(action: onUndo) {
                Text("\u{21A9}").font(.title2)
            }
            .disabled(!canUndo)

            Spacer()
            Text(timeText).font(.headline)
            Spacer()
            Text(scoreText)
                .font(.headline)
                .foregroundColor(.pink)
            Spacer()

            Button(action: onReset) {
                Text("\u{21BB}").font(.title2)
            }
        }
        .padding(.horizontal)
    }
}

private struct GameOverAlertButtons: View {
    let onQuit: () -> Void
    let onRestart: () -> Void
    let onNextLevel: (() -> Void)?

    var body: some View {
        Button("Quit", action: onQuit)
        Button("Restart", action: onRestart)
        if let onNextLevel = onNextLevel {
            Button("Next", action: onNextLevel)
        }
    }
}

private struct GameOverAlertMessage: View {
    let gameOverInfo: GameOverInfo?

    var body: some View {
        if let info = gameOverInfo {
            let starsText = info.stars.map { stars in
                String(repeating: "\u{2605}", count: Int(truncating: stars))
            } ?? ""
            Text("\(info.scoreText) \(starsText)")
        }
    }
}

struct GameOverInfo: Equatable {
    let scoreText: String
    let stars: NSNumber?
}
