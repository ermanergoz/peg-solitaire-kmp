# UI Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Redesign home screen and level selector with warm visual identity, replace Unicode icons with Canvas-drawn vectors, fix pause overlay bug, and align iOS game bars.

**Architecture:** UI-layer only changes across Android Compose and iOS SwiftUI. No ViewModel or domain changes. New named colors in theme files, Canvas/Path icon drawing replaces all Unicode text symbols.

**Tech Stack:** Kotlin/Compose (Android), SwiftUI (iOS), Canvas/Path drawing on both platforms.

**Spec:** `docs/superpowers/specs/2026-03-21-ui-redesign-design.md`

---

### Task 1: Add named colors to theme files

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/theme/Color.kt`
- Create: `iosApp/iosApp/Theme/Colors.swift`

- [ ] **Step 1: Add new color constants to Android Color.kt**

Add below the existing badge colors (after line 39):

```kotlin
// Redesign colors
val WarmBackground = Color(0xFFF5F0EB)
val CompletedGradientStart = Color(0xFF6C63FF)
val CompletedGradientEnd = Color(0xFF8B7FFF)
val StarGold = Color(0xFFF59E0B)
val StarEmpty = Color(0xFFDDDDDD)
val ChevronGray = Color(0xFFCCCCCC)

// Board thumbnail gradients
val ThumbnailEnglishStart = Color(0xFFA8D8EA)
val ThumbnailEnglishEnd = Color(0xFF7EC8E3)
val ThumbnailFrenchStart = Color(0xFFF8B4C8)
val ThumbnailFrenchEnd = Color(0xFFF29DAE)
val ThumbnailGermanStart = Color(0xFFF5E6CA)
val ThumbnailGermanEnd = Color(0xFFE8D5B0)
val ThumbnailAsymmetricStart = Color(0xFFB4E6C8)
val ThumbnailAsymmetricEnd = Color(0xFF8FD4AA)
val ThumbnailDiamondStart = Color(0xFFC8B4F0)
val ThumbnailDiamondEnd = Color(0xFFB09DE0)
```

- [ ] **Step 2: Add a helper function for thumbnail gradient colors by board type**

Add to Color.kt below the new constants:

```kotlin
fun thumbnailGradient(boardType: BoardType): Pair<Color, Color> = when (boardType) {
    BoardType.ENGLISH -> ThumbnailEnglishStart to ThumbnailEnglishEnd
    BoardType.FRENCH -> ThumbnailFrenchStart to ThumbnailFrenchEnd
    BoardType.GERMAN -> ThumbnailGermanStart to ThumbnailGermanEnd
    BoardType.ASYMMETRIC -> ThumbnailAsymmetricStart to ThumbnailAsymmetricEnd
    BoardType.DIAMOND -> ThumbnailDiamondStart to ThumbnailDiamondEnd
}
```

- [ ] **Step 3: Add iOS color constants**

Create the `iosApp/iosApp/Theme/` directory and then create `iosApp/iosApp/Theme/Colors.swift` with all named colors. iOS currently has colors scattered as file-level `let` constants in `GameView.swift`. The new file centralizes redesign colors:

```swift
import SwiftUI

let warmBackground = Color(red: 0.961, green: 0.941, blue: 0.922)
let completedGradientStart = Color(red: 0.424, green: 0.388, blue: 1.0)
let completedGradientEnd = Color(red: 0.545, green: 0.498, blue: 1.0)
let starGold = Color(red: 0.961, green: 0.620, blue: 0.043)
let starEmpty = Color(red: 0.867, green: 0.867, blue: 0.867)
let chevronGray = Color(red: 0.800, green: 0.800, blue: 0.800)

func thumbnailGradient(boardType: BoardType) -> (start: Color, end: Color) {
    switch boardType {
    case .english:
        return (Color(red: 0.659, green: 0.847, blue: 0.918), Color(red: 0.494, green: 0.784, blue: 0.890))
    case .french:
        return (Color(red: 0.973, green: 0.706, blue: 0.784), Color(red: 0.949, green: 0.616, blue: 0.682))
    case .german:
        return (Color(red: 0.961, green: 0.902, blue: 0.792), Color(red: 0.910, green: 0.835, blue: 0.690))
    case .asymmetric:
        return (Color(red: 0.706, green: 0.902, blue: 0.784), Color(red: 0.561, green: 0.831, blue: 0.667))
    case .diamond:
        return (Color(red: 0.784, green: 0.706, blue: 0.941), Color(red: 0.690, green: 0.616, blue: 0.878))
    default:
        return (Color(red: 0.659, green: 0.847, blue: 0.918), Color(red: 0.494, green: 0.784, blue: 0.890))
    }
}
```

Note: Keep existing game bar colors (`badgePurple`, `badgeGreen`, etc.) in `GameView.swift` — they are only used there. The new file holds colors used across multiple screens.

- [ ] **Step 4: Verify compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```
Add named colors for UI redesign
```

---

### Task 2: Create Canvas icon drawing utilities (Android)

**Files:**
- Create: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/component/IconDrawing.kt`

- [ ] **Step 1: Create the icon drawing file with all icon composables**

Create `IconDrawing.kt` with private composable functions for each icon. Each function draws into a `Canvas` at the given size. All icons use white color on their colored circle backgrounds (the circle is already drawn by the button container).

