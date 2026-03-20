import SwiftUI
import Shared

private let cardCornerRadius: CGFloat = 16
private let heroCornerRadius: CGFloat = 20
private let thumbnailSize: CGFloat = 36
private let thumbnailCornerRadius: CGFloat = 10
private let settingsButtonSize: CGFloat = 36
private let settingsIconSize: CGFloat = 20
private let settingsCornerRadius: CGFloat = 10
private let boardNameFontSize: CGFloat = 14
private let scoreFontSize: CGFloat = 11
private let levelDotSize: CGFloat = 28
private let playButtonCornerRadius: CGFloat = 12
private let notPlayedText = "Not played"
private let scoreMiddleDot = " \u{00B7} "
private let heroLabelAlpha = 0.7
private let levelDotsBeforeCount = 3
private let levelDotsAfterCount = 2
private let millisPerSecond: Int64 = 1000
private let secondsPerMinute: Int64 = 60

struct MenuView: View {
    let bestScoreFor: (BoardType) -> GameScore?
    let onClassicSelected: (BoardType) -> Void
    let onChallengeSelected: () -> Void
    let onSettingsClick: () -> Void
    let currentChallengeLevel: Int32

    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TitleRow(onSettingsClick: onSettingsClick)
                    .padding(.bottom, 24)

                ChallengeHeroCard(
                    currentChallengeLevel: currentChallengeLevel,
                    onSelected: onChallengeSelected
                )
                .padding(.bottom, 28)

                ClassicModeSection(
                    bestScoreFor: bestScoreFor,
                    onClassicSelected: onClassicSelected
                )
            }
            .padding(24)
        }
        .background(colorScheme == .dark ? Color(.systemBackground) : warmBackground)
        .ignoresSafeArea(edges: .bottom)
    }
}

private struct TitleRow: View {
    let onSettingsClick: () -> Void

    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        HStack(alignment: .center) {
            Text("Peg Solitaire")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.pink)

            Spacer()

            Button(action: onSettingsClick) {
                Image("ic_settings")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: settingsIconSize, height: settingsIconSize)
                    .foregroundColor(.pink)
            }
            .frame(width: settingsButtonSize, height: settingsButtonSize)
            .background(colorScheme == .dark ? Color(.secondarySystemBackground) : Color.white)
            .cornerRadius(settingsCornerRadius)
            .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
        }
    }
}

private struct ChallengeHeroCard: View {
    let currentChallengeLevel: Int32
    let onSelected: () -> Void

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: heroCornerRadius)
                .fill(
                    LinearGradient(
                        colors: [completedGradientStart, completedGradientEnd],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )

            VStack(alignment: .leading, spacing: 16) {
                Text("CHALLENGE MODE")
                    .font(.caption)
                    .fontWeight(.semibold)
                    .foregroundColor(.white.opacity(heroLabelAlpha))
                    .kerning(1.2)

                Text("Level \(currentChallengeLevel)")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.white)

                Button(action: onSelected) {
                    HStack {
                        Spacer()
                        Text("Play")
                            .font(.headline)
                            .foregroundColor(completedGradientStart)
                        Spacer()
                    }
                    .padding(.vertical, 12)
                    .background(Color.white)
                    .cornerRadius(playButtonCornerRadius)
                }
                .buttonStyle(.plain)

                LevelDotsRow(currentLevel: currentChallengeLevel)

                Button(action: onSelected) {
                    HStack {
                        Spacer()
                        Text("Browse All Levels →")
                            .font(.subheadline)
                            .foregroundColor(.white.opacity(heroLabelAlpha))
                        Spacer()
                    }
                }
                .buttonStyle(.plain)
            }
            .padding(20)
        }
    }
}

private struct LevelDotsRow: View {
    let currentLevel: Int32

    var body: some View {
        HStack(spacing: 8) {
            Spacer()
            ForEach(dotLevels, id: \.self) { level in
                LevelDot(level: level, isCurrent: level == currentLevel)
            }
            Spacer()
        }
    }

    private var dotLevels: [Int32] {
        let start = max(1, currentLevel - Int32(levelDotsBeforeCount))
        let end = currentLevel + Int32(levelDotsAfterCount)
        return Array(start...end)
    }
}

