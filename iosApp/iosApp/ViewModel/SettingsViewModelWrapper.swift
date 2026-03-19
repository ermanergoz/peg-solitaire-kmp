import SwiftUI
import Shared

class SettingsViewModelWrapper: ObservableObject {
    private let viewModel: SettingsViewModel
    private let stateCollector: FlowCollector<SettingsUiState>
    private let eventCollector: FlowCollector<SettingsEvent>

    @Published var uiState = SettingsUiState(
        soundEnabled: true,
        hapticEnabled: true,
        showResetConfirmation: false,
        error: nil
    )

    var onScoresReset: (() -> Void)?

    init() {
        viewModel = KoinHelper().getSettingsViewModel()
        stateCollector = FlowCollector(flow: viewModel.uiState)
        eventCollector = FlowCollector(flow: viewModel.events)

        stateCollector.collect { [weak self] state in
            guard let self = self, let state = state else { return }
            self.uiState = state
        }

        eventCollector.collect { [weak self] event in
            guard let self = self, let event = event else { return }
            if event is SettingsEvent.ScoresReset {
                self.onScoresReset?()
            }
        }
    }

    func toggleSound() { viewModel.toggleSound() }
    func toggleHaptic() { viewModel.toggleHaptic() }
    func requestResetScores() { viewModel.requestResetScores() }
    func confirmResetScores() { viewModel.confirmResetScores() }
    func dismissResetDialog() { viewModel.dismissResetDialog() }

    deinit {
        stateCollector.cancel()
        eventCollector.cancel()
        viewModel.onCleared()
    }
}