```kotlin
package com.erman.pegsolitaire.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val ICON_SCALE = 0.55f
private const val STROKE_FRACTION = 0.08f
private const val STAR_OUTER_RATIO = 0.45f
private const val STAR_INNER_RATIO = 0.18f
private const val STAR_POINTS = 5

@Composable
fun PauseIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val s = size.minDimension * ICON_SCALE
        val barWidth = s * 0.22f
        val barHeight = s * 0.8f
        val gap = s * 0.18f
        val left = (size.width - barWidth * 2 - gap) / 2f
        val top = (size.height - barHeight) / 2f
        drawRoundRect(tint, Offset(left, top), Size(barWidth, barHeight), CornerRadius(barWidth * 0.3f))
        drawRoundRect(tint, Offset(left + barWidth + gap, top), Size(barWidth, barHeight), CornerRadius(barWidth * 0.3f))
    }
}

@Composable
fun PlayIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val s = size.minDimension * ICON_SCALE
        val cx = size.width / 2f
        val cy = size.height / 2f
        val path = Path().apply {
            moveTo(cx - s * 0.25f, cy - s * 0.45f)
            lineTo(cx + s * 0.4f, cy)
            lineTo(cx - s * 0.25f, cy + s * 0.45f)
            close()
        }
        drawPath(path, tint)
    }
}

@Composable
fun UndoIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val s = size.minDimension * ICON_SCALE
        val strokeW = size.minDimension * STROKE_FRACTION
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = s * 0.4f
        val path = Path().apply {
            moveTo(cx + r * 0.3f, cy - r)
            cubicTo(cx - r * 0.8f, cy - r, cx - r, cy - r * 0.2f, cx - r, cy + r * 0.1f)
            cubicTo(cx - r, cy + r * 0.8f, cx - r * 0.3f, cy + r, cx + r * 0.3f, cy + r)
            cubicTo(cx + r * 0.9f, cy + r, cx + r, cy + r * 0.4f, cx + r, cy)
        }
        drawPath(path, tint, style = Stroke(width = strokeW * 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val arrowPath = Path().apply {
            moveTo(cx - r * 0.2f, cy - r - s * 0.15f)
            lineTo(cx + r * 0.3f, cy - r)
            lineTo(cx - r * 0.2f, cy - r + s * 0.15f)
        }
        drawPath(arrowPath, tint, style = Stroke(width = strokeW * 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ResetIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val s = size.minDimension * ICON_SCALE
        val strokeW = size.minDimension * STROKE_FRACTION
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = s * 0.4f
        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2, r * 2),
            style = Stroke(width = strokeW * 2.5f, cap = StrokeCap.Round)
        )
        val tipAngle = Math.toRadians(-90.0 + 300.0).toFloat()
        val tipX = cx + r * cos(tipAngle)
        val tipY = cy + r * sin(tipAngle)
        val arrowPath = Path().apply {
            moveTo(tipX - s * 0.12f, tipY - s * 0.12f)
            lineTo(tipX, tipY)
            lineTo(tipX + s * 0.12f, tipY - s * 0.05f)
        }
        drawPath(arrowPath, tint, style = Stroke(width = strokeW * 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun HintIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val s = size.minDimension * ICON_SCALE
        val strokeW = size.minDimension * STROKE_FRACTION
        val cx = size.width / 2f
        val cy = size.height / 2f
        val bulbR = s * 0.3f
        val path = Path().apply {
            moveTo(cx - bulbR * 0.55f, cy + bulbR * 0.3f)
            cubicTo(cx - bulbR * 1.2f, cy - bulbR * 0.3f, cx - bulbR * 0.8f, cy - bulbR * 1.4f, cx, cy - bulbR * 1.4f)
            cubicTo(cx + bulbR * 0.8f, cy - bulbR * 1.4f, cx + bulbR * 1.2f, cy - bulbR * 0.3f, cx + bulbR * 0.55f, cy + bulbR * 0.3f)
            lineTo(cx + bulbR * 0.55f, cy + bulbR * 0.8f)
            lineTo(cx - bulbR * 0.55f, cy + bulbR * 0.8f)
            close()
        }
        drawPath(path, tint)
        drawLine(tint, Offset(cx - bulbR * 0.4f, cy + bulbR * 1.1f), Offset(cx + bulbR * 0.4f, cy + bulbR * 1.1f), strokeWidth = strokeW * 2.5f, cap = StrokeCap.Round)
        drawLine(tint, Offset(cx - bulbR * 0.3f, cy + bulbR * 1.4f), Offset(cx + bulbR * 0.3f, cy + bulbR * 1.4f), strokeWidth = strokeW * 2.5f, cap = StrokeCap.Round)
    }
}

@Composable
fun GearIcon(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier = modifier) {
        val s = size.minDimension * 0.7f
        val strokeW = size.minDimension * STROKE_FRACTION
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerR = s * 0.4f
        val innerR = s * 0.2f
        val tickLen = s * 0.12f
        drawCircle(tint, outerR, Offset(cx, cy), style = Stroke(width = strokeW * 2f))
        drawCircle(tint, innerR, Offset(cx, cy), style = Stroke(width = strokeW * 2f))
        for (i in 0 until 4) {
            val angle = Math.toRadians(i * 90.0).toFloat()
            val startX = cx + (outerR - tickLen * 0.5f) * cos(angle)
            val startY = cy + (outerR - tickLen * 0.5f) * sin(angle)
            val endX = cx + (outerR + tickLen) * cos(angle)
            val endY = cy + (outerR + tickLen) * sin(angle)
            drawLine(tint, Offset(startX, startY), Offset(endX, endY), strokeWidth = strokeW * 2.5f, cap = StrokeCap.Round)
        }
    }
}

@Composable
fun LockIcon(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier = modifier) {
        val s = size.minDimension * ICON_SCALE
        val cx = size.width / 2f
        val cy = size.height / 2f
        val bodyW = s * 0.65f
        val bodyH = s * 0.45f
        val bodyTop = cy - bodyH * 0.1f
        drawRoundRect(tint, Offset(cx - bodyW / 2f, bodyTop), Size(bodyW, bodyH), CornerRadius(s * 0.06f))
        val shackleW = s * 0.4f
        val shackleH = s * 0.35f
        val strokeW = size.minDimension * STROKE_FRACTION * 2f
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - shackleW / 2f, bodyTop - shackleH),
            size = Size(shackleW, shackleH * 2f),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ChevronIcon(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        val strokeW = s * 0.18f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val halfH = s * 0.3f
        val halfW = s * 0.18f
        val path = Path().apply {
            moveTo(cx - halfW, cy - halfH)
            lineTo(cx + halfW, cy)
            lineTo(cx - halfW, cy + halfH)
        }
        drawPath(path, tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

fun DrawScope.drawStar(center: Offset, outerRadius: Float, color: Color) {
    val innerRadius = outerRadius * (STAR_INNER_RATIO / STAR_OUTER_RATIO)
    val path = Path()
    for (i in 0 until STAR_POINTS * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val angle = (Math.toRadians(-90.0 + i * 360.0 / (STAR_POINTS * 2))).toFloat()
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Add Canvas icon drawing utilities
```