private struct LevelDot: View {
    let level: Int32
    let isCurrent: Bool

    var body: some View {
        ZStack {
            Circle()
                .fill(isCurrent ? Color.white : Color.white.opacity(0.3))
                .frame(width: levelDotSize, height: levelDotSize)

            Text("\(level)")
                .font(.system(size: 11, weight: isCurrent ? .bold : .regular))
                .foregroundColor(isCurrent ? completedGradientStart : .white)
        }
    }
}

private struct ClassicModeSection: View {
    let bestScoreFor: (BoardType) -> GameScore?
    let onClassicSelected: (BoardType) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("CLASSIC MODE")
                .font(.caption)
                .fontWeight(.semibold)
                .foregroundColor(.secondary)
                .kerning(1.2)
                .padding(.bottom, 12)

            ForEach(BoardType.entries, id: \.name) { boardType in
                ClassicBoardCard(
                    boardType: boardType,
                    score: bestScoreFor(boardType),
                    onSelected: { onClassicSelected(boardType) }
                )
            }
        }
    }
}

private struct ClassicBoardCard: View {
    let boardType: BoardType
    let score: GameScore?
    let onSelected: () -> Void

    var body: some View {
        Button(action: onSelected) {
            HStack(spacing: 12) {
                BoardShapeThumbnail(boardType: boardType)

                VStack(alignment: .leading, spacing: 2) {
                    Text(boardType.name.lowercased().capitalized)
                        .font(.system(size: boardNameFontSize, weight: .medium))
                        .foregroundColor(.primary)

                    Text(scoreText)
                        .font(.system(size: scoreFontSize))
                        .foregroundColor(.secondary)
                }

                Spacer()
            }
            .padding(12)
            .background(Color(.systemBackground))
            .cornerRadius(cardCornerRadius)
            .shadow(color: .black.opacity(0.08), radius: 2, x: 0, y: 1)
        }
        .buttonStyle(.plain)
        .padding(.bottom, 8)
    }

    private var scoreText: String {
        guard let score else { return notPlayedText }
        let totalSeconds = score.elapsedTimeMillis / millisPerSecond
        let minutes = totalSeconds / secondsPerMinute
        let seconds = totalSeconds % secondsPerMinute
        return "\(score.remainingPegs) left\(scoreMiddleDot)\(String(format: "%02d:%02d", minutes, seconds))"
    }
}

private struct BoardShapeThumbnail: View {
    let boardType: BoardType

    var body: some View {
        Canvas { context, size in
            drawBoardShape(context: context, size: size, boardType: boardType)
        }
        .frame(width: thumbnailSize, height: thumbnailSize)
        .background(thumbnailBackground(boardType: boardType))
        .cornerRadius(thumbnailCornerRadius)
    }

    private func thumbnailBackground(boardType: BoardType) -> Color {
        switch boardType {
        case .english:
            return Color(red: 0.863, green: 0.894, blue: 0.973)
        case .french:
            return Color(red: 0.961, green: 0.855, blue: 0.878)
        case .german:
            return Color(red: 0.961, green: 0.929, blue: 0.855)
        case .asymmetric:
            return Color(red: 0.855, green: 0.941, blue: 0.910)
        case .diamond:
            return Color(red: 0.941, green: 0.855, blue: 0.918)
        default:
            return Color(red: 0.863, green: 0.894, blue: 0.973)
        }
    }

    private func shapeFillColor(boardType: BoardType) -> Color {
        switch boardType {
        case .english:
            return Color(red: 0.620, green: 0.670, blue: 0.820)
        case .french:
            return Color(red: 0.780, green: 0.620, blue: 0.650)
        case .german:
            return Color(red: 0.780, green: 0.740, blue: 0.620)
        case .asymmetric:
            return Color(red: 0.600, green: 0.760, blue: 0.700)
        case .diamond:
            return Color(red: 0.750, green: 0.620, blue: 0.760)
        default:
            return Color(red: 0.620, green: 0.670, blue: 0.820)
        }
    }

