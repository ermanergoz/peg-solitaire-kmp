package com.erman.pegsolitaire.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.engine.Position
import com.erman.pegsolitaire.ui.theme.MarkedPegCenter
import com.erman.pegsolitaire.ui.theme.MarkedPegColor
import com.erman.pegsolitaire.ui.theme.MarkedPegGlow
import com.erman.pegsolitaire.ui.theme.PegColor
import com.erman.pegsolitaire.ui.theme.PegColorCenter
import com.erman.pegsolitaire.ui.theme.PegSlotColorDark
import com.erman.pegsolitaire.ui.theme.PegSlotColorLight
import kotlin.math.roundToInt
import kotlin.math.sin

private const val PEG_MARGIN = 3f
private const val SHADOW_OFFSET_X = 0f
private const val SHADOW_OFFSET_Y = 3f
private const val SHADOW_ALPHA = 40
private const val SHADOW_RADIUS_FACTOR = 1.05f
private const val SHINE_ALPHA = 160
private const val GLOW_RADIUS_MIN = 1.3f
private const val GLOW_RADIUS_MAX = 1.6f
private const val GLOW_ALPHA_MIN = 0.6f
private const val GLOW_ALPHA_MAX = 1.0f
private const val GLOW_PULSE_DURATION_MS = 800
private const val SHINE_RADIUS_FACTOR = 0.35f
private const val SHINE_OFFSET_FACTOR = 0.25f
private const val GRADIENT_OFFSET_FACTOR = 0.25f
private const val SLOT_ALPHA = 0.35f
private const val BODY_GRADIENT_RADIUS_FACTOR = 1.2f
private const val SHINE_VERTICAL_OFFSET_FACTOR = 0.3f
private const val MOVE_ANIMATION_DURATION_MS = 250
private const val SHAKE_DURATION_MS = 400
private const val SHAKE_AMPLITUDE = 12f
private const val SHAKE_FREQUENCY = 3
private const val VORTEX_MAX_ROTATION = 540f
private val ShadowColor = Color(0, 0, 0, SHADOW_ALPHA)
private val ShineColor = Color(255, 255, 255, SHINE_ALPHA)

data class MoveAnimationData(
    val from: Position,
    val to: Position,
    val captured: Position
)

@Composable
fun BoardCanvas(
    board: Board,
    onCellClicked: (row: Int, col: Int) -> Unit,
    moveAnimation: MoveAnimationData?,
    onMoveAnimationFinished: () -> Unit,
    isShaking: Boolean,
    onShakeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val slotColor = if (isDark) PegSlotColorDark else PegSlotColorLight
    val aspectRatio = board.cols.toFloat() / board.rows.toFloat()

    val pulseTransition = rememberInfiniteTransition(label = "selectionPulse")
    val glowRadiusFactor by pulseTransition.animateFloat(
        initialValue = GLOW_RADIUS_MIN,
        targetValue = GLOW_RADIUS_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(GLOW_PULSE_DURATION_MS),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowRadius"
    )
    val glowAlpha by pulseTransition.animateFloat(
        initialValue = GLOW_ALPHA_MIN,
        targetValue = GLOW_ALPHA_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(GLOW_PULSE_DURATION_MS),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val moveAnimatable = remember(moveAnimation) {
        Animatable(if (moveAnimation != null) 0f else 1f)
    }

    LaunchedEffect(moveAnimation) {
        if (moveAnimation != null) {
            moveAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(MOVE_ANIMATION_DURATION_MS, easing = LinearEasing)
            )
            onMoveAnimationFinished()
        }
    }

    val shakeAnimatable = remember(isShaking) {
        Animatable(0f)
    }
    LaunchedEffect(isShaking) {
        if (isShaking) {
            shakeAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(SHAKE_DURATION_MS, easing = LinearEasing)
            )
            onShakeFinished()
        }
    }

    val shakeX = if (isShaking) {
        val t = shakeAnimatable.value
        (sin(t * SHAKE_FREQUENCY * 2 * Math.PI) * SHAKE_AMPLITUDE * (1f - t)).toFloat()
    } else 0f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .offset { IntOffset(shakeX.roundToInt(), 0) }
            .pointerInput(board.rows, board.cols) {
                detectTapGestures { offset ->
                    val cellWidth = size.width.toFloat() / board.cols
                    val cellHeight = size.height.toFloat() / board.rows
                    val col = (offset.x / cellWidth).toInt()
                    val row = (offset.y / cellHeight).toInt()
                    if (row in 0 until board.rows && col in 0 until board.cols) {
                        onCellClicked(row, col)
                    }
                }
            }
    ) {
        val cellWidth = size.width / board.cols
        val cellHeight = size.height / board.rows
        val radius = minOf(cellWidth, cellHeight) / 2f
        val anim = moveAnimation
        val progress = moveAnimatable.value

        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                val cx = col * cellWidth + cellWidth / 2f
                val cy = row * cellHeight + cellHeight / 2f
                val center = Offset(cx, cy)

                val isAnimSource = anim != null && anim.from.row == row && anim.from.col == col
                val isAnimCaptured = anim != null && anim.captured.row == row && anim.captured.col == col

                when {
                    isAnimSource -> drawPegSlot(center, radius, slotColor)
                    isAnimCaptured -> drawPegSlot(center, radius, slotColor)
                    board.isEmpty(row, col) -> drawPegSlot(center, radius, slotColor)
                    board.isSelected(row, col) -> drawPeg(
                        center, radius, isSelected = true,
                        glowRadiusFactor = glowRadiusFactor,
                        glowAlpha = glowAlpha
                    )
                    board.isPeg(row, col) -> drawPeg(center, radius, isSelected = false)
                }
            }
        }

        if (anim != null) {
            val fromCx = anim.from.col * cellWidth + cellWidth / 2f
            val fromCy = anim.from.row * cellHeight + cellHeight / 2f
            val toCx = anim.to.col * cellWidth + cellWidth / 2f
            val toCy = anim.to.row * cellHeight + cellHeight / 2f
            val slidingCenter = Offset(
                fromCx + (toCx - fromCx) * progress,
                fromCy + (toCy - fromCy) * progress
            )
            drawPeg(slidingCenter, radius, isSelected = false)

            val capturedCx = anim.captured.col * cellWidth + cellWidth / 2f
            val capturedCy = anim.captured.row * cellHeight + cellHeight / 2f
            val capturedCenter = Offset(capturedCx, capturedCy)
            val capturedScale = 1f - progress
            val capturedRotation = progress * VORTEX_MAX_ROTATION
            if (capturedScale > 0.01f) {
                drawVortexPeg(capturedCenter, radius, capturedScale, capturedRotation)
            }
        }
    }
}