---

### Task 3: Wire Canvas icons into Android game bottom bar

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/component/GameBottomBar.kt`

- [ ] **Step 1: Replace BottomCircleButton text with icon composables**

Replace the `Text` inside `BottomCircleButton` with a composable icon content lambda. Update the `GameBottomBar` to pass the appropriate icon composable for each button.

Rewrite `GameBottomBar.kt`:

```kotlin
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.erman.pegsolitaire.ui.theme.BadgeGray
import com.erman.pegsolitaire.ui.theme.BadgeOrange
import com.erman.pegsolitaire.ui.theme.BadgePurple
import com.erman.pegsolitaire.ui.theme.BadgeRed

private val BUTTON_SIZE = 48.dp
private val ICON_SIZE = 48.dp
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
                color = if (canUndo) BadgeGray else BadgeGray.copy(alpha = DISABLED_ALPHA),
                enabled = canUndo,
                onClick = onUndoClicked
            ) { UndoIcon(modifier = Modifier.size(ICON_SIZE)) }

            BottomCircleButton(
                color = BadgePurple,
                enabled = true,
                onClick = onPauseClicked
            ) {
                if (isPaused) PlayIcon(modifier = Modifier.size(ICON_SIZE))
                else PauseIcon(modifier = Modifier.size(ICON_SIZE))
            }

            BottomCircleButton(
                color = BadgeRed,
                enabled = true,
                onClick = onResetClicked
            ) { ResetIcon(modifier = Modifier.size(ICON_SIZE)) }

            BottomCircleButton(
                color = if (hintsEnabled) BadgeOrange.copy(alpha = ACTIVE_HINT_ALPHA) else BadgeOrange,
                enabled = true,
                onClick = onHintClicked
            ) { HintIcon(modifier = Modifier.size(ICON_SIZE)) }
        }
    }
}