    private func drawBoardShape(context: GraphicsContext, size: CGSize, boardType: BoardType) {
        let fill = shapeFillColor(boardType: boardType)
        switch boardType {
        case .english:
            drawEnglishShape(context: context, size: size, fill: fill)
        case .french:
            drawFrenchShape(context: context, size: size, fill: fill)
        case .german:
            drawGermanShape(context: context, size: size, fill: fill)
        case .asymmetric:
            drawAsymmetricShape(context: context, size: size, fill: fill)
        case .diamond:
            drawDiamondShape(context: context, size: size, fill: fill)
        default:
            drawEnglishShape(context: context, size: size, fill: fill)
        }
    }

    // English 7x7: rows [2,7,7,7,7,7,2] with column offsets [2,0,0,0,0,0,2]
    // Simplified as: top arm (3 wide, 2 tall), middle band (7 wide, 3 tall), bottom arm (3 wide, 2 tall)
    private func drawEnglishShape(context: GraphicsContext, size: CGSize, fill: Color) {
        let rows: [(colsWide: Int, colStart: Int)] = [
            (3, 2), (3, 2),
            (7, 0), (7, 0), (7, 0),
            (3, 2), (3, 2)
        ]
        drawGridShape(context: context, size: size, fill: fill, rows: rows, totalCols: 7)
    }

    // French 7x7: 3,5,7,7,7,5,3
    private func drawFrenchShape(context: GraphicsContext, size: CGSize, fill: Color) {
        let rows: [(colsWide: Int, colStart: Int)] = [
            (3, 2), (5, 1),
            (7, 0), (7, 0), (7, 0),
            (5, 1), (3, 2)
        ]
        drawGridShape(context: context, size: size, fill: fill, rows: rows, totalCols: 7)
    }

    // German 9x9: top 3 rows (3 wide), middle 3 rows (9 wide), bottom 3 rows (3 wide)
    private func drawGermanShape(context: GraphicsContext, size: CGSize, fill: Color) {
        let rows: [(colsWide: Int, colStart: Int)] = [
            (3, 3), (3, 3), (3, 3),
            (9, 0), (9, 0), (9, 0),
            (3, 3), (3, 3), (3, 3)
        ]
        drawGridShape(context: context, size: size, fill: fill, rows: rows, totalCols: 9)
    }

    // Asymmetric 8x8: top 2 rows (3 wide at col 3), middle 3 rows (8 wide), bottom 3 rows (3 wide at col 3)
    private func drawAsymmetricShape(context: GraphicsContext, size: CGSize, fill: Color) {
        let rows: [(colsWide: Int, colStart: Int)] = [
            (3, 3), (3, 3),
            (8, 0), (8, 0), (8, 0),
            (3, 3), (3, 3), (3, 3)
        ]
        drawGridShape(context: context, size: size, fill: fill, rows: rows, totalCols: 8)
    }

    // Diamond 9x9: 1,3,5,7,9,7,5,3,1
    private func drawDiamondShape(context: GraphicsContext, size: CGSize, fill: Color) {
        let widths = [1, 3, 5, 7, 9, 7, 5, 3, 1]
        let rows: [(colsWide: Int, colStart: Int)] = widths.map { w in
            (w, (9 - w) / 2)
        }
        drawGridShape(context: context, size: size, fill: fill, rows: rows, totalCols: 9)
    }

    private func drawGridShape(
        context: GraphicsContext,
        size: CGSize,
        fill: Color,
        rows: [(colsWide: Int, colStart: Int)],
        totalCols: Int
    ) {
        let padding: CGFloat = 3
        let availableWidth = size.width - padding * 2
        let availableHeight = size.height - padding * 2
        let cellW = availableWidth / CGFloat(totalCols)
        let cellH = availableHeight / CGFloat(rows.count)

        var path = Path()
        for (rowIndex, row) in rows.enumerated() {
            let y = padding + CGFloat(rowIndex) * cellH
            let x = padding + CGFloat(row.colStart) * cellW
            let w = CGFloat(row.colsWide) * cellW
            path.addRect(CGRect(x: x, y: y, width: w, height: cellH))
        }
        context.fill(path, with: .color(fill))
    }
}
