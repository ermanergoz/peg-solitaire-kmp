import SwiftUI
import Shared

struct MenuView: View {
    let onClassicSelected: (BoardType) -> Void
    let onChallengeSelected: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            Text("Peg Solitaire")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.pink)

            ClassicModeCard(onBoardSelected: onClassicSelected)

            MenuCard(title: "Challenge Mode") {
                MenuButton(text: "Browse Levels", color: .purple) {
                    onChallengeSelected()
                }
            }
        }
        .padding(24)
    }
}

private struct ClassicModeCard: View {
    let onBoardSelected: (BoardType) -> Void

    var body: some View {
        MenuCard(title: "Classic Mode") {
            ForEach(BoardType.entries, id: \.name) { boardType in
                MenuButton(
                    text: boardType.name.lowercased().capitalized,
                    color: .pink
                ) {
                    onBoardSelected(boardType)
                }
            }
        }
    }
}

private struct MenuCard<Content: View>: View {
    let title: String
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(spacing: 8) {
            Text(title)
                .font(.headline)
            content()
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(radius: 4)
    }
}

private struct MenuButton: View {
    let text: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .frame(maxWidth: .infinity)
        }
        .buttonStyle(.borderedProminent)
        .tint(color)
    }
}
