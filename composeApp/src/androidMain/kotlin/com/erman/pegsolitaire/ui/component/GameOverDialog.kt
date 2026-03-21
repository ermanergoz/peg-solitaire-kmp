package com.erman.pegsolitaire.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import pegsolitaire.shared.generated.resources.Res
import pegsolitaire.shared.generated.resources.game_over
import pegsolitaire.shared.generated.resources.next
import pegsolitaire.shared.generated.resources.quit
import pegsolitaire.shared.generated.resources.restart

private const val STAR_FILLED = "\u2605"
private const val STAR_EMPTY = "\u2606"
private const val MAX_STARS = 3

@Composable
fun GameOverDialog(
    scoreText: String,
    stars: Int?,
    onRestart: () -> Unit,
    onQuit: () -> Unit,
    onNextLevel: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(Res.string.game_over),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = scoreText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                if (stars != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..MAX_STARS) {
                            Text(
                                text = if (i <= stars) STAR_FILLED else STAR_EMPTY,
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onQuit) {
                    Text(stringResource(Res.string.quit))
                }
                TextButton(onClick = onRestart) {
                    Text(stringResource(Res.string.restart))
                }
                if (onNextLevel != null) {
                    TextButton(onClick = onNextLevel) {
                        Text(stringResource(Res.string.next))
                    }
                }
            }
        }
    )
}
