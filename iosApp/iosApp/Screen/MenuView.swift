import SwiftUI
import Shared

struct MenuView: View {
    let bestScoreFor: (BoardType) -> GameScore?
    let onClassicSelected: (BoardType) -> Void
    let onChallengeSelected: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            Text("Peg Solitaire")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.pink)

            ClassicModeCard(
                bestScoreFor: bestScoreFor,
                onBoardSelected: onClassicSelected
            )

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
    let bestScoreFor: (BoardType) -> GameScore?
    let onBoardSelected: (BoardType) -> Void

    var body: some View {
        MenuCard(title: "Classic Mode") {
            ForEach(BoardType.entries, id: \.name) { boardType in
                let score = bestScoreFor(boardType)
                MenuButton(
                    text: boardType.name.lowercased().capitalized,
                    scoreText: score.map { formatScoreText(score: $0) },
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
    var scoreText: String? = nil
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                Text(text)
                Spacer()
                if let scoreText {
                    Text(scoreText)
                        .font(.caption)
                }
            }
            .frame(maxWidth: .infinity)
        }
        .buttonStyle(.borderedProminent)
        .tint(color)
    }
}

private let millisPerSecond: Int64 = 1000
private let secondsPerMinute: Int64 = 60

private func formatScoreText(score: GameScore) -> String {
    let totalSeconds = score.elapsedTimeMillis / millisPerSecond
    let minutes = totalSeconds / secondsPerMinute
    let seconds = totalSeconds % secondsPerMinute
    return "\(score.remainingPegs) left \u{00B7} \(String(format: "%02d:%02d", minutes, seconds))"
}
