# Settings Screen Design

## Overview

Add a settings screen accessible via a gear icon on the menu screen. The screen provides toggles for sound effects and haptic feedback (persistence only, no playback implementation), and a "Reset All Scores" button with confirmation dialog.

## Scope

- Settings UI + persistence for sound/haptic toggles (actual playback deferred)
- Reset all scores (classic best scores + challenge level progress) with confirmation
- Android Compose + iOS SwiftUI
- Follows existing clean architecture layers

## Storage Layer

### Library

Use `multiplatform-settings` (russhwolf) with `multiplatform-settings-coroutines` for Flow support. The impl uses `FlowSettings` (the coroutines extension) for reactive observation via `getBoolean()` and `getBooleanFlow()`. Wraps `SharedPreferences` on Android and `NSUserDefaults` on iOS.

### Domain

`SettingsRepository` interface:

```kotlin
interface SettingsRepository {
    fun observeSoundEnabled(): Flow<Boolean>
    fun observeHapticEnabled(): Flow<Boolean>
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setHapticEnabled(enabled: Boolean)
}
```

No separate `UserSettings` domain model — the ViewModel combines the two flows directly into `SettingsUiState`. A domain model would be premature since no other feature consumes these settings yet.

### Data

`SettingsRepositoryImpl` uses `FlowSettings` (from `multiplatform-settings-coroutines`) for both reads and reactive observation. All write operations dispatch to `Dispatchers.IO` via `withContext`.

Platform-specific `Settings` factory:
- Android: `SharedPreferencesSettings(context.getSharedPreferences(...))`
- iOS: `NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)`

### Constants

Setting keys and defaults as `private const val` in the repository impl file (single-file scope):
- `KEY_SOUND_ENABLED = "sound_enabled"`, default `true`
- `KEY_HAPTIC_ENABLED = "haptic_enabled"`, default `true`

## Reset Scores

### SQLDelight

Add to `PegSolitaire.sq`:

```sql
deleteAllScores:
DELETE FROM ScoreEntity;
```

Note: `clearAllLevelProgress` query already exists (`DELETE FROM LevelProgressEntity`).

### Repository Changes

Add to `ScoreRepository` interface:

```kotlin
suspend fun clearAllScores()
```

`ScoreRepositoryImpl` implements with `withContext(Dispatchers.IO)`.

### Use Case

`ResetAllScoresUseCase` composes existing use cases rather than accessing repositories directly:

```kotlin
class ResetAllScoresUseCase(
    private val clearAllScoresUseCase: ClearAllScoresUseCase,
    private val clearChallengeProgressUseCase: ClearChallengeProgressUseCase
)
```

- `ClearAllScoresUseCase` is a new use case wrapping `scoreRepository.clearAllScores()`
- `ClearChallengeProgressUseCase` already exists
- Both called in a single `invoke()`. Partial failure is acceptable — these are independent data stores and the user can retry. Each call is wrapped in its own try-catch within the composing use case.

## Use Cases

### GetSettingsUseCase

```kotlin
class GetSettingsUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<Pair<Boolean, Boolean>> =
        combine(
            settingsRepository.observeSoundEnabled(),
            settingsRepository.observeHapticEnabled()
        ) { sound, haptic -> sound to haptic }
}
```

### UpdateSettingUseCase

```kotlin
class UpdateSettingUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun setSoundEnabled(enabled: Boolean) =
        settingsRepository.setSoundEnabled(enabled)
    suspend fun setHapticEnabled(enabled: Boolean) =
        settingsRepository.setHapticEnabled(enabled)
}
```

## Presentation

All settings presentation files go directly in the `presentation` package (flat, consistent with existing `HomeViewModel`, `GameViewModel`, etc.). `SettingsUiState` and `SettingsEvent` are defined inline in `SettingsViewModel.kt`, following the `HomeViewModel` pattern.

### SettingsUiState

```kotlin
data class SettingsUiState(
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val showResetConfirmation: Boolean = false,
    val error: String? = null
)
```

### SettingsEvent

```kotlin
sealed class SettingsEvent {
    data object ScoresReset : SettingsEvent()
}
```

### SettingsViewModel

- `StateFlow<SettingsUiState>` as single source of truth
- `SharedFlow<SettingsEvent>(extraBufferCapacity = 8)` for one-off events
- Collects `GetSettingsUseCase` flow on init to populate state
- Methods:
  - `toggleSound()` — calls `UpdateSettingUseCase.setSoundEnabled(!current)`
  - `toggleHaptic()` — calls `UpdateSettingUseCase.setHapticEnabled(!current)`
  - `requestResetScores()` — sets `showResetConfirmation = true`
  - `confirmResetScores()` — calls `ResetAllScoresUseCase`, emits `ScoresReset`, dismisses dialog
  - `dismissResetDialog()` — sets `showResetConfirmation = false`
  - `onCleared()` — cancels scope
