import SwiftUI
import Shared

private let pegMargin: CGFloat = 5
private let shadowOffsetX: CGFloat = 1
private let shadowOffsetY: CGFloat = 3
private let shadowOpacity: Double = 0.2
private let glowRadiusFactor: CGFloat = 1.5
private let shineRadiusFactor: CGFloat = 0.35
private let shineOffsetFactor: CGFloat = 0.25
private let shineVerticalOffsetFactor: CGFloat = 0.3
private let shineOpacity: Double = 0.7
private let gradientOffsetFactor: CGFloat = 0.25
private let bodyGradientRadiusFactor: CGFloat = 1.2

private let pegOuterColor = Color(red: 0.91, green: 0.227, blue: 0.365)
private let pegInnerColor = Color(red: 1.0, green: 0.482, blue: 0.584)
private let selectedOuterColor = Color(red: 1.0, green: 0.843, blue: 0.0)
private let selectedInnerColor = Color(red: 1.0, green: 0.94, blue: 0.39)
private let selectedGlowColor = Color(red: 1.0, green: 0.843, blue: 0.0, opacity: 0.25)
private let slotColorLight = Color(red: 0.886, green: 0.839, blue: 0.941)
private let slotColorDark = Color(red: 0.267, green: 0.208, blue: 0.396)
private let slotInnerLight = Color(red: 0.831, green: 0.769, blue: 0.91)
private let slotInnerDark = Color(red: 0.235, green: 0.18, blue: 0.361)
private let slotBorderOffset: CGFloat = 2
private let slotInnerShrink: CGFloat = 3
private let slotInnerOffset: CGFloat = 1

struct BoardView: View {
    let board: Board
    let onCellClicked: (Int32, Int32) -> Void

    @Environment(\.colorScheme) private var colorScheme

    private var slotColor: Color {
        colorScheme == .dark ? slotColorDark : slotColorLight
    }

    private var slotInnerColor: Color {
        colorScheme == .dark ? slotInnerDark : slotInnerLight
    }

    var body: some View {
        GeometryReader { geometry in
            let layout = BoardLayout(board: board, size: geometry.size)

            Canvas { context, _ in
                drawBoard(context: &context, layout: layout)
            }
            .contentShape(Rectangle())
            .onTapGesture { location in
                handleTap(at: location, layout: layout)
            }
        }
        .aspectRatio(CGFloat(board.cols) / CGFloat(board.rows), contentMode: .fit)
    }

    private func drawBoard(context: inout GraphicsContext, layout: BoardLayout) {
        for row in 0..<layout.rows {
            for col in 0..<layout.cols {
                let center = layout.cellCenter(row: row, col: col)
                drawCell(context: &context, row: Int32(row), col: Int32(col), center: center, radius: layout.pegRadius)
            }
        }
    }

    private func drawCell(context: inout GraphicsContext, row: Int32, col: Int32, center: CGPoint, radius: CGFloat) {
        if board.isEmpty(row: row, col: col) {
            drawSlot(context: &context, center: center, radius: radius)
        } else if board.isSelected(row: row, col: col) {
            drawPeg(context: &context, center: center, radius: radius, isSelected: true)
        } else if board.isPeg(row: row, col: col) {
            drawPeg(context: &context, center: center, radius: radius, isSelected: false)
        }
    }

    private func handleTap(at location: CGPoint, layout: BoardLayout) {
        let col = Int32((location.x - layout.offsetX) / layout.cellSize)
        let row = Int32((location.y - layout.offsetY) / layout.cellSize)

        guard row >= 0, row < Int32(layout.rows),
              col >= 0, col < Int32(layout.cols) else { return }

        onCellClicked(row, col)
    }

