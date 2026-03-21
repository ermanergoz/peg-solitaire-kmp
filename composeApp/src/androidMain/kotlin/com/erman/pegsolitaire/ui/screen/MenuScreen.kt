package com.erman.pegsolitaire.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erman.pegsolitaire.R
import org.jetbrains.compose.resources.stringResource
import pegsolitaire.composeapp.generated.resources.Res
import pegsolitaire.composeapp.generated.resources.app_title
import pegsolitaire.composeapp.generated.resources.browse_all_levels
import pegsolitaire.composeapp.generated.resources.challenge_mode
import pegsolitaire.composeapp.generated.resources.classic_mode
import pegsolitaire.composeapp.generated.resources.level_n
import pegsolitaire.composeapp.generated.resources.not_played
import pegsolitaire.composeapp.generated.resources.play
import pegsolitaire.composeapp.generated.resources.score_left
import pegsolitaire.composeapp.generated.resources.settings
import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.engine.BoardType
import com.erman.pegsolitaire.presentation.HomeViewModel
import com.erman.pegsolitaire.presentation.formatElapsedTime
import com.erman.pegsolitaire.ui.theme.CardBackgroundDark
import com.erman.pegsolitaire.ui.theme.CardBackgroundLight
import com.erman.pegsolitaire.ui.theme.CompletedGradientEnd
import com.erman.pegsolitaire.ui.theme.CompletedGradientStart
import com.erman.pegsolitaire.ui.theme.TextPrimaryDark
import com.erman.pegsolitaire.ui.theme.TextPrimaryLight
import com.erman.pegsolitaire.ui.theme.TextSecondaryDark
import com.erman.pegsolitaire.ui.theme.TextSecondaryLight
import com.erman.pegsolitaire.ui.theme.WarmBackground
import com.erman.pegsolitaire.ui.theme.boardBackgroundColor

private val CARD_CORNER_RADIUS = 16.dp
private val CARD_ELEVATION = 2.dp
private val HERO_CORNER_RADIUS = 20.dp
private val THUMBNAIL_SIZE = 36.dp
private val THUMBNAIL_CORNER_RADIUS = 10.dp
private val SETTINGS_BUTTON_SIZE = 36.dp
private val SETTINGS_ICON_SIZE = 20.dp
private val SETTINGS_CORNER_RADIUS = 10.dp
private val BOARD_NAME_FONT_SIZE = 14.sp
private val SCORE_FONT_SIZE = 11.sp
private val LEVEL_DOT_SIZE = 28.dp
private val PLAY_BUTTON_CORNER_RADIUS = 12.dp
private const val SCORE_MIDDLE_DOT = " \u00B7 "
private const val HERO_LABEL_ALPHA = 0.7f
private const val LEVEL_DOT_COMPLETED_ALPHA = 0.3f
private const val LEVEL_DOT_LOCKED_ALPHA = 0.15f
private const val LEVEL_DOTS_BEFORE = 3
private const val LEVEL_DOTS_AFTER = 2

@Composable
fun MenuScreen(
    homeViewModel: HomeViewModel,
    onClassicSelected: (BoardType) -> Unit,
    onChallengeSelected: () -> Unit,
    onSettingsClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        homeViewModel.loadData()
    }

    val uiState by homeViewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val background = if (isDark) MaterialTheme.colorScheme.background else WarmBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        TitleRow(onSettingsClick = onSettingsClick, isDark = isDark)

        Spacer(modifier = Modifier.height(20.dp))

        ChallengeHeroCard(
            currentChallengeLevel = uiState.currentChallengeLevel,
            onPlayClick = onChallengeSelected,
            onBrowseAllClick = onChallengeSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        ClassicModeSection(
            bestScores = uiState.bestScores,
            isDark = isDark,
            onClassicSelected = onClassicSelected
        )
    }
}

@Composable
private fun TitleRow(onSettingsClick: () -> Unit, isDark: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.app_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        SettingsButton(onClick = onSettingsClick, isDark = isDark)
    }
}

@Composable
private fun SettingsButton(onClick: () -> Unit, isDark: Boolean) {
    val cardColor = if (isDark) CardBackgroundDark else CardBackgroundLight
    Box(
        modifier = Modifier
            .size(SETTINGS_BUTTON_SIZE)
            .clip(RoundedCornerShape(SETTINGS_CORNER_RADIUS))
            .background(cardColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = stringResource(Res.string.settings),
            tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
            modifier = Modifier.size(SETTINGS_ICON_SIZE)
        )
    }
}

@Composable
private fun ChallengeHeroCard(
    currentChallengeLevel: Int,
    onPlayClick: () -> Unit,
    onBrowseAllClick: () -> Unit
) {
    val gradient = Brush.linearGradient(
        colors = listOf(CompletedGradientStart, CompletedGradientEnd),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HERO_CORNER_RADIUS))
            .background(gradient)
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.challenge_mode).uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                color = Color.White.copy(alpha = HERO_LABEL_ALPHA),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.level_n, currentChallengeLevel),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlayClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(PLAY_BUTTON_CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = CompletedGradientStart
                )
            ) {
                Text(
                    text = stringResource(Res.string.play),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LevelDotsRow(currentChallengeLevel = currentChallengeLevel)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${stringResource(Res.string.browse_all_levels)} \u2192",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = HERO_LABEL_ALPHA),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onBrowseAllClick)
            )
        }
    }
}

