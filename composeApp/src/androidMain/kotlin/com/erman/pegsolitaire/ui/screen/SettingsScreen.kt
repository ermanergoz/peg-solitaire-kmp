package com.erman.pegsolitaire.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erman.pegsolitaire.presentation.SettingsUiState
import org.jetbrains.compose.resources.stringResource
import pegsolitaire.composeapp.generated.resources.Res
import pegsolitaire.composeapp.generated.resources.cancel
import pegsolitaire.composeapp.generated.resources.haptic_feedback
import pegsolitaire.composeapp.generated.resources.reset
import pegsolitaire.composeapp.generated.resources.reset_all_scores
import pegsolitaire.composeapp.generated.resources.reset_all_scores_confirm
import pegsolitaire.composeapp.generated.resources.settings
import pegsolitaire.composeapp.generated.resources.sound_effects

private const val CARD_CORNER_RADIUS = 16
private const val BUTTON_CORNER_RADIUS = 12
private const val CARD_ELEVATION = 4
private const val SCREEN_PADDING = 24
private const val SECTION_SPACING = 24
private const val CARD_CONTENT_PADDING = 16
private const val TOGGLE_VERTICAL_PADDING = 8
private const val SPACER_HEIGHT = 16

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptic: () -> Unit,
    onResetScoresClick: () -> Unit,
    onConfirmReset: () -> Unit,
    onDismissReset: () -> Unit
) {
    BackHandler { onBackClick() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(SCREEN_PADDING.dp)
    ) {
        Text(
            text = stringResource(Res.string.settings),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(SECTION_SPACING.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp)
        ) {
            Column(modifier = Modifier.padding(CARD_CONTENT_PADDING.dp)) {
                SettingsToggleRow(
                    label = stringResource(Res.string.sound_effects),
                    checked = uiState.soundEnabled,
                    onToggle = onToggleSound
                )
                SettingsToggleRow(
                    label = stringResource(Res.string.haptic_feedback),
                    checked = uiState.hapticEnabled,
                    onToggle = onToggleHaptic
                )
            }
        }

        Spacer(modifier = Modifier.height(SPACER_HEIGHT.dp))

        Button(
            onClick = onResetScoresClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(stringResource(Res.string.reset_all_scores))
        }
    }

    if (uiState.showResetConfirmation) {
        AlertDialog(
            onDismissRequest = onDismissReset,
            title = { Text(stringResource(Res.string.reset_all_scores)) },
            text = { Text(stringResource(Res.string.reset_all_scores_confirm)) },
            confirmButton = {
                TextButton(onClick = onConfirmReset) {
                    Text(stringResource(Res.string.reset), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissReset) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TOGGLE_VERTICAL_PADDING.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