    private func drawSlot(context: inout GraphicsContext, center: CGPoint, radius: CGFloat) {
        let outerCenter = CGPoint(x: center.x, y: center.y + slotBorderOffset)
        let outerPath = circlePath(center: outerCenter, radius: radius)
        context.fill(outerPath, with: .color(slotColor))

        let innerCenter = CGPoint(x: center.x, y: center.y + slotInnerOffset)
        let innerPath = circlePath(center: innerCenter, radius: radius - slotInnerShrink)
        context.fill(innerPath, with: .color(slotInnerColor))
    }

    private func drawPeg(context: inout GraphicsContext, center: CGPoint, radius: CGFloat, isSelected: Bool) {
        if isSelected { drawSelectionGlow(context: &context, center: center, radius: radius) }
        drawDropShadow(context: &context, center: center, radius: radius)
        drawPegBody(context: &context, center: center, radius: radius, isSelected: isSelected)
        drawShineHighlight(context: &context, center: center, radius: radius)
    }

    private func drawSelectionGlow(context: inout GraphicsContext, center: CGPoint, radius: CGFloat) {
        let glowRadius = radius * glowRadiusFactor
        let path = circlePath(center: center, radius: glowRadius)
        context.fill(path, with: .color(selectedGlowColor))
    }

    private func drawDropShadow(context: inout GraphicsContext, center: CGPoint, radius: CGFloat) {
        let shadowCenter = CGPoint(x: center.x + shadowOffsetX, y: center.y + shadowOffsetY)
        let path = circlePath(center: shadowCenter, radius: radius)
        context.fill(path, with: .color(Color.black.opacity(shadowOpacity)))
    }

    private func drawPegBody(context: inout GraphicsContext, center: CGPoint, radius: CGFloat, isSelected: Bool) {
        let outerColor = isSelected ? selectedOuterColor : pegOuterColor
        let innerColor = isSelected ? selectedInnerColor : pegInnerColor
        let gradientCenter = CGPoint(
            x: center.x - radius * gradientOffsetFactor,
            y: center.y - radius * gradientOffsetFactor
        )

        let path = circlePath(center: center, radius: radius)
        context.fill(path, with: .radialGradient(
            Gradient(colors: [innerColor, outerColor]),
            center: gradientCenter,
            startRadius: 0,
            endRadius: radius * bodyGradientRadiusFactor
        ))
    }

    private func drawShineHighlight(context: inout GraphicsContext, center: CGPoint, radius: CGFloat) {
        let shineRadius = radius * shineRadiusFactor
        let shineCenter = CGPoint(
            x: center.x - radius * shineOffsetFactor,
            y: center.y - radius * shineVerticalOffsetFactor
        )
        let path = circlePath(center: shineCenter, radius: shineRadius)
        context.fill(path, with: .radialGradient(
            Gradient(colors: [Color.white.opacity(shineOpacity), Color.clear]),
            center: shineCenter,
            startRadius: 0,
            endRadius: shineRadius
        ))
    }

    private func circlePath(center: CGPoint, radius: CGFloat) -> Path {
        Path(ellipseIn: CGRect(
            x: center.x - radius, y: center.y - radius,
            width: radius * 2, height: radius * 2
        ))
    }
}

private struct BoardLayout {
    let rows: Int
    let cols: Int
    let cellSize: CGFloat
    let offsetX: CGFloat
    let offsetY: CGFloat
    let pegRadius: CGFloat

    init(board: Board, size: CGSize) {
        rows = Int(board.rows)
        cols = Int(board.cols)
        cellSize = min(size.width / CGFloat(cols), size.height / CGFloat(rows))
        offsetX = (size.width - cellSize * CGFloat(cols)) / 2
        offsetY = (size.height - cellSize * CGFloat(rows)) / 2
        pegRadius = cellSize / 2 - pegMargin
    }

    func cellCenter(row: Int, col: Int) -> CGPoint {
        CGPoint(
            x: offsetX + CGFloat(col) * cellSize + cellSize / 2,
            y: offsetY + CGFloat(row) * cellSize + cellSize / 2
        )
    }
}
