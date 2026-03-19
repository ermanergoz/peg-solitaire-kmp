import SwiftUI
import Shared

private let pegMargin: CGFloat = 3
private let shadowOffsetX: CGFloat = 0
private let shadowOffsetY: CGFloat = 3
private let shadowOpacity: Double = 0.16
private let shadowRadiusFactor: CGFloat = 1.05
private let glowRadiusMin: CGFloat = 1.3
private let glowRadiusMax: CGFloat = 1.6
private let glowAlphaMin: Double = 0.15
private let glowAlphaMax: Double = 0.25
private let glowPulseDuration: Double = 0.8
private let shineRadiusFactor: CGFloat = 0.35
private let shineOffsetFactor: CGFloat = 0.25
private let shineVerticalOffsetFactor: CGFloat = 0.3
private let shineOpacity: Double = 0.7
private let gradientOffsetFactor: CGFloat = 0.25
private let bodyGradientRadiusFactor: CGFloat = 1.2
private let slotAlpha: Double = 0.35
private let moveAnimDuration: Double = 0.25
private let shakeAmplitude: CGFloat = 12
private let shakeFrequency: Double = 3
private let shakeDuration: Double = 0.4
private let vortexMaxRotation: Double = 540

private let pegOuterColor = Color(red: 0.91, green: 0.227, blue: 0.365)
private let pegInnerColor = Color(red: 1.0, green: 0.482, blue: 0.584)
private let selectedOuterColor = Color(red: 1.0, green: 0.843, blue: 0.0)
private let selectedInnerColor = Color(red: 1.0, green: 0.94, blue: 0.39)
private let selectedGlowColor = Color(red: 1.0, green: 0.843, blue: 0.0)
private let slotColorLight = Color(red: 0.745, green: 0.769, blue: 0.816)
private let slotColorDark = Color(red: 0.227, green: 0.247, blue: 0.290)

struct MoveAnimData: Equatable {
    let fromRow: Int32
    let fromCol: Int32
    let toRow: Int32
    let toCol: Int32
    let capturedRow: Int32
    let capturedCol: Int32
}

struct BoardView: View {
    let board: Board
    let onCellClicked: (Int32, Int32) -> Void
    var moveAnim: MoveAnimData? = nil
    var onMoveAnimFinished: (() -> Void)? = nil
    var isShaking: Bool = false
    var onShakeFinished: (() -> Void)? = nil

    @Environment(\.colorScheme) private var colorScheme
    @State private var moveStartTime: Date? = nil
    @State private var shakeStartTime: Date? = nil
    @State private var moveFinished = false
    @State private var shakeFinished = false

    private var slotColor: Color {
        colorScheme == .dark ? slotColorDark : slotColorLight
    }

    private var needsAnimation: Bool {
        moveAnim != nil || isShaking || hasSelectedPeg
    }

    var body: some View {
        TimelineView(.animation(paused: !needsAnimation)) { timeline in
            let now = timeline.date
            let moveProgress = computeMoveProgress(now: now)
            let shakeProgress = computeShakeProgress(now: now)
            let shakeX = computeShakeX(progress: shakeProgress)
            let glowT = computeGlowT(now: now)
            let moveAnimStarted = moveStartTime != nil

            GeometryReader { geometry in
                let layout = BoardLayout(board: board, size: geometry.size)

                Canvas { context, _ in
                    drawBoard(
                        context: &context,
                        layout: layout,
                        moveProgress: moveProgress,
                        moveAnimStarted: moveAnimStarted,
                        glowT: glowT
                    )
                }
                .contentShape(Rectangle())
                .onTapGesture { location in
                    handleTap(at: location, layout: layout)
                }
            }
            .aspectRatio(CGFloat(board.cols) / CGFloat(board.rows), contentMode: .fit)
            .offset(x: shakeX)
        }
        .onChange(of: moveAnim) { _, newAnim in
            if newAnim != nil {
                moveStartTime = Date()
                moveFinished = false
            } else {
                moveStartTime = nil
            }
        }
        .onChange(of: isShaking) { _, shaking in
            if shaking {
                shakeStartTime = Date()
                shakeFinished = false
            } else {
                shakeStartTime = nil
            }
        }
    }

    private var hasSelectedPeg: Bool {
        for row in 0..<Int32(board.rows) {
            for col in 0..<Int32(board.cols) {
                if board.isSelected(row: row, col: col) { return true }
            }
        }
        return false
    }

    private func computeGlowT(now: Date) -> CGFloat {
        let period = glowPulseDuration * 2
        let t = now.timeIntervalSinceReferenceDate.truncatingRemainder(dividingBy: period) / period
        return t < 0.5 ? CGFloat(t * 2) : CGFloat(1 - (t - 0.5) * 2)
    }

    private func computeMoveProgress(now: Date) -> CGFloat {
        guard let start = moveStartTime, moveAnim != nil else { return 1.0 }
        let elapsed = now.timeIntervalSince(start)
        let progress = min(elapsed / moveAnimDuration, 1.0)
        if progress >= 1.0 && !moveFinished {
            DispatchQueue.main.async {
                moveFinished = true
                onMoveAnimFinished?()
            }
        }
        return CGFloat(progress)
    }

    private func computeShakeProgress(now: Date) -> CGFloat {
        guard let start = shakeStartTime, isShaking else { return 0 }
        let elapsed = now.timeIntervalSince(start)
        let progress = min(elapsed / shakeDuration, 1.0)
        if progress >= 1.0 && !shakeFinished {
            DispatchQueue.main.async {
                shakeFinished = true
                onShakeFinished?()
            }
        }
        return CGFloat(progress)
    }

