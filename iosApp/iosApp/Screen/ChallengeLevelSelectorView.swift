import SwiftUI
import Shared

private let gridColumns = 3
private let gridSpacing: CGFloat = 12
private let cellCornerRadius: CGFloat = 16
private let loadMoreThreshold = 10
private let maxStars = 3
private let lockedOpacity = 0.35
private let levelNumberSize: CGFloat = 20
private let starRowWidth: CGFloat = 36
private let starRowHeight: CGFloat = 14
private let lockIconSize: CGFloat = 20
private let swipeBackThreshold: CGFloat = 100

struct ChallengeLevelSelectorView: View {
    let onLevelSelected: (Int32) -> Void
    let onBack: () -> Void

    @StateObject private var viewModel = ChallengeLevelSelectorViewModelWrapper()
    @Environment(\.colorScheme) private var colorScheme

    private let columns = Array(
        repeating: GridItem(.flexible(), spacing: gridSpacing),
        count: gridColumns
    )

    var body: some View {
        VStack(spacing: 0) {
            LevelSelectorTopBar()

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
                    colorScheme: colorScheme,
                    onLevelSelected: onLevelSelected,
                    onLoadMore: viewModel.loadMoreLevels
                )
            }
        }
        .background(colorScheme == .light ? warmBackground : Color(.systemBackground))
        .gesture(
            DragGesture(minimumDistance: swipeBackThreshold)
                .onEnded { value in
                    if value.translation.width > swipeBackThreshold {
                        onBack()
                    }
                }
        )
        .onAppear { viewModel.loadInitialLevels() }
    }
}

private struct LevelSelectorTopBar: View {
    var body: some View {
        Text("Challenge Levels")
            .font(.title2)
            .fontWeight(.bold)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal)
            .padding(.vertical, 12)
    }
}

private struct LevelGrid: View {
    let levels: [LevelItem]
    let isLoadingMore: Bool
    let columns: [GridItem]
    let colorScheme: ColorScheme
    let onLevelSelected: (Int32) -> Void
    let onLoadMore: () -> Void

    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: gridSpacing) {
                ForEach(levels, id: \.levelNumber) { level in
                    LevelCell(level: level, colorScheme: colorScheme) {
                        onLevelSelected(level.levelNumber)
                    }
                    .onAppear {
                        if level.levelNumber >= (levels.last?.levelNumber ?? 0) - Int32(loadMoreThreshold) {
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
    let colorScheme: ColorScheme
    let onTap: () -> Void

    private var isCompleted: Bool { level.stars > 0 }
    private var cellColor: Color { Color(.secondarySystemBackground) }

    var body: some View {
        Button(action: onTap) {
            GeometryReader { geo in
                let size = geo.size
                ZStack {
                    RoundedRectangle(cornerRadius: cellCornerRadius)
                        .background(
                            isCompleted
                                ? AnyShapeStyle(LinearGradient(
                                    colors: [completedGradientStart, completedGradientEnd],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                ))
                                : AnyShapeStyle(cellColor)
                        )
                        .foregroundColor(.clear)
                        .cornerRadius(cellCornerRadius)
                        .shadow(radius: isCompleted ? 0 : 1)

                    VStack(spacing: 4) {
                        if level.isLocked {
                            Image("ic_lock")
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: lockIconSize, height: lockIconSize)
                                .foregroundColor(.gray)
                        }

                        Text("\(level.levelNumber)")
                            .font(.system(size: levelNumberSize, weight: .bold))
                            .foregroundColor(isCompleted ? .white : .primary)

                        if isCompleted {
                            StarRow(stars: level.stars)
                        }
                    }
                    .frame(width: size.width, height: size.height)
                }
            }
            .aspectRatio(1, contentMode: .fit)
        }
        .disabled(level.isLocked)
        .opacity(level.isLocked ? lockedOpacity : 1.0)
    }
}

private struct StarRow: View {
    let stars: Int32

    var body: some View {
        HStack(spacing: 2) {
            ForEach(0..<maxStars, id: \.self) { i in
                Image(Int32(i) < stars ? "ic_star_filled" : "ic_star_empty")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .foregroundColor(Int32(i) < stars ? starGold : .gray.opacity(0.3))
            }
        }
        .frame(width: starRowWidth, height: starRowHeight)
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