@Composable
private fun LevelDotsRow(currentChallengeLevel: Int) {
    val firstDotLevel = (currentChallengeLevel - LEVEL_DOTS_BEFORE).coerceAtLeast(1)
    val levels = (firstDotLevel until firstDotLevel + LEVEL_DOTS_BEFORE + 1 + LEVEL_DOTS_AFTER).toList()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { index, level ->
            if (index > 0) Spacer(modifier = Modifier.width(6.dp))
            LevelDot(level = level, currentChallengeLevel = currentChallengeLevel)
        }
    }
}

@Composable
private fun LevelDot(level: Int, currentChallengeLevel: Int) {
    val isCurrent = level == currentChallengeLevel
    val isCompleted = level < currentChallengeLevel

    val dotBackground = when {
        isCurrent -> Color.White
        isCompleted -> Color.White.copy(alpha = LEVEL_DOT_COMPLETED_ALPHA)
        else -> Color.White.copy(alpha = LEVEL_DOT_LOCKED_ALPHA)
    }
    val textColor = when {
        isCurrent -> CompletedGradientStart
        else -> Color.White
    }
    val dotSize = if (isCurrent) LEVEL_DOT_SIZE else LEVEL_DOT_SIZE * 0.8f

    Box(
        modifier = Modifier
            .size(dotSize)
            .clip(CircleShape)
            .background(dotBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level.toString(),
            fontSize = 10.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun ClassicModeSection(
    bestScores: Map<BoardType, GameScore>,
    isDark: Boolean,
    onClassicSelected: (BoardType) -> Unit
) {
    Text(
        text = stringResource(Res.string.classic_mode).uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        color = if (isDark) TextSecondaryDark else TextSecondaryLight
    )

    Spacer(modifier = Modifier.height(10.dp))

    BoardType.entries.forEach { boardType ->
        ClassicBoardCard(
            boardType = boardType,
            score = bestScores[boardType],
            isDark = isDark,
            onClick = { onClassicSelected(boardType) }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ClassicBoardCard(
    boardType: BoardType,
    score: GameScore?,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (isDark) CardBackgroundDark else CardBackgroundLight
    val nameColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val scoreColor = if (isDark) TextSecondaryDark else TextSecondaryLight

    val boardName = boardType.name.lowercase().replaceFirstChar { it.uppercase() }
    val notPlayedText = stringResource(Res.string.not_played)
    val scoreText = score?.let {
        "${stringResource(Res.string.score_left, it.remainingPegs)}$SCORE_MIDDLE_DOT${formatElapsedTime(it.elapsedTimeMillis)}"
    } ?: notPlayedText

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoardShapeThumbnail(boardType = boardType, isDark = isDark)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = boardName,
                    fontSize = BOARD_NAME_FONT_SIZE,
                    fontWeight = FontWeight.SemiBold,
                    color = nameColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = scoreText,
                    fontSize = SCORE_FONT_SIZE,
                    color = scoreColor
                )
            }
        }
    }
}

@Composable
private fun BoardShapeThumbnail(boardType: BoardType, isDark: Boolean) {
    val bgColor = boardBackgroundColor(boardType, isDark)
    val fillColor = bgColor.darken(0.35f)

    Box(
        modifier = Modifier
            .size(THUMBNAIL_SIZE)
            .clip(RoundedCornerShape(THUMBNAIL_CORNER_RADIUS))
            .background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBoardSilhouette(boardType = boardType, fillColor = fillColor)
        }
    }
}

private fun Color.darken(fraction: Float): Color {
    val f = 1f - fraction
    return Color(red = red * f, green = green * f, blue = blue * f, alpha = alpha)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBoardSilhouette(
    boardType: BoardType,
    fillColor: Color
) {
    val rowPattern = boardSilhouetteRows(boardType)
    val rowCount = rowPattern.size
    val colCount = rowPattern.maxOf { it.last } + 1
    val maxDim = maxOf(rowCount, colCount)
    val cellSize = minOf(size.width, size.height) / maxDim
    val offsetX = (size.width - colCount * cellSize) / 2f
    val offsetY = (size.height - rowCount * cellSize) / 2f

    rowPattern.forEachIndexed { row, cols ->
        for (col in cols) {
            drawRect(
                color = fillColor,
                topLeft = Offset(offsetX + col * cellSize + cellSize * 0.1f, offsetY + row * cellSize + cellSize * 0.1f),
                size = androidx.compose.ui.geometry.Size(cellSize * 0.8f, cellSize * 0.8f)
            )
        }
    }
}

private fun boardSilhouetteRows(boardType: BoardType): List<IntRange> = when (boardType) {
    BoardType.ENGLISH -> listOf(
        2..4, 2..4,
        0..6, 0..6, 0..6,
        2..4, 2..4
    )
    BoardType.FRENCH -> listOf(
        2..4,
        1..5,
        0..6, 0..6, 0..6,
        1..5,
        2..4
    )
    BoardType.GERMAN -> listOf(
        3..5, 3..5, 3..5,
        0..8, 0..8, 0..8,
        3..5, 3..5, 3..5
    )
    BoardType.ASYMMETRIC -> listOf(
        3..5, 3..5,
        0..7, 0..7, 0..7,
        3..5, 3..5, 3..5
    )
    BoardType.DIAMOND -> listOf(
        4..4,
        3..5,
        2..6,
        1..7,
        0..8,
        1..7,
        2..6,
        3..5,
        4..4
    )
}