- All coroutine operations in try-catch, errors surfaced via `uiState.error`

### Menu Data Refresh

When the user resets scores and navigates back to the menu, `HomeViewModel` must show updated data. Since `HomeViewModel.loadData()` is called externally by the UI, the menu screen already calls `loadData()` when it appears (Android: in the Route composable, iOS: in `onAppear`). This ensures stale data is refreshed on navigation back — no additional invalidation mechanism needed.

## UI — Android (Compose)

### SettingsScreen

Stateless composable receiving `SettingsUiState` and callbacks:
- Top bar with back navigation
- Toggle row for "Sound Effects" with Switch
- Toggle row for "Haptic Feedback" with Switch
- "Reset All Scores" button (destructive styling)
- Confirmation dialog when `showResetConfirmation` is true

### Navigation

- Add `data object Settings : Screen()` to sealed class in `App.kt`
- Add `SettingsRoute` composable (inside `SettingsScreen.kt`) that wires ViewModel to `SettingsScreen`
- Add gear icon button in `MenuScreen` top area, navigates to `Screen.Settings`

## UI — iOS (SwiftUI)

### SettingsView

SwiftUI view with:
- Toggle for "Sound Effects"
- Toggle for "Haptic Feedback"
- "Reset All Scores" button
- `.alert` modifier for confirmation dialog

### SettingsViewModelWrapper

`ObservableObject` wrapping Kotlin `SettingsViewModel`:
- `@Published var uiState: SettingsUiState`
- `FlowCollector` for state observation
- `FlowCollector` for events
- Methods forwarding to Kotlin ViewModel
- `deinit` cancels collectors and calls `onCleared()`

### Navigation

- Add `case settings` to `AppScreen` enum in `ContentView.swift`
- Add settings case to navigation switch
- Add gear icon button in `MenuView`

## Dependency Injection

### SharedModule.kt

```kotlin
// Settings
single<SettingsRepository> { SettingsRepositoryImpl(get()) }
factory { GetSettingsUseCase(get()) }
factory { UpdateSettingUseCase(get()) }
factory { ClearAllScoresUseCase(get()) }
factory { ResetAllScoresUseCase(get(), get()) }
factory { SettingsViewModel(get(), get(), get()) }
```

### Platform Modules

- Android: `single { SharedPreferencesSettings(get<Context>().getSharedPreferences("settings", Context.MODE_PRIVATE)) }` providing `Settings` instance
- iOS: `single { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }` providing `Settings` instance

### KoinHelper

Add `fun getSettingsViewModel(): SettingsViewModel = get()` to `KoinHelper` class.

## File List

### New Files

| File | Layer |
|------|-------|
| `shared/.../domain/repository/SettingsRepository.kt` | Domain |
| `shared/.../domain/usecase/GetSettingsUseCase.kt` | Domain |
| `shared/.../domain/usecase/UpdateSettingUseCase.kt` | Domain |
| `shared/.../domain/usecase/ClearAllScoresUseCase.kt` | Domain |
| `shared/.../domain/usecase/ResetAllScoresUseCase.kt` | Domain |
| `shared/.../data/repository/SettingsRepositoryImpl.kt` | Data |
| `shared/.../presentation/SettingsViewModel.kt` | Presentation |
| `composeApp/.../ui/settings/SettingsScreen.kt` | UI (Android) |
| `iosApp/.../Settings/SettingsView.swift` | UI (iOS) |
| `iosApp/.../Settings/SettingsViewModelWrapper.swift` | UI (iOS) |

### Modified Files

| File | Change |
|------|--------|
| `gradle/libs.versions.toml` | Add multiplatform-settings dependency |
| `shared/build.gradle.kts` | Add multiplatform-settings dependencies |
| `shared/.../data/local/PegSolitaire.sq` | Add `deleteAllScores` query |
| `shared/.../domain/repository/ScoreRepository.kt` | Add `clearAllScores()` |
| `shared/.../data/repository/ScoreRepositoryImpl.kt` | Implement `clearAllScores()` |
| `shared/.../di/SharedModule.kt` | Register settings dependencies |
| `shared/.../di/AndroidModule.kt` | Register Android Settings factory |
| `shared/.../di/IosModule.kt` | Register iOS Settings factory |
| `shared/iosMain/.../di/KoinHelper.kt` | Add `getSettingsViewModel()` |
| `composeApp/.../App.kt` | Add Settings screen + navigation |
| `composeApp/.../ui/menu/MenuScreen.kt` | Add gear icon |
| `iosApp/.../ContentView.swift` | Add settings navigation case |
| `iosApp/.../Menu/MenuView.swift` | Add gear icon |