    private func computeShakeX(progress: CGFloat) -> CGFloat {
        guard progress > 0 && progress < 1 else { return 0 }
        return sin(progress * shakeFrequency * 2 * .pi) * shakeAmplitude * (1 - progress)
    }

    private func drawBoard(
        context: inout GraphicsContext,
        layout: BoardLayout,
        moveProgress: CGFloat,
        moveAnimStarted: Bool,
        glowT: CGFloat
    ) {
        let animRadius = lerp(glowRadiusMin, glowRadiusMax, glowT)
        let animAlpha = lerp(glowAlphaMin, glowAlphaMax, glowT)
        let anim = moveAnim
        let hasActiveAnim = anim != nil && moveAnimStarted

        for row in 0..<layout.rows {
            for col in 0..<layout.cols {
                let center = layout.cellCenter(row: row, col: col)
                let r = Int32(row)
                let c = Int32(col)

                let isAnimSource = hasActiveAnim && anim!.fromRow == r && anim!.fromCol == c
                let isAnimCaptured = hasActiveAnim && anim!.capturedRow == r && anim!.capturedCol == c

                if isAnimSource || isAnimCaptured {
                    drawSlot(context: &context, center: center, radius: layout.pegRadius)
                } else if board.isEmpty(row: r, col: c) {
                    drawSlot(context: &context, center: center, radius: layout.pegRadius)
                } else if board.isSelected(row: r, col: c) {
                    drawSelectionGlow(context: &context, center: center, radius: layout.pegRadius, glowRadius: animRadius, glowAlpha: animAlpha)
                    drawPeg(context: &context, center: center, radius: layout.pegRadius, isSelected: true)
                } else if board.isPeg(row: r, col: c) {
                    drawPeg(context: &context, center: center, radius: layout.pegRadius, isSelected: false)
                }
            }
        }

        if let anim = anim, moveAnimStarted {
            let fromCenter = layout.cellCenter(row: Int(anim.fromRow), col: Int(anim.fromCol))
            let toCenter = layout.cellCenter(row: Int(anim.toRow), col: Int(anim.toCol))
            let slidingCenter = CGPoint(
                x: fromCenter.x + (toCenter.x - fromCenter.x) * moveProgress,
                y: fromCenter.y + (toCenter.y - fromCenter.y) * moveProgress
            )
            drawPeg(context: &context, center: slidingCenter, radius: layout.pegRadius, isSelected: false)

            let capturedCenter = layout.cellCenter(row: Int(anim.capturedRow), col: Int(anim.capturedCol))
            let scale = 1.0 - moveProgress
            let rotation = Angle.degrees(Double(moveProgress) * vortexMaxRotation)
            if scale > 0.01 {
                drawVortexPeg(context: &context, center: capturedCenter, radius: layout.pegRadius, scale: scale, rotation: rotation)
            }
        }
    }

    private func lerp(_ a: CGFloat, _ b: CGFloat, _ t: CGFloat) -> CGFloat {
        a + (b - a) * t
    }

    private func lerp(_ a: Double, _ b: Double, _ t: CGFloat) -> Double {
        a + (b - a) * Double(t)
    }

    private func handleTap(at location: CGPoint, layout: BoardLayout) {
        let col = Int32((location.x - layout.offsetX) / layout.cellSize)
        let row = Int32((location.y - layout.offsetY) / layout.cellSize)

        guard row >= 0, row < Int32(layout.rows),
              col >= 0, col < Int32(layout.cols) else { return }

        onCellClicked(row, col)
    }

    private func drawSlot(context: inout GraphicsContext, center: CGPoint, radius: CGFloat) {
        let path = circlePath(center: center, radius: radius)
        context.fill(path, with: .color(slotColor.opacity(slotAlpha)))
    }

    private func drawPeg(context: inout GraphicsContext, center: CGPoint, radius: CGFloat, isSelected: Bool) {
        drawDropShadow(context: &context, center: center, radius: radius)
        drawPegBody(context: &context, center: center, radius: radius, isSelected: isSelected)
        drawShineHighlight(context: &context, center: center, radius: radius)
    }

    private func drawVortexPeg(context: inout GraphicsContext, center: CGPoint, radius: CGFloat, scale: CGFloat, rotation: Angle) {
        let scaledRadius = radius * scale
        guard scaledRadius > 0 else { return }
        var ctx = context
        ctx.translateBy(x: center.x, y: center.y)
        ctx.rotate(by: rotation)
        ctx.translateBy(x: -center.x, y: -center.y)
        drawDropShadow(context: &ctx, center: center, radius: scaledRadius)
        drawPegBody(context: &ctx, center: center, radius: scaledRadius, isSelected: false)
        drawShineHighlight(context: &ctx, center: center, radius: scaledRadius)
    }

    private func drawSelectionGlow(context: inout GraphicsContext, center: CGPoint, radius: CGFloat, glowRadius: CGFloat, glowAlpha: Double) {
        let r = radius * glowRadius
        let path = circlePath(center: center, radius: r)
        context.fill(path, with: .radialGradient(
            Gradient(colors: [selectedGlowColor.opacity(glowAlpha), Color.clear]),
            center: center,
            startRadius: 0,
            endRadius: r
        ))
    }

    private func drawDropShadow(context: inout GraphicsContext, center: CGPoint, radius: CGFloat) {
        let shadowCenter = CGPoint(x: center.x + shadowOffsetX, y: center.y + shadowOffsetY)
        let shadowRadius = radius * shadowRadiusFactor
        let path = circlePath(center: shadowCenter, radius: shadowRadius)
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
