package com.erman.pegsolitaire.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.engine.BoardType
import com.erman.pegsolitaire.presentation.HomeViewModel
import com.erman.pegsolitaire.presentation.formatElapsedTime

private const val CARD_CORNER_RADIUS = 16
private const val BUTTON_CORNER_RADIUS = 12
private const val CARD_ELEVATION = 4

@Composable
fun MenuScreen(
    homeViewModel: HomeViewModel,
    onClassicSelected: (BoardType) -> Unit,
    onChallengeSelected: () -> Unit
) {
    LaunchedEffect(Unit) {
        homeViewModel.loadData()
    }

    val uiState by homeViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Peg Solitaire",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        MenuCard(title = "Classic Mode") {
            ClassicModeButtons(
                bestScores = uiState.bestScores,
                onBoardSelected = onClassicSelected,
                buttonColor = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MenuCard(title = "Challenge Mode") {
            MenuButton(
                text = "Browse Levels",
                color = MaterialTheme.colorScheme.secondary
            ) { onChallengeSelected() }
        }
    }
}

@Composable
private fun MenuCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ClassicModeButtons(
    bestScores: Map<BoardType, GameScore>,
    onBoardSelected: (BoardType) -> Unit,
    buttonColor: Color
) {
    BoardType.entries.forEach { boardType ->
        val label = boardType.name.lowercase().replaceFirstChar { it.uppercase() }
        val score = bestScores[boardType]
        MenuButton(
            text = label,
            scoreText = score?.let { "${it.remainingPegs} left \u00B7 ${formatElapsedTime(it.elapsedTimeMillis)}" },
            color = buttonColor
        ) { onBoardSelected(boardType) }
    }
}

@Composable
private fun MenuButton(
    text: String,
    color: Color,
    scoreText: String? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text)
            if (scoreText != null) {
                Text(
                    text = scoreText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
