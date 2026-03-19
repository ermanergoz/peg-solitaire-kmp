import SwiftUI
import Shared

private let scoreSeparator = " / "

private let badgePurple = Color(red: 0.424, green: 0.388, blue: 1.0)
private let badgeGreen = Color(red: 0.290, green: 0.871, blue: 0.502)
private let badgeBlue = Color(red: 0.376, green: 0.647, blue: 0.980)
private let badgeRed = Color(red: 0.937, green: 0.267, blue: 0.267)
private let badgeGray = Color(red: 0.612, green: 0.639, blue: 0.686)

private let bottomButtonSize: CGFloat = 48
private let pillHPadding: CGFloat = 14
private let pillVPadding: CGFloat = 8
private let barHPadding: CGFloat = 16
private let barVPadding: CGFloat = 12
private let badgeFontSize: CGFloat = 15
private let disabledAlpha: Double = 0.4
private let swipeBackThreshold: CGFloat = 100

private let pauseSymbol = "\u{2016}"
private let playSymbol = "\u{25B6}"

struct GameView: View {
    let boardType: BoardType?
    let levelNumber: Int32?
    let onQuit: () -> Void

    @StateObject private var viewModel = GameViewModelWrapper()
    @State private var gameOverInfo: GameOverInfo? = nil
    @State private var isPaused = false
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
                    isPaused: isPaused,
                    onCellClicked: viewModel.onCellClicked,
                    onUndo: viewModel.undo,
                    onReset: viewModel.reset,
                    onPause: {
                        isPaused.toggle()
                        if isPaused {
                            viewModel.pauseTimer()
                        } else {
                            viewModel.resumeTimer()
                        }
                    },
                    moveAnim: viewModel.lastMoveAnim,
                    onMoveAnimFinished: { viewModel.clearPendingMove() },
                    isShaking: viewModel.isInvalidMove,
                    onShakeFinished: { viewModel.clearPendingInvalidMove() }
                )
            } else {
                ProgressView()
            }
        }
        .gesture(
            DragGesture(minimumDistance: swipeBackThreshold)
                .onEnded { value in
                    if value.translation.width > swipeBackThreshold {
                        onQuit()
                    }
                }
        )
        .onAppear(perform: startGame)
        .onDisappear { viewModel.pauseTimer() }
        .onChange(of: viewModel.lastGameOverScore) { _, newValue in
            gameOverInfo = newValue
        }
        .onChange(of: scenePhase) { _, newPhase in
            if newPhase == .active {
                if !isPaused { viewModel.resumeTimer() }
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
                    isPaused = false
                    viewModel.reset()
                },
                onNextLevel: nextLevelNumber.map { nextLevel in
                    {
                        gameOverInfo = nil
                        isPaused = false
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
    let isPaused: Bool
    let onCellClicked: (Int32, Int32) -> Void
    let onUndo: () -> Void
    let onReset: () -> Void
    let onPause: () -> Void
    var moveAnim: MoveAnimData? = nil
    var onMoveAnimFinished: (() -> Void)? = nil
    var isShaking: Bool = false
    var onShakeFinished: (() -> Void)? = nil

    @Environment(\.colorScheme) private var colorScheme

    private var scoreText: String {
        "\(state.remainingPegs)\(scoreSeparator)\(state.totalPegs)"
    }

    private var timeText: String {
        let totalSeconds = state.elapsedTimeMillis / 1000
        return String(format: "%02d:%02d", totalSeconds / 60, totalSeconds % 60)
    }

    var body: some View {
        ZStack {
            VStack {
                GameTopBarView(scoreText: scoreText, timeText: timeText)

                BoardView(
                    board: state.board,
                    onCellClicked: onCellClicked,
                    moveAnim: moveAnim,
                    onMoveAnimFinished: onMoveAnimFinished,
                    isShaking: isShaking,
                    onShakeFinished: onShakeFinished
                )
                .padding()

                GameBottomBarView(
                    canUndo: state.canUndo,
                    isPaused: isPaused,
                    onUndo: onUndo,
                    onPause: onPause,
                    onReset: onReset
                )
            }

            if isPaused {
                PauseOverlayView(onResume: onPause)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(boardBackgroundColor(boardType: state.boardType, colorScheme: colorScheme))
    }
}

private struct GameTopBarView: View {
    let scoreText: String
    let timeText: String

    var body: some View {
        HStack(spacing: 10) {
            PillBadge(text: scoreText, color: badgeGreen)
            PillBadge(text: timeText, color: badgeBlue)
        }
        .padding(.horizontal, barHPadding)
        .padding(.vertical, barVPadding)
    }
}

private let overlayAlpha: Double = 0.5
private let playIconSize: CGFloat = 80

private struct PauseOverlayView: View {
    let onResume: () -> Void

    var body: some View {
        Color.black.opacity(overlayAlpha)
            .ignoresSafeArea()
            .onTapGesture(perform: onResume)
            .overlay {
                Canvas { context, size in
                    let path = Path { p in
                        p.move(to: CGPoint(x: size.width * 0.2, y: 0))
                        p.addLine(to: CGPoint(x: size.width, y: size.height / 2))
                        p.addLine(to: CGPoint(x: size.width * 0.2, y: size.height))
                        p.closeSubpath()
                    }
                    context.fill(path, with: .color(.white.opacity(0.9)))
                }
                .frame(width: playIconSize, height: playIconSize)
            }
    }
}

private struct GameBottomBarView: View {
    let canUndo: Bool
    let isPaused: Bool
    let onUndo: () -> Void
    let onPause: () -> Void
    let onReset: () -> Void

    var body: some View {
        HStack(spacing: 16) {
            BottomCircleButton(
                symbol: "\u{21A9}",
                color: canUndo ? badgeGray : badgeGray.opacity(disabledAlpha),
                action: onUndo
            )
            .disabled(!canUndo)

            BottomCircleButton(
                symbol: isPaused ? playSymbol : pauseSymbol,
                color: badgePurple,
                action: onPause
            )

            BottomCircleButton(symbol: "\u{21BB}", color: badgeRed, action: onReset)
        }
        .padding(.horizontal, barHPadding)
        .padding(.vertical, barVPadding)
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
