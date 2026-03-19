package com.erman.pegsolitaire.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.erman.pegsolitaire.R
import com.erman.pegsolitaire.domain.model.LevelItem
import com.erman.pegsolitaire.presentation.ChallengeLevelSelectorViewModel
import com.erman.pegsolitaire.ui.theme.CandyLavender
import com.erman.pegsolitaire.ui.theme.MarkedPegColor

private const val GRID_COLUMNS = 4
private const val LEVEL_CELL_CORNER_RADIUS = 12
private const val GRID_SPACING = 12
private const val LOAD_MORE_THRESHOLD = 10
private const val MAX_STARS = 3
private const val LOCKED_ALPHA = 0.4f
private const val STAR_SIZE = 14

@Composable
fun ChallengeLevelSelectorScreen(
    viewModel: ChallengeLevelSelectorViewModel,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialLevels()
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LevelSelectorTopBar()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.error != null && uiState.levels.isEmpty() -> ErrorContent(
                    message = uiState.error!!,
                    onRetry = viewModel::loadInitialLevels
                )
                uiState.isLoading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                else -> LevelGrid(
                    levels = uiState.levels,
                    isLoadingMore = uiState.isLoadingMore,
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
            text = "Challenge Levels",
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
        contentPadding = PaddingValues(GRID_SPACING.dp),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING.dp),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(levels, key = { it.levelNumber }) { level ->
            LevelCell(level = level, onClick = { onLevelSelected(level.levelNumber) })
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
private fun LevelCell(level: LevelItem, onClick: () -> Unit) {
    val backgroundColor = when {
        level.isLocked -> MaterialTheme.colorScheme.surface
        level.stars > 0 -> CandyLavender.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(LEVEL_CELL_CORNER_RADIUS.dp))
            .background(backgroundColor)
            .then(
                if (level.isLocked) Modifier.alpha(LOCKED_ALPHA)
                else Modifier.clickable(onClick = onClick)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (level.isLocked) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = level.levelNumber.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
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
        repeat(MAX_STARS) { index ->
            Text(
                text = if (index < stars) "\u2605" else "\u2606",
                fontSize = STAR_SIZE.sp,
                color = if (index < stars) MarkedPegColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
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
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}
