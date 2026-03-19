import SwiftUI
import Shared

private let transitionDuration: Double = 0.25

struct ContentView: View {
    @StateObject private var router = Router()
    @StateObject private var homeViewModel = HomeViewModelWrapper()

    var body: some View {
        NavigationStack {
            screenContent
                .animation(.easeInOut(duration: transitionDuration), value: router.screenId)
        }
    }

    @ViewBuilder
    private var screenContent: some View {
        switch router.currentScreen {
        case .menu:
            MenuView(
                bestScoreFor: { homeViewModel.bestScore(for: $0) },
                onClassicSelected: { boardType in
                    router.currentScreen = .classicGame(boardType: boardType)
                },
                onChallengeSelected: {
                    router.currentScreen = .challengeLevelSelector
                },
                onSettingsClick: {
                    router.currentScreen = .settings
                }
            )
            .onAppear { homeViewModel.loadData() }
            .transition(.opacity)
        case .challengeLevelSelector:
            ChallengeLevelSelectorView(
                onLevelSelected: { level in
                    router.currentScreen = .challengeGame(levelNumber: level)
                },
                onBack: {
                    router.currentScreen = .menu
                }
            )
            .transition(.opacity)
        case .classicGame(let boardType):
            GameView(
                boardType: boardType,
                levelNumber: nil,
                onQuit: { router.currentScreen = .menu }
            )
            .transition(.opacity)
        case .challengeGame(let levelNumber):
            GameView(
                boardType: nil,
                levelNumber: levelNumber,
                onQuit: { router.currentScreen = .challengeLevelSelector }
            )
            .transition(.opacity)
        case .settings:
            SettingsView(
                onBack: { router.currentScreen = .menu }
            )
            .transition(.opacity)
        }
    }
}

class Router: ObservableObject {
    @Published var currentScreen: AppScreen = .menu

    var screenId: String {
        switch currentScreen {
        case .menu: return "menu"
        case .challengeLevelSelector: return "challengeSelector"
        case .classicGame: return "classicGame"
        case .challengeGame: return "challengeGame"
        case .settings: return "settings"
        }
    }
}

enum AppScreen {
    case menu
    case challengeLevelSelector
    case classicGame(boardType: BoardType)
    case challengeGame(levelNumber: Int32)
    case settings
}
