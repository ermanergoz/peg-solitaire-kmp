# Shared KMP Localization

## Overview

Add multi-language support using Compose Multiplatform Resources in the shared module. String resources managed via `strings.xml` files with auto-generated `Res.string` accessors. 19 languages (including English), 22 strings. RTL support included automatically.

## Gradle Setup

Add compose multiplatform and compose compiler plugins to `shared/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.composeMultiplatform)  // add
    alias(libs.plugins.composeCompiler)       // add
}
```

Add compose resources dependency to `commonMain.dependencies`:

```kotlin
commonMain.dependencies {
    // existing dependencies...
    implementation(libs.compose.components.resources) // add
}
```

## String Resources

### Directory structure

```
shared/src/commonMain/composeResources/
├── values/
│   └── strings.xml          (English - default)
├── values-es/
│   └── strings.xml          (Spanish)
├── values-pt-rBR/
│   └── strings.xml          (Portuguese Brazil)
├── values-fr/
│   └── strings.xml          (French)
├── values-de/
│   └── strings.xml          (German)
├── values-ja/
│   └── strings.xml          (Japanese)
├── values-ko/
│   └── strings.xml          (Korean)
├── values-zh-rCN/
│   └── strings.xml          (Chinese Simplified)
├── values-tr/
│   └── strings.xml          (Turkish)
├── values-it/
│   └── strings.xml          (Italian)
├── values-ru/
│   └── strings.xml          (Russian)
├── values-ar/
│   └── strings.xml          (Arabic)
├── values-hi/
│   └── strings.xml          (Hindi)
├── values-in/
│   └── strings.xml          (Indonesian - locale code "in")
├── values-az/
│   └── strings.xml          (Azerbaijani)
├── values-kk/
│   └── strings.xml          (Kazakh)
├── values-uz/
│   └── strings.xml          (Uzbek)
├── values-tk/
│   └── strings.xml          (Turkmen)
└── values-ky/
    └── strings.xml          (Kyrgyz)
```

### Default strings.xml (English)

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_title">Peg Solitaire</string>
    <string name="challenge_mode">Challenge Mode</string>
    <string name="classic_mode">Classic Mode</string>
    <string name="play">Play</string>
    <string name="browse_all_levels">Browse All Levels</string>
    <string name="level_n">Level %1$d</string>
    <string name="settings">Settings</string>
    <string name="sound_effects">Sound Effects</string>
    <string name="haptic_feedback">Haptic Feedback</string>
    <string name="reset_all_scores">Reset All Scores</string>
    <string name="reset_all_scores_confirm">This will permanently delete all your classic mode best scores and challenge mode progress.</string>
    <string name="reset">Reset</string>
    <string name="cancel">Cancel</string>
    <string name="challenge_levels">Challenge Levels</string>
    <string name="game_over">Game Over</string>
    <string name="quit">Quit</string>
    <string name="restart">Restart</string>
    <string name="next">Next</string>
    <string name="retry">Retry</string>
    <string name="not_played">Not played</string>
    <string name="generic_error">Something went wrong. Please try again.</string>
    <string name="score_left">%1$d left</string>
</resources>
```

22 strings. Format parameters use `%1$d` (Android-style positional format).

## Accessing Strings

### Android (Compose)

The compose resources plugin generates `Res.string` accessors. Use `stringResource()`:

```kotlin
// Before
Text("Peg Solitaire")
// After
Text(stringResource(Res.string.app_title))

// With format args
Text(stringResource(Res.string.level_n, currentLevel))
Text(stringResource(Res.string.score_left, remainingPegs))
```

Import: `org.jetbrains.compose.resources.stringResource` and the generated `Res` class from the shared module.

### iOS (SwiftUI)

The shared module exposes `Res.string` as Kotlin objects. Access from Swift via a helper that reads the string resource. Since SwiftUI views are not `@Composable`, use the `getString()` function from compose resources:

Create a Swift helper in `iosApp/iosApp/Localization/StringHelper.swift`:

```swift
import Shared

func str(_ resource: StringResource) -> String {
    return StringHelper.shared.getString(resource: resource)
}

func str(_ resource: StringResource, _ arg: Int32) -> String {
    return StringHelper.shared.getStringFormatted(resource: resource, arg: arg)
}
```

In the shared module, create `shared/src/commonMain/.../localization/StringHelper.kt`:

```kotlin
object StringHelper {
    fun getString(resource: StringResource): String {
        return runBlocking { org.jetbrains.compose.resources.getString(resource) }
    }

    fun getStringFormatted(resource: StringResource, arg: Int): String {
        return runBlocking { org.jetbrains.compose.resources.getString(resource, arg) }
    }
}
```

Note: `getString()` from compose resources is a suspend function. For iOS (non-Compose context), `runBlocking` is used. This is acceptable since string resolution from bundled resources is instant (no I/O).

Then in SwiftUI:
```swift
Text(str(Res.shared.string.app_title))
Text(str(Res.shared.string.level_n, currentLevel))
```

### Shared Presentation Layer

Replace the hardcoded `GENERIC_ERROR_MESSAGE` in `shared/.../presentation/Constants.kt` with a reference to the string resource. Since the presentation layer now needs compose resources, the `Strings` helper bridges this:

```kotlin
// Before
internal const val GENERIC_ERROR_MESSAGE = "Something went wrong. Please try again."
// After
internal fun genericErrorMessage(): String = StringHelper.getString(Res.string.generic_error)
```

## UI Layer Changes

Replace all hardcoded strings across:

**Android files:**
- `MenuScreen.kt` — app title, challenge/classic mode labels, play, browse levels, level N, not played, score left
- `GameScreen.kt` — quit, retry
- `SettingsScreen.kt` — settings, sound effects, haptic feedback, reset all scores, confirm message, reset, cancel
- `ChallengeLevelSelectorScreen.kt` — challenge levels, retry
- `GameOverDialog.kt` — game over, quit, restart, next

**iOS files:**
- `MenuView.swift` — same strings as Android MenuScreen
- `GameView.swift` — game over, quit, restart, next
- `SettingsView.swift` — settings, sound effects, haptic feedback, reset all scores, confirm, reset, cancel
- `ChallengeLevelSelectorView.swift` — challenge levels, retry

## RTL Support

Compose Multiplatform handles RTL layout automatically for Arabic. Layout direction mirrors based on the device locale. The `→` arrow in "Browse All Levels" should be handled in the UI layer (not in the string) so it flips direction in RTL:

- Remove `→` from the string resource
- Add a trailing icon/text in the composable/view that respects layout direction

## Testing

A unit test verifies all locale directories have a `strings.xml` with all string keys. The compose resources plugin validates XML at build time.

## What Does NOT Change

- Board type names (English, French, German, Asymmetric, Diamond) — proper nouns, not translated
- Score separator (` · `), time format (`MM:SS`) — universal
- No ViewModel signature changes
- Engine and domain layers untouched
