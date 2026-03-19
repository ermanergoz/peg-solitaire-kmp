import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var router = Router()

    var body: some View {
        NavigationStack {
            switch router.currentScreen {
            case .menu:
                MenuView(
                    onClassicSelected: { boardType in
                        router.currentScreen = .classicGame(boardType: boardType)
                    },
                    onChallengeSelected: {
                        router.currentScreen = .challengeLevelSelector
                    }
                )
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
}
