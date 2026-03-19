import SwiftUI
import Shared

class HomeViewModelWrapper: ObservableObject {
    private let viewModel: HomeViewModel
    private let stateCollector: FlowCollector<HomeUiState>

    @Published var uiState = HomeUiState(
        bestScores: [:],
        hasOngoingChallenge: false,
        currentChallengeLevel: 1,
        isLoading: true,
        error: nil
    )

    init() {
        viewModel = KoinHelper().getHomeViewModel()
        stateCollector = FlowCollector(flow: viewModel.uiState)

        stateCollector.collect { [weak self] state in
            guard let self = self, let state = state else { return }
            self.uiState = state
        }
    }

    func bestScore(for boardType: BoardType) -> GameScore? {
        uiState.bestScores[boardType] as? GameScore
    }

    func loadData() {
        viewModel.loadData()
    }

    deinit {
        stateCollector.cancel()
        viewModel.onCleared()
    }
}
