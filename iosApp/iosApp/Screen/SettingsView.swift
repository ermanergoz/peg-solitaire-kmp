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
            Text("Settings")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.pink)
                .frame(maxWidth: .infinity, alignment: .leading)

            VStack(spacing: 0) {
                Toggle("Sound Effects", isOn: Binding(
                    get: { viewModel.uiState.soundEnabled },
                    set: { _ in viewModel.toggleSound() }
                ))
                .padding(.vertical, toggleVerticalPadding)

                Toggle("Haptic Feedback", isOn: Binding(
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
                Text("Reset All Scores")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.red)

            Spacer()
        }
        .padding(screenPadding)
        .alert("Reset All Scores", isPresented: Binding(
            get: { viewModel.uiState.showResetConfirmation },
            set: { if !$0 { viewModel.dismissResetDialog() } }
        )) {
            Button("Cancel", role: .cancel) { viewModel.dismissResetDialog() }
            Button("Reset", role: .destructive) { viewModel.confirmResetScores() }
        } message: {
            Text("This will permanently delete all your classic mode best scores and challenge mode progress.")
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
