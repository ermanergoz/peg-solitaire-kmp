import SwiftUI
import Shared

private let gridColumns = 4
private let gridSpacing: CGFloat = 12
private let cellCornerRadius: CGFloat = 12
private let loadMoreThreshold = 10
private let maxStars = 3
private let lockedOpacity = 0.4
private let starSize: CGFloat = 14

struct ChallengeLevelSelectorView: View {
    let onLevelSelected: (Int32) -> Void
    let onBack: () -> Void

    @StateObject private var viewModel = ChallengeLevelSelectorViewModelWrapper()

    private let columns = Array(
        repeating: GridItem(.flexible(), spacing: gridSpacing),
        count: gridColumns
    )

    var body: some View {
        VStack(spacing: 0) {
            LevelSelectorTopBar(onBack: onBack)

            if let error = viewModel.uiState.error, viewModel.uiState.levels.isEmpty {
                Spacer()
                ErrorContent(message: error, onRetry: viewModel.loadInitialLevels)
                Spacer()
            } else if viewModel.uiState.isLoading {
                Spacer()
                ProgressView()
                Spacer()
            } else {
                LevelGrid(
                    levels: viewModel.uiState.levels,
                    isLoadingMore: viewModel.uiState.isLoadingMore,
                    columns: columns,
                    onLevelSelected: onLevelSelected,
                    onLoadMore: viewModel.loadMoreLevels
                )
            }
        }
        .onAppear { viewModel.loadInitialLevels() }
    }
}

private struct LevelSelectorTopBar: View {
    let onBack: () -> Void

    var body: some View {
        HStack {
            Button(action: onBack) {
                Image(systemName: "chevron.left")
                    .font(.title3)
            }
            Text("Challenge Levels")
                .font(.title2)
                .fontWeight(.bold)
            Spacer()
        }
        .padding(.horizontal)
        .padding(.vertical, 12)
    }
}

private struct LevelGrid: View {
    let levels: [LevelItem]
    let isLoadingMore: Bool
    let columns: [GridItem]
    let onLevelSelected: (Int32) -> Void
    let onLoadMore: () -> Void

    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: gridSpacing) {
                ForEach(levels, id: \.levelNumber) { level in
                    LevelCell(level: level) {
                        onLevelSelected(level.levelNumber)
                    }
                    .onAppear {
                        if level.levelNumber >= levels.last!.levelNumber - Int32(loadMoreThreshold) {
                            onLoadMore()
                        }
                    }
                }

                if isLoadingMore {
                    Section {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .padding()
                    }
                }
            }
            .padding(gridSpacing)
        }
    }
}

private struct LevelCell: View {
    let level: LevelItem
    let onTap: () -> Void

    private var backgroundColor: Color {
        if level.isLocked {
            return Color(.systemBackground)
        }
        if level.stars > 0 {
            return .purple.opacity(0.15)
        }
        return Color(.systemBackground)
    }

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: 4) {
                if level.isLocked {
                    Image(systemName: "lock.fill")
                        .font(.system(size: 16))
                }

                Text("\(level.levelNumber)")
                    .font(.headline)
                    .fontWeight(.bold)

                if level.stars > 0 {
                    StarRow(stars: level.stars)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(12)
            .background(backgroundColor)
            .cornerRadius(cellCornerRadius)
            .shadow(radius: 1)
        }
        .disabled(level.isLocked)
        .opacity(level.isLocked ? lockedOpacity : 1.0)
    }
}

private struct StarRow: View {
    let stars: Int32

    var body: some View {
        HStack(spacing: 2) {
            ForEach(0..<Int32(maxStars), id: \.self) { index in
                Image(systemName: index < stars ? "star.fill" : "star")
                    .font(.system(size: starSize))
                    .foregroundColor(index < stars ? .yellow : .gray.opacity(0.3))
            }
        }
    }
}

private struct ErrorContent: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text(message)
                .foregroundColor(.red)
            Button("Retry", action: onRetry)
        }
    }
}
