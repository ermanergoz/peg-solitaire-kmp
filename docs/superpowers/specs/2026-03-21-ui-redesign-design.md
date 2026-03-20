# UI Redesign

## Overview

Full visual redesign of home screen and level selector, new Material-style vector icons replacing Unicode text, pause overlay bug fix, and iOS game bar alignment fix.

## 1. Material-Style Vector Icons

Replace all Unicode text icons with Canvas/Path-drawn vector icons on both platforms. Clean rounded strokes, consistent weight (2.5-3dp stroke), white fill on colored circle backgrounds.

### Icon Definitions

All icons drawn inside their existing circle button (48dp Android, 48pt iOS). Icon paths occupy roughly 60% of the button diameter (~28dp drawing area).

| Icon | Shape Description |
|------|-------------------|
| Pause | Two rounded vertical bars, centered, ~5dp wide, ~20dp tall, ~11dp gap |
| Play | Right-pointing triangle, slightly left-offset for optical centering |
| Undo | Curved counter-clockwise arrow with arrowhead at top-left |
| Reset | Circular clockwise arrow (270-degree arc) with arrowhead |
| Hint | Lightbulb silhouette: rounded top dome + two small horizontal lines at base |
| Settings | Gear: outer circle with 4 tick marks (N/S/E/W) + inner circle. Drawn at 20dp inside the 36dp settings button container (see Section 2) |
| Lock | Rounded rectangle body + U-shaped shackle on top |
| Chevron | Right-pointing angle bracket (>), 2.5dp stroke, rounded caps, ~10dp tall, ~6dp wide |
| Star (filled) | 5-point star, solid fill, `StarGold` color |
| Star (empty) | 5-point star, same path, `StarEmpty` fill |

### Named Colors (added to Color.kt / file-level constants in Swift)

These are cross-package colors, added to the existing theme color files:

| Name | Hex | Usage |
|------|-----|-------|
| `WarmBackground` | `#F5F0EB` | Home/level selector background (light mode) |
| `CompletedGradientStart` | `#6C63FF` | Purple gradient start for completed levels, challenge card accent |
| `CompletedGradientEnd` | `#8B7FFF` | Purple gradient end for completed levels |
| `StarGold` | `#F59E0B` | Filled star color |
| `StarEmpty` | `#DDDDDD` | Empty star color |
| `ChevronGray` | `#CCCCCC` | Chevron icon tint |
| `ThumbnailEnglishStart/End` | `#A8D8EA` / `#7EC8E3` | English board thumbnail gradient (135-degree) |
| `ThumbnailFrenchStart/End` | `#F8B4C8` / `#F29DAE` | French board thumbnail gradient (135-degree) |
| `ThumbnailGermanStart/End` | `#F5E6CA` / `#E8D5B0` | German board thumbnail gradient (135-degree) |
| `ThumbnailAsymmetricStart/End` | `#B4E6C8` / `#8FD4AA` | Asymmetric board thumbnail gradient (135-degree) |
| `ThumbnailDiamondStart/End` | `#C8B4F0` / `#B09DE0` | Diamond board thumbnail gradient (135-degree) |

### Implementation Approach

- **Android:** Replace `Text(symbol)` in `BottomCircleButton` with `Canvas` drawing `Path` commands. Extract each icon as a private composable function (e.g., `DrawPauseIcon`, `DrawPlayIcon`).
- **iOS:** Replace `Text(symbol)` in `BottomCircleButton` with SwiftUI `Canvas` drawing `Path`. Extract each icon as a private helper function.
- **Settings icon:** Android replaces `Text("\u2699")` with a `Canvas` gear icon inside `IconButton`. iOS replaces `Image(systemName: "gearshape")` with a `Canvas` gear icon.
- **Lock icon:** Android replaces `painterResource(R.drawable.ic_lock)` with Canvas-drawn lock. iOS replaces `Image(systemName: "lock.fill")` with Canvas-drawn lock.
- **Stars:** Android replaces Unicode star text with Canvas-drawn star paths. iOS replaces SF Symbol star images with Canvas-drawn star paths.

## 2. Home Screen Redesign

### Visual Identity

- Background: `WarmBackground` (light mode), existing `MaterialTheme.colorScheme.background` / `Color(.systemBackground)` (dark mode)
- Cards: white (light) / surface color (dark), 16dp corner radius, soft shadow (2dp elevation Android / 8px blur at 6% opacity iOS)
- Typography: bold section headings, semibold card titles

### Layout (Scrollable)

```
[StatusBarPadding]
[Settings gear icon .................. top-right]

[Title: "Peg Solitaire" .............. bold, large, left-aligned]

[Section label: "Classic Mode" ....... semibold heading]
[Board card: colored thumbnail | "English" | "3 left · 02:45" | chevron]
[Board card: colored thumbnail | "French"  | "Not played"     | chevron]
[Board card: colored thumbnail | "German"  | "5 left · 04:12" | chevron]
[Board card: colored thumbnail | "Asymmetric" | "Not played"  | chevron]
[Board card: colored thumbnail | "Diamond" | "Not played"     | chevron]

[Section label: "Challenge Mode" ..... semibold heading]
[Challenge card: "Browse Levels" ..... purple accent, centered]

[NavigationBarPadding]
```

