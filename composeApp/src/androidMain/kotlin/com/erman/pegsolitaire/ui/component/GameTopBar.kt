package com.erman.pegsolitaire.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erman.pegsolitaire.presentation.formatElapsedTime
import com.erman.pegsolitaire.ui.theme.BadgeBlue
import com.erman.pegsolitaire.ui.theme.BadgeGreen

private val PILL_CORNER_RADIUS = 20.dp
private val PILL_HORIZONTAL_PADDING = 16.dp
private val PILL_VERTICAL_PADDING = 8.dp
private val BAR_HORIZONTAL_PADDING = 16.dp
private val BAR_TOP_PADDING = 8.dp
private val BAR_BOTTOM_PADDING = 4.dp
private val PILL_SPACING = 12.dp
private val BADGE_FONT_SIZE = 15.sp

@Composable
fun GameTopBar(
    scoreText: String,
    elapsedTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    val timeText = formatElapsedTime(elapsedTimeMillis)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = BAR_HORIZONTAL_PADDING,
                end = BAR_HORIZONTAL_PADDING,
                top = BAR_TOP_PADDING,
                bottom = BAR_BOTTOM_PADDING
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(PILL_SPACING)) {
            PillBadge(text = scoreText, color = BadgeGreen)
            PillBadge(text = timeText, color = BadgeBlue)
        }
    }
}

@Composable
private fun PillBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(PILL_CORNER_RADIUS))
            .background(color)
            .padding(horizontal = PILL_HORIZONTAL_PADDING, vertical = PILL_VERTICAL_PADDING),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = BADGE_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )
    }
}
