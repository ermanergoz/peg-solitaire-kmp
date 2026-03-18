package com.erman.pegsolitaire.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.ui.theme.MarkedPegCenter
import com.erman.pegsolitaire.ui.theme.MarkedPegColor
import com.erman.pegsolitaire.ui.theme.MarkedPegGlow
import com.erman.pegsolitaire.ui.theme.PegColor
import com.erman.pegsolitaire.ui.theme.PegColorCenter
import com.erman.pegsolitaire.ui.theme.PegSlotColorDark
import com.erman.pegsolitaire.ui.theme.PegSlotColorLight
import com.erman.pegsolitaire.ui.theme.PegSlotInnerDark
import com.erman.pegsolitaire.ui.theme.PegSlotInnerLight

private const val PEG_MARGIN = 5f
private const val SHADOW_OFFSET_X = 1f
private const val SHADOW_OFFSET_Y = 3f
private const val SHADOW_ALPHA_FACTOR = 50
private const val SHINE_ALPHA = 180
private const val GLOW_RADIUS_FACTOR = 1.5f
private const val SHINE_RADIUS_FACTOR = 0.35f
private const val SHINE_OFFSET_FACTOR = 0.25f
private const val GRADIENT_OFFSET_FACTOR = 0.25f
private const val SLOT_INNER_OFFSET = 1f
private const val SLOT_INNER_SHRINK = 3f
private const val SLOT_BORDER_OFFSET = 2f
private const val BODY_GRADIENT_RADIUS_FACTOR = 1.2f
private const val SHINE_VERTICAL_OFFSET_FACTOR = 0.3f
private val ShadowColor = Color(0, 0, 0, SHADOW_ALPHA_FACTOR)
private val ShineColor = Color(255, 255, 255, SHINE_ALPHA)

@Composable
fun BoardCanvas(
    board: Board,
    onCellClicked: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val slotColor = if (isDark) PegSlotColorDark else PegSlotColorLight
    val slotInnerColor = if (isDark) PegSlotInnerDark else PegSlotInnerLight
    val aspectRatio = board.cols.toFloat() / board.rows.toFloat()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
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

        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                val cx = col * cellWidth + cellWidth / 2f
                val cy = row * cellHeight + cellHeight / 2f
                val center = Offset(cx, cy)

                when {
                    board.isEmpty(row, col) -> drawPegSlot(center, radius, slotColor, slotInnerColor)
                    board.isSelected(row, col) -> drawPeg(center, radius, isSelected = true)
                    board.isPeg(row, col) -> drawPeg(center, radius, isSelected = false)
                }
            }
        }
    }
}

private fun DrawScope.drawPegSlot(
    center: Offset,
    radius: Float,
    slotColor: Color,
    slotInnerColor: Color
) {
    val slotRadius = radius - PEG_MARGIN - SLOT_BORDER_OFFSET
    drawCircle(slotColor, slotRadius, center)
    drawCircle(
        slotInnerColor,
        slotRadius - SLOT_INNER_SHRINK,
        Offset(center.x, center.y + SLOT_INNER_OFFSET)
    )
}

private fun DrawScope.drawPeg(center: Offset, radius: Float, isSelected: Boolean) {
    val pegRadius = radius - PEG_MARGIN
    if (pegRadius <= 0f) return

    if (isSelected) drawSelectionGlow(center, pegRadius)
    drawDropShadow(center, pegRadius)
    drawPegBody(center, pegRadius, isSelected)
    drawShineHighlight(center, pegRadius)
}

private fun DrawScope.drawSelectionGlow(center: Offset, pegRadius: Float) {
    val glowRadius = pegRadius * GLOW_RADIUS_FACTOR
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(MarkedPegGlow, Color.Transparent),
            center = center,
            radius = glowRadius
        ),
        radius = glowRadius,
        center = center
    )
}

private fun DrawScope.drawDropShadow(center: Offset, pegRadius: Float) {
    val shadowCenter = Offset(center.x + SHADOW_OFFSET_X, center.y + SHADOW_OFFSET_Y)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(ShadowColor, Color.Transparent),
            center = shadowCenter,
            radius = pegRadius
        ),
        radius = pegRadius,
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