@Composable
private fun BottomCircleButton(
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
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
        icon()
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Replace Unicode symbols with Canvas icons in game bottom bar
```

---

### Task 4: Redesign Android home screen

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/MenuScreen.kt`

- [ ] **Step 1: Rewrite MenuScreen.kt with the new warm light design**

Replace the entire file with the redesigned scrollable layout using board cards with thumbnails, Canvas icons, and warm background:

```kotlin
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.engine.BoardType
import com.erman.pegsolitaire.presentation.HomeViewModel
import com.erman.pegsolitaire.presentation.formatElapsedTime
import com.erman.pegsolitaire.ui.component.ChevronIcon
import com.erman.pegsolitaire.ui.component.GearIcon
import com.erman.pegsolitaire.ui.theme.CardBackgroundDark
import com.erman.pegsolitaire.ui.theme.CardBackgroundLight
import com.erman.pegsolitaire.ui.theme.ChevronGray
import com.erman.pegsolitaire.ui.theme.CompletedGradientStart
import com.erman.pegsolitaire.ui.theme.WarmBackground
import com.erman.pegsolitaire.ui.theme.thumbnailGradient

private val CARD_CORNER_RADIUS = 16.dp
private val CARD_ELEVATION = 2.dp
private val THUMBNAIL_SIZE = 44.dp
private val THUMBNAIL_CORNER_RADIUS = 12.dp
private val PEG_DOT_RADIUS = 9f
private val SETTINGS_BUTTON_SIZE = 36.dp
private val SETTINGS_ICON_SIZE = 20.dp
private val SETTINGS_CORNER_RADIUS = 10.dp
private val CHEVRON_SIZE = 16.dp
private val SCORE_FONT_SIZE = 12.sp
private val BOARD_NAME_FONT_SIZE = 15.sp
private const val NOT_PLAYED_TEXT = "Not played"
private const val SCORE_MIDDLE_DOT = " \u00B7 "

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
    val backgroundColor = if (isDark) MaterialTheme.colorScheme.background else WarmBackground
    val cardColor = if (isDark) CardBackgroundDark else CardBackgroundLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            SettingsButton(isDark = isDark, onClick = onSettingsClick)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Peg Solitaire",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Classic Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        BoardType.entries.forEach { boardType ->
            BoardCard(
                boardType = boardType,
                score = uiState.bestScores[boardType],
                cardColor = cardColor,
                onClick = { onClassicSelected(boardType) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Challenge Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        ChallengeCard(cardColor = cardColor, onClick = onChallengeSelected)
    }
}

@Composable
private fun SettingsButton(isDark: Boolean, onClick: () -> Unit) {
    val buttonColor = if (isDark) CardBackgroundDark else CardBackgroundLight
    val iconTint = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Gray

    Box(
        modifier = Modifier
            .size(SETTINGS_BUTTON_SIZE)
            .shadow(CARD_ELEVATION, RoundedCornerShape(SETTINGS_CORNER_RADIUS))
            .clip(RoundedCornerShape(SETTINGS_CORNER_RADIUS))
            .background(buttonColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        GearIcon(modifier = Modifier.size(SETTINGS_ICON_SIZE), tint = iconTint)
    }
}

@Composable
private fun BoardCard(
    boardType: BoardType,
    score: GameScore?,
    cardColor: Color,
    onClick: () -> Unit
) {
    val label = boardType.name.lowercase().replaceFirstChar { it.uppercase() }
    val (gradStart, gradEnd) = thumbnailGradient(boardType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoardThumbnail(startColor = gradStart, endColor = gradEnd)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = BOARD_NAME_FONT_SIZE,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = score?.let { "${it.remainingPegs} left$SCORE_MIDDLE_DOT${formatElapsedTime(it.elapsedTimeMillis)}" }
                        ?: NOT_PLAYED_TEXT,
                    fontSize = SCORE_FONT_SIZE,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            ChevronIcon(modifier = Modifier.size(CHEVRON_SIZE), tint = ChevronGray)
        }
    }
}

@Composable
private fun BoardThumbnail(startColor: Color, endColor: Color) {
    Box(
        modifier = Modifier
            .size(THUMBNAIL_SIZE)
            .clip(RoundedCornerShape(THUMBNAIL_CORNER_RADIUS))
            .background(Brush.linearGradient(
                listOf(startColor, endColor),
                start = Offset.Zero,
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            drawCircle(Color.White, radius = PEG_DOT_RADIUS)
        }
    }
}

@Composable
private fun ChallengeCard(cardColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Browse Levels",
                fontWeight = FontWeight.SemiBold,
                fontSize = BOARD_NAME_FONT_SIZE,
                color = CompletedGradientStart
            )
        }
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Redesign home screen with warm light theme and card list
```

---

### Task 5: Redesign Android level selector

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/ChallengeLevelSelectorScreen.kt`

- [ ] **Step 1: Rewrite ChallengeLevelSelectorScreen.kt with 3-column grid and gradient cells**

Key changes from current: 4-column → 3-column, square aspect ratio cells, completed cells get purple gradient, Canvas-drawn lock and star icons, warm background.

```kotlin
package com.erman.pegsolitaire.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erman.pegsolitaire.domain.model.LevelItem
import com.erman.pegsolitaire.presentation.ChallengeLevelSelectorViewModel
import com.erman.pegsolitaire.ui.component.LockIcon
import com.erman.pegsolitaire.ui.component.drawStar
import com.erman.pegsolitaire.ui.theme.CardBackgroundDark
import com.erman.pegsolitaire.ui.theme.CardBackgroundLight
import com.erman.pegsolitaire.ui.theme.CompletedGradientEnd
import com.erman.pegsolitaire.ui.theme.CompletedGradientStart
import com.erman.pegsolitaire.ui.theme.StarEmpty
import com.erman.pegsolitaire.ui.theme.StarGold
import com.erman.pegsolitaire.ui.theme.WarmBackground

private const val GRID_COLUMNS = 3
private val LEVEL_CELL_CORNER_RADIUS = 16.dp
private val GRID_SPACING = 12.dp
private const val LOAD_MORE_THRESHOLD = 10
private const val MAX_STARS = 3
private const val LOCKED_ALPHA = 0.35f
private val LEVEL_NUMBER_SIZE = 20.sp
private val STAR_ROW_SIZE = 36.dp
private val STAR_RADIUS = 6f
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
    val backgroundColor = if (isDark) MaterialTheme.colorScheme.background else WarmBackground

    LaunchedEffect(Unit) {
        viewModel.loadInitialLevels()
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
            LevelCell(level = level, isDark = isDark, onClick = { onLevelSelected(level.levelNumber) })
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
    val isCompleted = level.stars > 0
    val cellColor = if (isDark) CardBackgroundDark else CardBackgroundLight

    val backgroundModifier = if (isCompleted) {
        Modifier.background(
            Brush.linearGradient(
                colors = listOf(CompletedGradientStart, CompletedGradientEnd),
                start = Offset.Zero,
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
    } else {
        Modifier.background(cellColor)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(LEVEL_CELL_CORNER_RADIUS))
            .then(backgroundModifier)
            .then(
                if (level.isLocked) Modifier.alpha(LOCKED_ALPHA)
                else Modifier.clickable(onClick = onClick)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (level.isLocked) {
                LockIcon(
                    modifier = Modifier.size(LOCK_ICON_SIZE),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = level.levelNumber.toString(),
                fontSize = LEVEL_NUMBER_SIZE,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurface
            )

            if (isCompleted) {
                Spacer(modifier = Modifier.height(4.dp))
                StarRow(stars = level.stars)
            }
        }
    }
}

@Composable
private fun StarRow(stars: Int) {
    Canvas(modifier = Modifier.size(STAR_ROW_SIZE, 14.dp)) {
        val spacing = size.width / MAX_STARS
        val startX = (size.width - spacing * (MAX_STARS - 1)) / 2f
        repeat(MAX_STARS) { index ->
            val color = if (index < stars) StarGold else Color.White.copy(alpha = 0.4f)
            drawStar(
                center = Offset(startX + index * spacing, size.height / 2f),
                outerRadius = STAR_RADIUS,
                color = color
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
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Redesign level selector with 3-column grid and gradient cells
```

---

### Task 6: Fix Android pause overlay

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/GameScreen.kt`

- [ ] **Step 1: Add clickable to the Canvas in PauseOverlay**

In `GameScreen.kt`, find the `PauseOverlay` composable (around line 228-251). Add `.clickable` to the Canvas modifier so taps on the play triangle also trigger resume:

Change the Canvas inside PauseOverlay from:

```kotlin
Canvas(modifier = Modifier.size(PLAY_ICON_SIZE)) {
```

To:

```kotlin
Canvas(
    modifier = Modifier
        .size(PLAY_ICON_SIZE)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onResume
        )
) {
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Fix pause overlay play icon not responding to taps
```

---

### Task 7: Redesign iOS home screen

**Files:**
- Modify: `iosApp/iosApp/Screen/MenuView.swift`

- [ ] **Step 1: Rewrite MenuView.swift with warm light design**

Rewrite to match the Android redesign: scrollable layout, board cards with gradient thumbnails, Canvas gear and chevron icons, warm background.

```swift
import SwiftUI
import Shared

private let cardCornerRadius: CGFloat = 16
private let cardElevation: CGFloat = 2
private let thumbnailSize: CGFloat = 44
private let thumbnailCornerRadius: CGFloat = 12
private let pegDotRadius: CGFloat = 9
private let settingsButtonSize: CGFloat = 36
private let settingsIconSize: CGFloat = 20
private let settingsCornerRadius: CGFloat = 10
private let chevronSize: CGFloat = 16
private let scoreFontSize: CGFloat = 12
private let boardNameFontSize: CGFloat = 15
private let notPlayedText = "Not played"
private let scoreMiddleDot = " \u{00B7} "

private let millisPerSecond: Int64 = 1000
private let secondsPerMinute: Int64 = 60

struct MenuView: View {
    let bestScoreFor: (BoardType) -> GameScore?
    let onClassicSelected: (BoardType) -> Void
    let onChallengeSelected: () -> Void
    let onSettingsClick: () -> Void

    @Environment(\.colorScheme) private var colorScheme

    private var backgroundColor: Color {
        colorScheme == .dark ? Color(.systemBackground) : warmBackground
    }

    private var cardColor: Color {
        colorScheme == .dark ? Color(.secondarySystemBackground) : .white
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    Spacer()
                    SettingsButton(colorScheme: colorScheme, action: onSettingsClick)
                }

                Text("Peg Solitaire")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .foregroundColor(.pink)
                    .padding(.top, 8)

                Text("Classic Mode")
                    .font(.headline)
                    .fontWeight(.semibold)
                    .padding(.top, 24)
                    .padding(.bottom, 12)

                ForEach(BoardType.entries, id: \.name) { boardType in
                    BoardCard(
                        boardType: boardType,
                        score: bestScoreFor(boardType),
                        cardColor: cardColor,
                        action: { onClassicSelected(boardType) }
                    )
                    .padding(.bottom, 10)
                }

                Text("Challenge Mode")
                    .font(.headline)
                    .fontWeight(.semibold)
                    .padding(.top, 16)
                    .padding(.bottom, 12)

                ChallengeCard(cardColor: cardColor, action: onChallengeSelected)
            }
            .padding(24)
        }
        .background(backgroundColor.ignoresSafeArea())
    }
}

private struct SettingsButton: View {
    let colorScheme: ColorScheme
    let action: () -> Void

    private var buttonColor: Color {
        colorScheme == .dark ? Color(.secondarySystemBackground) : .white
    }

    private var iconTint: Color {
        colorScheme == .dark ? .white.opacity(0.7) : .gray
    }

    var body: some View {
        Button(action: action) {
            Canvas { context, size in
                drawGearIcon(context: context, size: size, tint: iconTint)
            }
            .frame(width: settingsIconSize, height: settingsIconSize)
            .frame(width: settingsButtonSize, height: settingsButtonSize)
            .background(buttonColor)
            .cornerRadius(settingsCornerRadius)
            .shadow(radius: cardElevation)
        }
    }
}

private struct BoardCard: View {
    let boardType: BoardType
    let score: GameScore?
    let cardColor: Color
    let action: () -> Void

    private var label: String {
        boardType.name.lowercased().capitalized
    }

    private var scoreText: String {
        guard let score = score else { return notPlayedText }
        let totalSeconds = score.elapsedTimeMillis / millisPerSecond
        let minutes = totalSeconds / secondsPerMinute
        let seconds = totalSeconds % secondsPerMinute
        return "\(score.remainingPegs) left\(scoreMiddleDot)\(String(format: "%02d:%02d", minutes, seconds))"
    }

    var body: some View {
        Button(action: action) {
            HStack(spacing: 14) {
                BoardThumbnail(boardType: boardType)

                VStack(alignment: .leading, spacing: 2) {
                    Text(label)
                        .font(.system(size: boardNameFontSize, weight: .semibold))
                        .foregroundColor(.primary)
                    Text(scoreText)
                        .font(.system(size: scoreFontSize))
                        .foregroundColor(.secondary)
                }

                Spacer()

                Canvas { context, size in
                    drawChevronIcon(context: context, size: size, tint: chevronGray)
                }
                .frame(width: chevronSize, height: chevronSize)
            }
            .padding(14)
            .background(cardColor)
            .cornerRadius(cardCornerRadius)
            .shadow(radius: cardElevation)
        }
        .buttonStyle(.plain)
    }
}

private struct BoardThumbnail: View {
    let boardType: BoardType

    var body: some View {
        let gradient = thumbnailGradient(boardType: boardType)
        ZStack {
            RoundedRectangle(cornerRadius: thumbnailCornerRadius)
                .fill(LinearGradient(
                    colors: [gradient.start, gradient.end],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                ))
            Circle()
                .fill(.white)
                .frame(width: 18, height: 18)
        }
        .frame(width: thumbnailSize, height: thumbnailSize)
    }
}

private struct ChallengeCard: View {
    let cardColor: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text("Browse Levels")
                .font(.system(size: boardNameFontSize, weight: .semibold))
                .foregroundColor(completedGradientStart)
                .frame(maxWidth: .infinity)
                .padding(18)
                .background(cardColor)
                .cornerRadius(cardCornerRadius)
                .shadow(radius: cardElevation)
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Canvas Icon Drawing

private func drawGearIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height)
    let cx = size.width / 2
    let cy = size.height / 2
    let outerR = s * 0.4
    let innerR = s * 0.2
    let tickLen = s * 0.12
    let strokeW = s * 0.12

    context.stroke(Path(ellipseIn: CGRect(x: cx - outerR, y: cy - outerR, width: outerR * 2, height: outerR * 2)), with: .color(tint), lineWidth: strokeW)
    context.stroke(Path(ellipseIn: CGRect(x: cx - innerR, y: cy - innerR, width: innerR * 2, height: innerR * 2)), with: .color(tint), lineWidth: strokeW)
    for i in 0..<4 {
        let angle = Double(i) * .pi / 2
        let startX = cx + (outerR - tickLen * 0.5) * cos(angle)
        let startY = cy + (outerR - tickLen * 0.5) * sin(angle)
        let endX = cx + (outerR + tickLen) * cos(angle)
        let endY = cy + (outerR + tickLen) * sin(angle)
        var path = Path()
        path.move(to: CGPoint(x: startX, y: startY))
        path.addLine(to: CGPoint(x: endX, y: endY))
        context.stroke(path, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round))
    }
}

private func drawChevronIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height)
    let cx = size.width / 2
    let cy = size.height / 2
    let halfH = s * 0.3
    let halfW = s * 0.18
    let strokeW = s * 0.18
    var path = Path()
    path.move(to: CGPoint(x: cx - halfW, y: cy - halfH))
    path.addLine(to: CGPoint(x: cx + halfW, y: cy))
    path.addLine(to: CGPoint(x: cx - halfW, y: cy + halfH))
    context.stroke(path, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round, lineJoin: .round))
}
```

- [ ] **Step 2: Verify iOS build**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL (verifies shared module; full iOS build requires Xcode)

- [ ] **Step 3: Commit**

```
Redesign iOS home screen with warm light theme and card list
```

---

### Task 8: Create iOS Canvas icon drawing utilities and wire into game bar

**Files:**
- Create: `iosApp/iosApp/Component/IconDrawing.swift`
- Modify: `iosApp/iosApp/Screen/GameView.swift`

- [ ] **Step 1: Create IconDrawing.swift with all icon drawing functions**

Create `iosApp/iosApp/Component/IconDrawing.swift` with functions matching the Android icon composables. Each function takes a `GraphicsContext`, `CGSize`, and `tint: Color`.

```swift
import SwiftUI

private let iconScale: CGFloat = 0.55
private let strokeFraction: CGFloat = 0.08
private let starPoints = 5

func drawPauseIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height) * iconScale
    let barWidth = s * 0.22
    let barHeight = s * 0.8
    let gap = s * 0.18
    let left = (size.width - barWidth * 2 - gap) / 2
    let top = (size.height - barHeight) / 2
    let r = barWidth * 0.3
    context.fill(Path(roundedRect: CGRect(x: left, y: top, width: barWidth, height: barHeight), cornerRadius: r), with: .color(tint))
    context.fill(Path(roundedRect: CGRect(x: left + barWidth + gap, y: top, width: barWidth, height: barHeight), cornerRadius: r), with: .color(tint))
}

func drawPlayIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height) * iconScale
    let cx = size.width / 2
    let cy = size.height / 2
    var path = Path()
    path.move(to: CGPoint(x: cx - s * 0.25, y: cy - s * 0.45))
    path.addLine(to: CGPoint(x: cx + s * 0.4, y: cy))
    path.addLine(to: CGPoint(x: cx - s * 0.25, y: cy + s * 0.45))
    path.closeSubpath()
    context.fill(path, with: .color(tint))
}

func drawUndoIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height) * iconScale
    let strokeW = min(size.width, size.height) * strokeFraction * 2.5
    let cx = size.width / 2
    let cy = size.height / 2
    let r = s * 0.4

    var arcPath = Path()
    arcPath.move(to: CGPoint(x: cx + r * 0.3, y: cy - r))
    arcPath.addCurve(
        to: CGPoint(x: cx - r, y: cy + r * 0.1),
        control1: CGPoint(x: cx - r * 0.8, y: cy - r),
        control2: CGPoint(x: cx - r, y: cy - r * 0.2)
    )
    arcPath.addCurve(
        to: CGPoint(x: cx + r * 0.3, y: cy + r),
        control1: CGPoint(x: cx - r, y: cy + r * 0.8),
        control2: CGPoint(x: cx - r * 0.3, y: cy + r)
    )
    arcPath.addCurve(
        to: CGPoint(x: cx + r, y: cy),
        control1: CGPoint(x: cx + r * 0.9, y: cy + r),
        control2: CGPoint(x: cx + r, y: cy + r * 0.4)
    )
    context.stroke(arcPath, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round, lineJoin: .round))

    var arrowPath = Path()
    arrowPath.move(to: CGPoint(x: cx - r * 0.2, y: cy - r - s * 0.15))
    arrowPath.addLine(to: CGPoint(x: cx + r * 0.3, y: cy - r))
    arrowPath.addLine(to: CGPoint(x: cx - r * 0.2, y: cy - r + s * 0.15))
    context.stroke(arrowPath, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round, lineJoin: .round))
}

func drawResetIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height) * iconScale
    let strokeW = min(size.width, size.height) * strokeFraction * 2.5
    let cx = size.width / 2
    let cy = size.height / 2
    let r = s * 0.4

    var arcPath = Path()
    arcPath.addArc(center: CGPoint(x: cx, y: cy), radius: r, startAngle: .degrees(-90), endAngle: .degrees(210), clockwise: false)
    context.stroke(arcPath, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round))

    let tipAngle = 210.0 * .pi / 180.0
    let tipX = cx + r * cos(tipAngle)
    let tipY = cy + r * sin(tipAngle)
    var arrowPath = Path()
    arrowPath.move(to: CGPoint(x: tipX - s * 0.12, y: tipY - s * 0.12))
    arrowPath.addLine(to: CGPoint(x: tipX, y: tipY))
    arrowPath.addLine(to: CGPoint(x: tipX + s * 0.12, y: tipY - s * 0.05))
    context.stroke(arrowPath, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round, lineJoin: .round))
}

func drawHintIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height) * iconScale
    let strokeW = min(size.width, size.height) * strokeFraction * 2.5
    let cx = size.width / 2
    let cy = size.height / 2
    let bulbR = s * 0.3

    var bulbPath = Path()
    bulbPath.move(to: CGPoint(x: cx - bulbR * 0.55, y: cy + bulbR * 0.3))
    bulbPath.addCurve(to: CGPoint(x: cx, y: cy - bulbR * 1.4), control1: CGPoint(x: cx - bulbR * 1.2, y: cy - bulbR * 0.3), control2: CGPoint(x: cx - bulbR * 0.8, y: cy - bulbR * 1.4))
    bulbPath.addCurve(to: CGPoint(x: cx + bulbR * 0.55, y: cy + bulbR * 0.3), control1: CGPoint(x: cx + bulbR * 0.8, y: cy - bulbR * 1.4), control2: CGPoint(x: cx + bulbR * 1.2, y: cy - bulbR * 0.3))
    bulbPath.addLine(to: CGPoint(x: cx + bulbR * 0.55, y: cy + bulbR * 0.8))
    bulbPath.addLine(to: CGPoint(x: cx - bulbR * 0.55, y: cy + bulbR * 0.8))
    bulbPath.closeSubpath()
    context.fill(bulbPath, with: .color(tint))

    var line1 = Path()
    line1.move(to: CGPoint(x: cx - bulbR * 0.4, y: cy + bulbR * 1.1))
    line1.addLine(to: CGPoint(x: cx + bulbR * 0.4, y: cy + bulbR * 1.1))
    context.stroke(line1, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round))

    var line2 = Path()
    line2.move(to: CGPoint(x: cx - bulbR * 0.3, y: cy + bulbR * 1.4))
    line2.addLine(to: CGPoint(x: cx + bulbR * 0.3, y: cy + bulbR * 1.4))
    context.stroke(line2, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round))
}

func drawLockIcon(context: GraphicsContext, size: CGSize, tint: Color) {
    let s = min(size.width, size.height) * iconScale
    let cx = size.width / 2
    let cy = size.height / 2
    let bodyW = s * 0.65
    let bodyH = s * 0.45
    let bodyTop = cy - bodyH * 0.1
    let bodyR = s * 0.06
    context.fill(Path(roundedRect: CGRect(x: cx - bodyW / 2, y: bodyTop, width: bodyW, height: bodyH), cornerRadius: bodyR), with: .color(tint))

    let shackleW = s * 0.4
    let shackleH = s * 0.35
    let strokeW = min(size.width, size.height) * strokeFraction * 2
    var shacklePath = Path()
    shacklePath.addArc(center: CGPoint(x: cx, y: bodyTop), radius: shackleW / 2, startAngle: .degrees(180), endAngle: .degrees(0), clockwise: false)
    context.stroke(shacklePath, with: .color(tint), style: StrokeStyle(lineWidth: strokeW, lineCap: .round))
}

func drawStarIcon(context: GraphicsContext, center: CGPoint, outerRadius: CGFloat, color: Color) {
    let innerRadius = outerRadius * 0.4
    var path = Path()
    for i in 0..<(starPoints * 2) {
        let r = i % 2 == 0 ? outerRadius : innerRadius
        let angle = -(.pi / 2) + Double(i) * .pi / Double(starPoints)
        let x = center.x + r * cos(angle)
        let y = center.y + r * sin(angle)
        if i == 0 {
            path.move(to: CGPoint(x: x, y: y))
        } else {
            path.addLine(to: CGPoint(x: x, y: y))
        }
    }
    path.closeSubpath()
    context.fill(path, with: .color(color))
}
```

- [ ] **Step 2: Update GameBottomBarView in GameView.swift to use Canvas icons**

In `GameView.swift`, replace the `BottomCircleButton` struct's `Text(symbol)` with `Canvas` drawing. Replace the struct:

```swift
private struct BottomCircleButton: View {
    let drawIcon: (GraphicsContext, CGSize) -> Void
    let color: Color
    var isDisabled: Bool = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Canvas { context, size in
                drawIcon(context, size)
            }
            .frame(width: bottomButtonSize, height: bottomButtonSize)
            .background(color)
            .clipShape(Circle())
        }
        .disabled(isDisabled)
    }
}
```

Update `GameBottomBarView` to use the new button:

```swift
private struct GameBottomBarView: View {
    let canUndo: Bool
    let isPaused: Bool
    let hintsEnabled: Bool
    let onUndo: () -> Void
    let onPause: () -> Void
    let onReset: () -> Void
    let onHint: () -> Void

    var body: some View {
        HStack(spacing: 16) {
            BottomCircleButton(
                drawIcon: { ctx, size in drawUndoIcon(context: ctx, size: size, tint: .white) },
                color: canUndo ? badgeGray : badgeGray.opacity(disabledAlpha),
                isDisabled: !canUndo,
                action: onUndo
            )

            BottomCircleButton(
                drawIcon: { ctx, size in
                    if isPaused {
                        drawPlayIcon(context: ctx, size: size, tint: .white)
                    } else {
                        drawPauseIcon(context: ctx, size: size, tint: .white)
                    }
                },
                color: badgePurple,
                action: onPause
            )

            BottomCircleButton(
                drawIcon: { ctx, size in drawResetIcon(context: ctx, size: size, tint: .white) },
                color: badgeRed,
                action: onReset
            )

            BottomCircleButton(
                drawIcon: { ctx, size in drawHintIcon(context: ctx, size: size, tint: .white) },
                color: hintsEnabled ? badgeOrange.opacity(activeHintAlpha) : badgeOrange,
                action: onHint
            )
        }
        .padding(.horizontal, barHPadding)
        .padding(.vertical, barVPadding)
    }
}
```

Also remove the old Unicode constants from the file top: `pauseSymbol`, `playSymbol`, `hintSymbol`.

- [ ] **Step 3: Commit**

```
Add iOS Canvas icon drawing and wire into game bottom bar
```

---

### Task 9: Fix iOS pause overlay and game bar alignment

**Files:**
- Modify: `iosApp/iosApp/Screen/GameView.swift`

- [ ] **Step 1: Fix pause overlay — add allowsHitTesting(false) to Canvas**

In `PauseOverlayView`, add `.allowsHitTesting(false)` to the Canvas:

```swift
private struct PauseOverlayView: View {
    let onResume: () -> Void

    var body: some View {
        Color.black.opacity(overlayAlpha)
            .ignoresSafeArea()
            .onTapGesture(perform: onResume)
            .overlay {
                Canvas { context, size in
                    let path = Path { p in
                        p.move(to: CGPoint(x: size.width * 0.2, y: 0))
                        p.addLine(to: CGPoint(x: size.width, y: size.height / 2))
                        p.addLine(to: CGPoint(x: size.width * 0.2, y: size.height))
                        p.closeSubpath()
                    }
                    context.fill(path, with: .color(.white.opacity(0.9)))
                }
                .frame(width: playIconSize, height: playIconSize)
                .allowsHitTesting(false)
            }
    }
}
```

- [ ] **Step 2: Fix game bar alignment — add Spacers around BoardView**

In `GameContentView`, add `Spacer()` above and below `BoardView` so bars pin to screen edges:

Change the VStack inside `GameContentView.body` from:

```swift
VStack {
    GameTopBarView(scoreText: scoreText, timeText: timeText)

    BoardView(
        board: state.board,
        ...
    )
    .padding()

    GameBottomBarView(...)
}
```

To:

```swift
VStack {
    GameTopBarView(scoreText: scoreText, timeText: timeText)

    Spacer()

    BoardView(
        board: state.board,
        ...
    )
    .padding()

    Spacer()

    GameBottomBarView(...)
}
```

- [ ] **Step 3: Commit**

```
Fix iOS pause overlay tap and align game bars to screen edges
```

---

### Task 10: Redesign iOS level selector

**Files:**
- Modify: `iosApp/iosApp/Screen/ChallengeLevelSelectorView.swift`

- [ ] **Step 1: Rewrite ChallengeLevelSelectorView.swift with 3-column grid and gradient cells**

Key changes: 4-column → 3-column, square cells, completed cells get purple gradient, Canvas stars and lock icons, warm background.

```swift
import SwiftUI
import Shared

private let gridColumns = 3
private let gridSpacing: CGFloat = 12
private let cellCornerRadius: CGFloat = 16
private let loadMoreThreshold = 10
private let maxStars = 3
private let lockedOpacity = 0.35
private let levelNumberSize: CGFloat = 20
private let starRowWidth: CGFloat = 36
private let starRowHeight: CGFloat = 14
private let starRadius: CGFloat = 6
private let lockIconSize: CGFloat = 20

struct ChallengeLevelSelectorView: View {
    let onLevelSelected: (Int32) -> Void
    let onBack: () -> Void

    @StateObject private var viewModel = ChallengeLevelSelectorViewModelWrapper()
    @Environment(\.colorScheme) private var colorScheme

    private let columns = Array(
        repeating: GridItem(.flexible(), spacing: gridSpacing),
        count: gridColumns
    )

    private var backgroundColor: Color {
        colorScheme == .dark ? Color(.systemBackground) : warmBackground
    }

    var body: some View {
        VStack(spacing: 0) {
            LevelSelectorTopBar()

            if let error = viewModel.uiState.error, viewModel.uiState.levels.isEmpty {
                Spacer()
                ErrorContent(message: error, onRetry: viewModel.loadInitialLevels)
                Spacer()
            } else if viewModel.uiState.isLoading {
                Spacer()
                ProgressView()
                Spacer()
            } else {
                LevelGrid(
                    levels: viewModel.uiState.levels,
                    isLoadingMore: viewModel.uiState.isLoadingMore,
                    columns: columns,
                    colorScheme: colorScheme,
                    onLevelSelected: onLevelSelected,
                    onLoadMore: viewModel.loadMoreLevels
                )
            }
        }
        .background(backgroundColor.ignoresSafeArea())
        .onAppear { viewModel.loadInitialLevels() }
    }
}

private struct LevelSelectorTopBar: View {
    var body: some View {
        Text("Challenge Levels")
            .font(.title2)
            .fontWeight(.bold)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal)
            .padding(.vertical, 12)
    }
}

private struct LevelGrid: View {
    let levels: [LevelItem]
    let isLoadingMore: Bool
    let columns: [GridItem]
    let colorScheme: ColorScheme
    let onLevelSelected: (Int32) -> Void
    let onLoadMore: () -> Void

    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: gridSpacing) {
                ForEach(levels, id: \.levelNumber) { level in
                    LevelCell(level: level, colorScheme: colorScheme) {
                        onLevelSelected(level.levelNumber)
                    }
                    .onAppear {
                        if level.levelNumber >= levels.last?.levelNumber ?? 0 - Int32(loadMoreThreshold) {
                            onLoadMore()
                        }
                    }
                }

                if isLoadingMore {
                    Section {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .padding()
                    }
                }
            }
            .padding(gridSpacing)
        }
    }
}

private struct LevelCell: View {
    let level: LevelItem
    let colorScheme: ColorScheme
    let onTap: () -> Void

    private var isCompleted: Bool { level.stars > 0 }

    private var cellColor: Color {
        colorScheme == .dark ? Color(.secondarySystemBackground) : .white
    }

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: 4) {
                if level.isLocked {
                    Canvas { context, size in
                        drawLockIcon(context: context, size: size, tint: .gray)
                    }
                    .frame(width: lockIconSize, height: lockIconSize)
                }

                Text("\(level.levelNumber)")
                    .font(.system(size: levelNumberSize, weight: .bold))
                    .foregroundColor(isCompleted ? .white : .primary)

                if isCompleted {
                    Canvas { context, size in
                        let spacing = size.width / CGFloat(maxStars)
                        let startX = (size.width - spacing * CGFloat(maxStars - 1)) / 2
                        for i in 0..<maxStars {
                            let color: Color = i < Int(level.stars) ? starGold : .white.opacity(0.4)
                            drawStarIcon(
                                context: context,
                                center: CGPoint(x: startX + CGFloat(i) * spacing, y: size.height / 2),
                                outerRadius: starRadius,
                                color: color
                            )
                        }
                    }
                    .frame(width: starRowWidth, height: starRowHeight)
                }
            }
            .frame(maxWidth: .infinity)
            .aspectRatio(1, contentMode: .fit)
            .background(
                isCompleted
                    ? AnyShapeStyle(LinearGradient(
                        colors: [completedGradientStart, completedGradientEnd],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    ))
                    : AnyShapeStyle(cellColor)
            )
            .cornerRadius(cellCornerRadius)
            .shadow(radius: isCompleted ? 4 : 1)
        }
        .disabled(level.isLocked)
        .opacity(level.isLocked ? lockedOpacity : 1.0)
        .buttonStyle(.plain)
    }
}

private struct ErrorContent: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text(message)
                .foregroundColor(.red)
            Button("Retry", action: onRetry)
        }
    }
}
```

- [ ] **Step 2: Commit**

```
Redesign iOS level selector with 3-column grid and gradient cells
```

---

### Task 11: Final verification and cleanup

**Files:**
- Possibly modify: `composeApp/src/androidMain/res/drawable/ic_lock.xml` (can be deleted, no longer referenced)

- [ ] **Step 1: Run Android compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Run shared tests**

Run: `./gradlew :shared:testDebugUnitTest`
Expected: All tests pass (no ViewModel/domain changes, so tests should be unaffected)

- [ ] **Step 3: Run iOS compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Delete unused ic_lock.xml drawable**

Delete `composeApp/src/androidMain/res/drawable/ic_lock.xml` — replaced by Canvas-drawn lock icon.

- [ ] **Step 5: Verify no remaining Unicode icon references**

Search for old Unicode symbols in the UI code to ensure all were replaced:
- Search for `\u2016`, `\u25B6`, `\u21A9`, `\u21BB`, `\uD83D\uDCA1`, `\u2699`, `\u2605`, `\u2606` in `composeApp/` and `iosApp/`
- These should only remain in non-UI code (e.g., the shared `SCORE_SEPARATOR` is fine)

- [ ] **Step 6: Commit**

```
Remove unused icon resources and verify build
```
