import SwiftUI
import Shared

private let scoreSeparator = " / "

private let badgePurple = Color(red: 0.424, green: 0.388, blue: 1.0)
private let badgeGreen = Color(red: 0.290, green: 0.871, blue: 0.502)
private let badgeBlue = Color(red: 0.376, green: 0.647, blue: 0.980)
private let badgeRed = Color(red: 0.937, green: 0.267, blue: 0.267)
private let badgeGray = Color(red: 0.612, green: 0.639, blue: 0.686)

private let iconSize: CGFloat = 40
private let bottomButtonSize: CGFloat = 48
private let pillCornerRadius: CGFloat = 20
private let pillHPadding: CGFloat = 14
private let pillVPadding: CGFloat = 8
private let barHPadding: CGFloat = 16
private let barVPadding: CGFloat = 12
private let badgeFontSize: CGFloat = 15
private let disabledAlpha: Double = 0.4

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
                    onReset: viewModel.reset,
                    onBack: onQuit
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
    let onBack: () -> Void

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
                onBack: onBack
            )

            BoardView(board: state.board, onCellClicked: onCellClicked)
                .padding()

            GameBottomBarView(
                canUndo: state.canUndo,
                onUndo: onUndo,
                onReset: onReset
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(boardBackgroundColor(boardType: state.boardType, colorScheme: colorScheme))
    }
}

private struct GameTopBarView: View {
    let scoreText: String
    let timeText: String
    let onBack: () -> Void

    var body: some View {
        HStack(spacing: 10) {
            CircleIconButton(symbol: "\u{2190}", color: badgePurple, action: onBack)
            PillBadge(text: scoreText, color: badgeGreen)
            PillBadge(text: timeText, color: badgeBlue)
            Spacer()
        }
        .padding(.horizontal, barHPadding)
        .padding(.vertical, barVPadding)
    }
}

private struct GameBottomBarView: View {
    let canUndo: Bool
    let onUndo: () -> Void
    let onReset: () -> Void

    var body: some View {
        HStack(spacing: 16) {
            BottomCircleButton(
                symbol: "\u{21A9}",
                color: canUndo ? badgeGray : badgeGray.opacity(disabledAlpha),
                action: onUndo
            )
            .disabled(!canUndo)

            BottomCircleButton(symbol: "\u{21BB}", color: badgeRed, action: onReset)
        }
        .padding(.horizontal, barHPadding)
        .padding(.vertical, barVPadding)
    }
}

private struct CircleIconButton: View {
    let symbol: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(symbol)
                .font(.system(size: badgeFontSize, weight: .bold))
                .foregroundColor(.white)
                .frame(width: iconSize, height: iconSize)
                .background(color)
                .clipShape(Circle())
        }
    }
}

private struct PillBadge: View {
    let text: String
    let color: Color

    var body: some View {
        Text(text)
            .font(.system(size: badgeFontSize, weight: .bold))
            .foregroundColor(.white)
            .padding(.horizontal, pillHPadding)
            .padding(.vertical, pillVPadding)
            .background(color)
            .clipShape(Capsule())
    }
}

private struct BottomCircleButton: View {
    let symbol: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(symbol)
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(.white)
                .frame(width: bottomButtonSize, height: bottomButtonSize)
                .background(color)
                .clipShape(Circle())
        }
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
