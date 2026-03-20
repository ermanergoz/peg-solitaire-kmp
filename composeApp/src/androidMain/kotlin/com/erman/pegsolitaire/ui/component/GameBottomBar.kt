package com.erman.pegsolitaire.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.erman.pegsolitaire.R
import com.erman.pegsolitaire.ui.theme.BadgeGray
import com.erman.pegsolitaire.ui.theme.BadgeOrange
import com.erman.pegsolitaire.ui.theme.BadgePurple
import com.erman.pegsolitaire.ui.theme.BadgeRed

private val BUTTON_SIZE = 48.dp
private val ICON_SIZE = 20.dp
private val BAR_HORIZONTAL_PADDING = 16.dp
private val BAR_VERTICAL_PADDING = 12.dp
private const val DISABLED_ALPHA = 0.4f
private const val ACTIVE_HINT_ALPHA = 0.5f

@Composable
fun GameBottomBar(
    canUndo: Boolean,
    isPaused: Boolean,
    hintsEnabled: Boolean,
    onUndoClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onHintClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = BAR_HORIZONTAL_PADDING, vertical = BAR_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BottomCircleButton(
                iconRes = R.drawable.ic_undo,
                color = if (canUndo) BadgeGray else BadgeGray.copy(alpha = DISABLED_ALPHA),
                enabled = canUndo,
                onClick = onUndoClicked
            )

            BottomCircleButton(
                iconRes = if (isPaused) R.drawable.ic_play else R.drawable.ic_pause,
                color = BadgePurple,
                enabled = true,
                onClick = onPauseClicked
            )

            BottomCircleButton(
                iconRes = R.drawable.ic_reset,
                color = BadgeRed,
                enabled = true,
                onClick = onResetClicked
            )

            BottomCircleButton(
                iconRes = R.drawable.ic_hint,
                color = if (hintsEnabled) BadgeOrange.copy(alpha = ACTIVE_HINT_ALPHA) else BadgeOrange,
                enabled = true,
                onClick = onHintClicked
            )
        }
    }
}

@Composable
private fun BottomCircleButton(
    iconRes: Int,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(BUTTON_SIZE)
            .clip(CircleShape)
            .background(color)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(ICON_SIZE)
        )
    }
}
