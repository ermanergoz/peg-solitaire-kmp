import SwiftUI
import Shared

private let swipeBackThreshold: CGFloat = 100
private let sectionSpacing: CGFloat = 24
private let screenPadding: CGFloat = 24
private let cardCornerRadius: CGFloat = 16
private let cardShadowRadius: CGFloat = 4
private let toggleVerticalPadding: CGFloat = 8

struct SettingsView: View {
    let onBack: () -> Void

    @StateObject private var viewModel = SettingsViewModelWrapper()

    var body: some View {
        VStack(spacing: sectionSpacing) {
            Text(strSettings())
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.pink)
                .frame(maxWidth: .infinity, alignment: .leading)

            VStack(spacing: 0) {
                Toggle(soundEffects(), isOn: Binding(
                    get: { viewModel.uiState.soundEnabled },
                    set: { _ in viewModel.toggleSound() }
                ))
                .padding(.vertical, toggleVerticalPadding)

                Toggle(hapticFeedback(), isOn: Binding(
                    get: { viewModel.uiState.hapticEnabled },
                    set: { _ in viewModel.toggleHaptic() }
                ))
                .padding(.vertical, toggleVerticalPadding)
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(cardCornerRadius)
            .shadow(radius: cardShadowRadius)

            Button(action: { viewModel.requestResetScores() }) {
                Text(resetAllScores())
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.red)

            Spacer()
        }
        .padding(screenPadding)
        .alert(resetAllScores(), isPresented: Binding(
            get: { viewModel.uiState.showResetConfirmation },
            set: { if !$0 { viewModel.dismissResetDialog() } }
        )) {
            Button(strCancel(), role: .cancel) { viewModel.dismissResetDialog() }
            Button(strReset(), role: .destructive) { viewModel.confirmResetScores() }
        } message: {
            Text(resetAllScoresConfirm())
        }
        .gesture(
            DragGesture(minimumDistance: swipeBackThreshold)
                .onEnded { value in
                    if value.translation.width > swipeBackThreshold {
                        onBack()
                    }
                }
        )
        .onAppear {
            viewModel.onScoresReset = onBack
        }
    }
}
