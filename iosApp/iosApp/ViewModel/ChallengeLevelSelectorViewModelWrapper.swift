import SwiftUI
import Shared

class ChallengeLevelSelectorViewModelWrapper: ObservableObject {
    private let viewModel: ChallengeLevelSelectorViewModel
    private let stateCollector: FlowCollector<ChallengeLevelSelectorUiState>

    @Published var uiState = ChallengeLevelSelectorUiState(
        levels: [],
        isLoading: true,
        isLoadingMore: false,
        error: nil
    )

    init() {
        viewModel = KoinHelper().getChallengeLevelSelectorViewModel()
        stateCollector = FlowCollector(flow: viewModel.uiState)

        stateCollector.collect { [weak self] state in
            guard let self = self, let state = state else { return }
            self.uiState = state
        }
    }

    func loadInitialLevels() {
        viewModel.loadInitialLevels()
    }

    func loadMoreLevels() {
        viewModel.loadMoreLevels()
    }

    deinit {
        stateCollector.cancel()
        viewModel.onCleared()
    }
}
