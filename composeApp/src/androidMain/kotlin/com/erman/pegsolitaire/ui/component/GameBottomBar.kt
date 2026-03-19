package com.erman.pegsolitaire.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erman.pegsolitaire.ui.theme.BadgeGray
import com.erman.pegsolitaire.ui.theme.BadgePurple
import com.erman.pegsolitaire.ui.theme.BadgeRed

private val BUTTON_SIZE = 48.dp
private val BAR_HORIZONTAL_PADDING = 16.dp
private val BAR_VERTICAL_PADDING = 12.dp
private val ICON_FONT_SIZE = 18.sp
private const val DISABLED_ALPHA = 0.4f
private const val PAUSE_SYMBOL = "\u2016"
private const val PLAY_SYMBOL = "\u25B6"

@Composable
fun GameBottomBar(
    canUndo: Boolean,
    isPaused: Boolean,
    onUndoClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = BAR_HORIZONTAL_PADDING, vertical = BAR_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BottomCircleButton(
                text = "\u21A9",
                color = if (canUndo) BadgeGray else BadgeGray.copy(alpha = DISABLED_ALPHA),
                enabled = canUndo,
                onClick = onUndoClicked
            )

            BottomCircleButton(
                text = if (isPaused) PLAY_SYMBOL else PAUSE_SYMBOL,
                color = BadgePurple,
                enabled = true,
                onClick = onPauseClicked
            )

            BottomCircleButton(
                text = "\u21BB",
                color = BadgeRed,
                enabled = true,
                onClick = onResetClicked
            )
        }
    }
}

@Composable
private fun BottomCircleButton(
    text: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(BUTTON_SIZE)
            .clip(CircleShape)
            .background(color)
            .then(
                if (enabled) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = ICON_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )
    }
}
