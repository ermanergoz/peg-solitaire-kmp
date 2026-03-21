package com.erman.pegsolitaire.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import pegsolitaire.composeapp.generated.resources.challenge_levels
import pegsolitaire.composeapp.generated.resources.retry
import com.erman.pegsolitaire.domain.model.LevelItem
import com.erman.pegsolitaire.presentation.ChallengeLevelSelectorViewModel
import com.erman.pegsolitaire.ui.theme.CardBackgroundDark
import com.erman.pegsolitaire.ui.theme.CardBackgroundLight
import com.erman.pegsolitaire.ui.theme.CompletedGradientEnd
import com.erman.pegsolitaire.ui.theme.CompletedGradientStart
import com.erman.pegsolitaire.ui.theme.StarGold
import com.erman.pegsolitaire.ui.theme.WarmBackground

private const val GRID_COLUMNS = 3
private val LEVEL_CELL_CORNER_RADIUS = 16.dp
private val GRID_SPACING = 12.dp
private const val LOAD_MORE_THRESHOLD = 10
private const val MAX_STARS = 3
private const val LOCKED_ALPHA = 0.35f
private val LEVEL_NUMBER_SIZE = 20.sp
private val STAR_ICON_SIZE = 12.dp
private val LOCK_ICON_SIZE = 20.dp

@Composable
fun ChallengeLevelSelectorScreen(
    viewModel: ChallengeLevelSelectorViewModel,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val background = if (isDark) MaterialTheme.colorScheme.background else WarmBackground

    LaunchedEffect(Unit) {
        viewModel.loadInitialLevels()
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
    ) {
        LevelSelectorTopBar()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.error != null && uiState.levels.isEmpty() -> ErrorContent(
                    message = uiState.error.orEmpty(),
                    onRetry = viewModel::loadInitialLevels
                )
                uiState.isLoading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                else -> LevelGrid(
                    levels = uiState.levels,
                    isLoadingMore = uiState.isLoadingMore,
                    isDark = isDark,
                    onLevelSelected = onLevelSelected,
                    onLoadMore = viewModel::loadMoreLevels
                )
            }
        }
    }
}

@Composable
private fun LevelSelectorTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.challenge_levels),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun LevelGrid(
    levels: List<LevelItem>,
    isLoadingMore: Boolean,
    isDark: Boolean,
    onLevelSelected: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= levels.size - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        state = gridState,
        contentPadding = PaddingValues(GRID_SPACING),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
        modifier = Modifier.fillMaxSize()
    ) {
        items(levels, key = { it.levelNumber }) { level ->
            LevelCell(
                level = level,
                isDark = isDark,
                onClick = { onLevelSelected(level.levelNumber) }
            )
        }

        if (isLoadingMore) {
            item(span = { GridItemSpan(GRID_COLUMNS) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelCell(level: LevelItem, isDark: Boolean, onClick: () -> Unit) {
    val cardColor = if (isDark) CardBackgroundDark else CardBackgroundLight
    val completedGradient = Brush.linearGradient(
        colors = listOf(CompletedGradientStart, CompletedGradientEnd),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val cellModifier = Modifier
        .aspectRatio(1f)
        .clip(RoundedCornerShape(LEVEL_CELL_CORNER_RADIUS))
        .then(
            when {
                level.stars > 0 -> Modifier.background(completedGradient)
                else -> Modifier.background(cardColor)
            }
        )
        .then(
            if (level.isLocked) Modifier.alpha(LOCKED_ALPHA)
            else Modifier.clickable(onClick = onClick)
        )
        .padding(12.dp)

    Box(
        modifier = cellModifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (level.isLocked) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(LOCK_ICON_SIZE)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = level.levelNumber.toString(),
                fontSize = LEVEL_NUMBER_SIZE,
                fontWeight = FontWeight.Bold,
                color = if (level.stars > 0) Color.White else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            if (level.stars > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                StarRow(stars = level.stars)
            }
        }
    }
}

@Composable
private fun StarRow(stars: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (index in 0 until MAX_STARS) {
            Icon(
                painter = painterResource(
                    if (index < stars) R.drawable.ic_star_filled else R.drawable.ic_star_empty
                ),
                contentDescription = null,
                tint = if (index < stars) StarGold else Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(STAR_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
    }
}