### Board Card Component

Each classic mode row is a tappable white rounded card. Tapping triggers the existing `onClassicSelected(boardType)` / `onBoardSelected(boardType)` navigation callback.

Contents:
- **Left:** 44dp rounded-square (12dp radius) thumbnail with gradient from the board's named thumbnail colors. Contains a centered 18dp white circle (abstract peg representation).
- **Center:** Board name (semibold 15sp) + score subtitle (caption 12sp, gray). Score format uses the existing `formatElapsedTime()` function: `"$remainingPegs left · MM:SS"`. If no score exists in `bestScores`, show "Not played" in gray. This is a change from current behavior where no subtitle is shown when there's no score.
- **Right:** Canvas-drawn chevron icon, `ChevronGray` color.

### Challenge Card

Full-width white card, centered text. "Browse Levels" in `CompletedGradientStart` as title. No subtitle (avoids needing ViewModel changes for a completed count). Tapping triggers existing `onChallengeSelected()` callback.

### Scrollability

- **Android:** Wrap the `Column` content in `verticalScroll(rememberScrollState())`. Remove `Arrangement.Center`. Content flows top-to-bottom naturally.
- **iOS:** Wrap `VStack` content in `ScrollView`. Remove any centering that prevents natural flow.

### Settings Button

Top-right corner, 36dp touchable area (intentionally smaller than the 48dp game bar buttons — this is a navigation icon, not a game control). Rounded-square (10dp radius) white background with subtle shadow. Canvas-drawn gear icon at 20dp inside. Tapping triggers existing `onSettingsClick()` callback.

## 3. Level Selector Redesign

### Layout Change

Switch from 4-column to 3-column grid. Cells are square aspect ratio (width determined by grid, height equals width).

### Cell States

**Completed (stars > 0):**
- Background: linear gradient from `CompletedGradientStart` to `CompletedGradientEnd` (135-degree angle)
- White bold level number (20sp)
- Canvas-drawn stars below number, always 3 stars total (filled = `StarGold`, unfilled = white at 40% opacity)
- Corner radius: 16dp

**Unlocked (stars == 0, not locked):**
- White background (light) / surface color (dark)
- Dark bold level number (20sp)
- Soft shadow (same as home screen cards)
- Corner radius: 16dp

**Locked:**
- White background (light) / surface color (dark)
- 35% opacity on entire cell
- Canvas-drawn lock icon above level number
- Gray level number
- Corner radius: 16dp

### Grid Spacing

12dp between cells, 12dp padding around grid.

### Top Bar

Keep current: "Challenge Levels" title, left-aligned, bold.

## 4. Pause Overlay Bug Fix

### Problem

The centered play triangle (Canvas) intercepts touch events, preventing the tap from reaching the overlay's resume handler. Tapping the background works, but tapping the triangle does not.

### Fix

**iOS:** Add `.allowsHitTesting(false)` to the Canvas overlay so taps pass through to the `Color.black` layer's `onTapGesture`.

```swift
// Before
.overlay {
    Canvas { ... }
        .frame(width: playIconSize, height: playIconSize)
}

// After
.overlay {
    Canvas { ... }
        .frame(width: playIconSize, height: playIconSize)
        .allowsHitTesting(false)
}
```

**Android:** Add `.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onResume)` to the Canvas modifier so taps on the icon explicitly trigger resume, matching the parent Box's behavior.

## 5. iOS Game Bar Alignment

### Problem

On iOS, `GameTopBarView` and `GameBottomBarView` sit immediately above/below the board in a plain `VStack`. On Android, the board has `.weight(1f)` which pushes bars to screen edges.

### Fix

In `GameContentView`, add `Spacer()` above and below the `BoardView` so it centers vertically while bars pin to edges:

```swift
VStack {
    GameTopBarView(...)
    Spacer()
    BoardView(...)
        .padding()
    Spacer()
    GameBottomBarView(...)
}
```

## 6. Dark Mode Considerations

The home screen and level selector warm backgrounds apply to light mode only. In dark mode:
- Home screen background: use existing `MaterialTheme.colorScheme.background` (Android) / `Color(.systemBackground)` (iOS)
- Cards: use `MaterialTheme.colorScheme.surface` / `Color(.secondarySystemBackground)`
- Board thumbnails: same gradient colors work on both themes
- Level cells: completed cells keep purple gradient in both modes. Unlocked/locked cells use surface color.

## No ViewModel Changes

All changes are UI-layer only. No modifications to ViewModels, use cases, repositories, or engine. The same `HomeUiState`, `ChallengeLevelSelectorUiState`, and `GameUiState` data flows unchanged.
