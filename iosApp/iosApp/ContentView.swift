import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var router = Router()
    @StateObject private var homeViewModel = HomeViewModelWrapper()

    var body: some View {
        NavigationStack {
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
            case .challengeLevelSelector:
                ChallengeLevelSelectorView(
                    onLevelSelected: { level in
                        router.currentScreen = .challengeGame(levelNumber: level)
                    },
                    onBack: {
                        router.currentScreen = .menu
                    }
                )
            case .classicGame(let boardType):
                GameView(
                    boardType: boardType,
                    levelNumber: nil,
                    onQuit: { router.currentScreen = .menu }
                )
            case .challengeGame(let levelNumber):
                GameView(
                    boardType: nil,
                    levelNumber: levelNumber,
                    onQuit: { router.currentScreen = .menu }
                )
            case .settings:
                SettingsView(
                    onBack: { router.currentScreen = .menu }
                )
            }
        }
    }
}

class Router: ObservableObject {
    @Published var currentScreen: AppScreen = .menu
}

enum AppScreen {
    case menu
    case challengeLevelSelector
    case classicGame(boardType: BoardType)
    case challengeGame(levelNumber: Int32)
    case settings
}