private fun DrawScope.drawPegSlot(center: Offset, radius: Float, slotColor: Color) {
    val slotRadius = radius - PEG_MARGIN
    drawCircle(slotColor.copy(alpha = SLOT_ALPHA), slotRadius, center)
}

private fun DrawScope.drawPeg(
    center: Offset,
    radius: Float,
    isSelected: Boolean,
    glowRadiusFactor: Float = GLOW_RADIUS_MIN,
    glowAlpha: Float = GLOW_ALPHA_MAX
) {
    val pegRadius = radius - PEG_MARGIN
    if (pegRadius <= 0f) return

    if (isSelected) drawSelectionGlow(center, pegRadius, glowRadiusFactor, glowAlpha)
    drawDropShadow(center, pegRadius)
    drawPegBody(center, pegRadius, isSelected)
    drawShineHighlight(center, pegRadius)
}

private fun DrawScope.drawVortexPeg(center: Offset, radius: Float, scale: Float, rotation: Float) {
    val pegRadius = (radius - PEG_MARGIN) * scale
    if (pegRadius <= 0f) return

    withTransform({
        rotate(rotation, center)
    }) {
        drawDropShadow(center, pegRadius)
        drawPegBody(center, pegRadius, isSelected = false)
        drawShineHighlight(center, pegRadius)
    }
}

private fun DrawScope.drawSelectionGlow(
    center: Offset,
    pegRadius: Float,
    radiusFactor: Float,
    alpha: Float
) {
    val glowRadius = pegRadius * radiusFactor
    val glowColor = MarkedPegGlow.copy(alpha = MarkedPegGlow.alpha * alpha)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor, Color.Transparent),
            center = center,
            radius = glowRadius
        ),
        radius = glowRadius,
        center = center
    )
}

private fun DrawScope.drawDropShadow(center: Offset, pegRadius: Float) {
    val shadowCenter = Offset(center.x + SHADOW_OFFSET_X, center.y + SHADOW_OFFSET_Y)
    val shadowRadius = pegRadius * SHADOW_RADIUS_FACTOR
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(ShadowColor, Color.Transparent),
            center = shadowCenter,
            radius = shadowRadius
        ),
        radius = shadowRadius,
        center = shadowCenter
    )
}

private fun DrawScope.drawPegBody(center: Offset, pegRadius: Float, isSelected: Boolean) {
    val outerColor = if (isSelected) MarkedPegColor else PegColor
    val innerColor = if (isSelected) MarkedPegCenter else PegColorCenter
    val gradientCenter = Offset(
        center.x - pegRadius * GRADIENT_OFFSET_FACTOR,
        center.y - pegRadius * GRADIENT_OFFSET_FACTOR
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(innerColor, outerColor),
            center = gradientCenter,
            radius = pegRadius * BODY_GRADIENT_RADIUS_FACTOR
        ),
        radius = pegRadius,
        center = center
    )
}

private fun DrawScope.drawShineHighlight(center: Offset, pegRadius: Float) {
    val shineRadius = pegRadius * SHINE_RADIUS_FACTOR
    val shineCenter = Offset(
        center.x - pegRadius * SHINE_OFFSET_FACTOR,
        center.y - pegRadius * SHINE_VERTICAL_OFFSET_FACTOR
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(ShineColor, Color.Transparent),
            center = shineCenter,
            radius = shineRadius
        ),
        radius = shineRadius,
        center = shineCenter
    )
}
