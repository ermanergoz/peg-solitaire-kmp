# Settings Screen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a settings screen with sound/haptic toggles (persistence only) and reset-all-scores, accessible via gear icon on the menu.

**Architecture:** multiplatform-settings library for key-value preference storage, composed use cases for reset, MVI ViewModel, stateless UI on both platforms.

**Tech Stack:** multiplatform-settings + multiplatform-settings-coroutines, Koin, SQLDelight, Compose, SwiftUI

**Spec:** `docs/superpowers/specs/2026-03-19-settings-screen-design.md`

---

### Task 1: Add multiplatform-settings dependency

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `shared/build.gradle.kts`

- [ ] **Step 1: Add version and library entries to version catalog**

In `gradle/libs.versions.toml`, add to `[versions]`:
```toml
multiplatformSettings = "1.3.0"
```

Add to `[libraries]`:
```toml
multiplatform-settings = { module = "com.russhwolf:multiplatform-settings", version.ref = "multiplatformSettings" }
multiplatform-settings-coroutines = { module = "com.russhwolf:multiplatform-settings-coroutines", version.ref = "multiplatformSettings" }
```

- [ ] **Step 2: Add dependencies to shared module**

In `shared/build.gradle.kts`, inside `sourceSets` → `commonMain.dependencies`, add:
```kotlin
implementation(libs.multiplatform.settings)
implementation(libs.multiplatform.settings.coroutines)
```

- [ ] **Step 3: Sync and verify compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```
Add multiplatform-settings dependency
```

---

### Task 2: Add deleteAllScores SQL query and ScoreRepository.clearAllScores()

**Files:**
- Modify: `shared/src/commonMain/sqldelight/com/erman/pegsolitaire/data/local/PegSolitaire.sq`
- Modify: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/repository/ScoreRepository.kt`
- Modify: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/data/repository/ScoreRepositoryImpl.kt`

- [ ] **Step 1: Add SQL query**

Append to `PegSolitaire.sq` (after line 61, before the trailing blank line):
```sql
deleteAllScores:
DELETE FROM ScoreEntity;
```

- [ ] **Step 2: Add interface method**

In `ScoreRepository.kt`, add after line 8 (after `saveScore`):
```kotlin
    suspend fun clearAllScores()
```

- [ ] **Step 3: Implement in ScoreRepositoryImpl**

In `ScoreRepositoryImpl.kt`, add after the `saveScore` method (after line 37):
```kotlin
    override suspend fun clearAllScores() = withContext(Dispatchers.IO) {
        queries.deleteAllScores()
    }
```

- [ ] **Step 4: Verify compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```
Add clearAllScores to score repository
```

---

### Task 3: Create ClearAllScoresUseCase and ResetAllScoresUseCase

**Files:**
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/ClearAllScoresUseCase.kt`
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/ResetAllScoresUseCase.kt`

- [ ] **Step 1: Create ClearAllScoresUseCase**

Create `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/ClearAllScoresUseCase.kt`:
```kotlin
package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.repository.ScoreRepository

class ClearAllScoresUseCase(private val scoreRepository: ScoreRepository) {

    suspend operator fun invoke() {
        scoreRepository.clearAllScores()
    }
}
```

- [ ] **Step 2: Create ResetAllScoresUseCase**

Create `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/ResetAllScoresUseCase.kt`:
```kotlin
package com.erman.pegsolitaire.domain.usecase

class ResetAllScoresUseCase(
    private val clearAllScoresUseCase: ClearAllScoresUseCase,
    private val clearChallengeProgressUseCase: ClearChallengeProgressUseCase
) {

    suspend operator fun invoke() {
        try {
            clearAllScoresUseCase()
        } catch (e: Exception) {
            // Continue to clear challenge progress even if scores fail
        }
        clearChallengeProgressUseCase()
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```
Add use cases for clearing scores and resetting all progress
```

---

### Task 4: Create SettingsRepository interface and implementation

**Files:**
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/repository/SettingsRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/data/repository/SettingsRepositoryImpl.kt`

- [ ] **Step 1: Create SettingsRepository interface**

Create `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/repository/SettingsRepository.kt`:
```kotlin
package com.erman.pegsolitaire.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSoundEnabled(): Flow<Boolean>
    fun observeHapticEnabled(): Flow<Boolean>
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setHapticEnabled(enabled: Boolean)
}
```

- [ ] **Step 2: Create SettingsRepositoryImpl**

Create `shared/src/commonMain/kotlin/com/erman/pegsolitaire/data/repository/SettingsRepositoryImpl.kt`:
```kotlin
package com.erman.pegsolitaire.data.repository

import com.erman.pegsolitaire.domain.repository.SettingsRepository
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

private const val KEY_SOUND_ENABLED = "sound_enabled"
private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
private const val DEFAULT_SOUND_ENABLED = true
private const val DEFAULT_HAPTIC_ENABLED = true

class SettingsRepositoryImpl(private val flowSettings: FlowSettings) : SettingsRepository {

    override fun observeSoundEnabled(): Flow<Boolean> =
        flowSettings.getBooleanFlow(KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED)

    override fun observeHapticEnabled(): Flow<Boolean> =
        flowSettings.getBooleanFlow(KEY_HAPTIC_ENABLED, DEFAULT_HAPTIC_ENABLED)

    override suspend fun setSoundEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        flowSettings.putBoolean(KEY_SOUND_ENABLED, enabled)
    }

    override suspend fun setHapticEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        flowSettings.putBoolean(KEY_HAPTIC_ENABLED, enabled)
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```
Add settings repository with multiplatform-settings storage
```

---

### Task 5: Create GetSettingsUseCase and UpdateSettingUseCase

**Files:**
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/GetSettingsUseCase.kt`
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/UpdateSettingUseCase.kt`

- [ ] **Step 1: Create GetSettingsUseCase**

Create `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/GetSettingsUseCase.kt`:
```kotlin
package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSettingsUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): Flow<Pair<Boolean, Boolean>> =
        combine(
            settingsRepository.observeSoundEnabled(),
            settingsRepository.observeHapticEnabled()
        ) { sound, haptic -> sound to haptic }
}
```

- [ ] **Step 2: Create UpdateSettingUseCase**

Create `shared/src/commonMain/kotlin/com/erman/pegsolitaire/domain/usecase/UpdateSettingUseCase.kt`:
```kotlin
package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.repository.SettingsRepository

class UpdateSettingUseCase(private val settingsRepository: SettingsRepository) {

    suspend fun setSoundEnabled(enabled: Boolean) {
        settingsRepository.setSoundEnabled(enabled)
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        settingsRepository.setHapticEnabled(enabled)
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```
Add settings use cases for reading and updating preferences
```

---

### Task 6: Create SettingsViewModel

**Files:**
- Create: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/presentation/SettingsViewModel.kt`

- [ ] **Step 1: Create SettingsViewModel with inline UiState and Event**

Create `shared/src/commonMain/kotlin/com/erman/pegsolitaire/presentation/SettingsViewModel.kt`:
```kotlin
package com.erman.pegsolitaire.presentation

import com.erman.pegsolitaire.domain.usecase.GetSettingsUseCase
import com.erman.pegsolitaire.domain.usecase.ResetAllScoresUseCase
import com.erman.pegsolitaire.domain.usecase.UpdateSettingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val showResetConfirmation: Boolean = false,
    val error: String? = null
)

sealed class SettingsEvent {
    data object ScoresReset : SettingsEvent()
}

private const val EVENT_BUFFER_CAPACITY = 8

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingUseCase: UpdateSettingUseCase,
    private val resetAllScoresUseCase: ResetAllScoresUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>(extraBufferCapacity = EVENT_BUFFER_CAPACITY)
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    init {
        scope.launch {
            try {
                getSettingsUseCase().collect { (sound, haptic) ->
                    _uiState.update { it.copy(soundEnabled = sound, hapticEnabled = haptic) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = GENERIC_ERROR_MESSAGE) }
            }
        }
    }

    fun toggleSound() {
        scope.launch {
            try {
                updateSettingUseCase.setSoundEnabled(!_uiState.value.soundEnabled)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = GENERIC_ERROR_MESSAGE) }
            }
        }
    }

    fun toggleHaptic() {
        scope.launch {
            try {
                updateSettingUseCase.setHapticEnabled(!_uiState.value.hapticEnabled)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = GENERIC_ERROR_MESSAGE) }
            }
        }
    }

    fun requestResetScores() {
        _uiState.update { it.copy(showResetConfirmation = true) }
    }

    fun confirmResetScores() {
        _uiState.update { it.copy(showResetConfirmation = false) }
        scope.launch {
            try {
                resetAllScoresUseCase()
                _events.tryEmit(SettingsEvent.ScoresReset)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = GENERIC_ERROR_MESSAGE) }
            }
        }
    }

    fun dismissResetDialog() {
        _uiState.update { it.copy(showResetConfirmation = false) }
    }

    fun onCleared() {
        scope.cancel()
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```
Add SettingsViewModel with MVI pattern
```

---

### Task 7: Wire up Koin DI for all settings dependencies

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/erman/pegsolitaire/di/SharedModule.kt`
- Modify: `shared/src/androidMain/kotlin/com/erman/pegsolitaire/di/AndroidModule.kt`
- Modify: `shared/src/iosMain/kotlin/com/erman/pegsolitaire/di/IosModule.kt`
- Modify: `shared/src/iosMain/kotlin/com/erman/pegsolitaire/di/KoinHelper.kt`

- [ ] **Step 1: Add imports and registrations to SharedModule.kt**

Add imports at the top of `SharedModule.kt`:
```kotlin
import com.erman.pegsolitaire.data.repository.SettingsRepositoryImpl
import com.erman.pegsolitaire.domain.repository.SettingsRepository
import com.erman.pegsolitaire.domain.usecase.ClearAllScoresUseCase
import com.erman.pegsolitaire.domain.usecase.GetSettingsUseCase
import com.erman.pegsolitaire.domain.usecase.ResetAllScoresUseCase
import com.erman.pegsolitaire.domain.usecase.UpdateSettingUseCase
import com.erman.pegsolitaire.presentation.SettingsViewModel
import com.russhwolf.settings.coroutines.FlowSettings
```

Add registrations inside the `module { }` block, after the `LevelRepository` single (after line 35):
```kotlin
    single<SettingsRepository> { SettingsRepositoryImpl(get<FlowSettings>()) }
```

Add factory registrations after `ClearChallengeProgressUseCase` factory (after line 45):
```kotlin
    factory { ClearAllScoresUseCase(get()) }
    factory { GetSettingsUseCase(get()) }
    factory { UpdateSettingUseCase(get()) }
    factory { ResetAllScoresUseCase(get(), get()) }
```

Add ViewModel factory after `ChallengeLevelSelectorViewModel` factory (after line 48):
```kotlin
    factory { SettingsViewModel(get(), get(), get()) }
```

- [ ] **Step 2: Add Android Settings factory to AndroidModule.kt**

Replace the entire `AndroidModule.kt` content:
```kotlin
package com.erman.pegsolitaire.di

import android.content.Context
import com.erman.pegsolitaire.data.local.DatabaseDriverFactory
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.dsl.module

private const val SETTINGS_PREFERENCES_NAME = "settings"

val androidModule = module {
    single { DatabaseDriverFactory(get()) }
    single<FlowSettings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE)
        ).toFlowSettings()
    }
}
```

- [ ] **Step 3: Add iOS Settings factory to IosModule.kt**

Replace the entire `IosModule.kt` content:
```kotlin
package com.erman.pegsolitaire.di

import com.erman.pegsolitaire.data.local.DatabaseDriverFactory
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single<FlowSettings> { NSUserDefaultsSettings().toFlowSettings() }
}
```

- [ ] **Step 4: Add getSettingsViewModel to KoinHelper.kt**

In `KoinHelper.kt`, add import:
```kotlin
import com.erman.pegsolitaire.presentation.SettingsViewModel
```

Add method inside `KoinHelper` class (after line 19):
```kotlin
    fun getSettingsViewModel(): SettingsViewModel = get()
```

- [ ] **Step 5: Verify compilation on both platforms**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64 :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```
Wire up Koin DI for settings dependencies
```

---

### Task 8: Create Android SettingsScreen and navigation

**Files:**
- Create: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/SettingsScreen.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/App.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/MenuScreen.kt`

- [ ] **Step 1: Create SettingsScreen.kt**

Create `composeApp/src/androidMain/kotlin/com/erman/pegsolitaire/ui/screen/SettingsScreen.kt`:
```kotlin
package com.erman.pegsolitaire.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Text("\u2190", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(SECTION_SPACING.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp)
        ) {
            Column(modifier = Modifier.padding(CARD_CONTENT_PADDING.dp)) {
                SettingsToggleRow(
                    label = "Sound Effects",
                    checked = uiState.soundEnabled,
                    onToggle = onToggleSound
                )
                SettingsToggleRow(
                    label = "Haptic Feedback",
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
            Text("Reset All Scores")
        }
    }

    if (uiState.showResetConfirmation) {
        AlertDialog(
            onDismissRequest = onDismissReset,
            title = { Text("Reset All Scores") },
            text = { Text("This will permanently delete all your classic mode best scores and challenge mode progress.") },
            confirmButton = {
                TextButton(onClick = onConfirmReset) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissReset) {
                    Text("Cancel")
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
```

- [ ] **Step 2: Add Settings screen and route to App.kt**

In `App.kt`, add imports:
```kotlin
import androidx.compose.runtime.DisposableEffect
import com.erman.pegsolitaire.presentation.SettingsEvent
import com.erman.pegsolitaire.presentation.SettingsViewModel
import com.erman.pegsolitaire.ui.screen.SettingsScreen
```

Add to sealed class (after line 22, before the closing brace):
```kotlin
    data object Settings : Screen()
```

Add case to the `when` block (after `ChallengeGame` case, before the closing brace of `when`):
```kotlin
            is Screen.Settings -> SettingsRoute(
                onBack = { currentScreen = Screen.Menu }
            )
```

Add `SettingsRoute` composable at the bottom of the file (after `ChallengeGameRoute`):
```kotlin
@Composable
private fun SettingsRoute(onBack: () -> Unit) {
    val viewModel = remember { KoinPlatform.getKoin().get<SettingsViewModel>() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ScoresReset -> onBack()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.onCleared() }
    }

    SettingsScreen(
        uiState = uiState,
        onBackClick = onBack,
        onToggleSound = viewModel::toggleSound,
        onToggleHaptic = viewModel::toggleHaptic,
        onResetScoresClick = viewModel::requestResetScores,
        onConfirmReset = viewModel::confirmResetScores,
        onDismissReset = viewModel::dismissResetDialog
    )
}
```

Add missing imports for `collectAsState`, `LaunchedEffect`, `DisposableEffect` if not already present (check existing imports — `remember`, `getValue` are already imported).

- [ ] **Step 3: Add gear icon to MenuScreen and onSettingsClick callback**

In `MenuScreen.kt`, update the `MenuScreen` composable signature to add `onSettingsClick`:

Change the function signature (lines 38-42) from:
```kotlin
fun MenuScreen(
    homeViewModel: HomeViewModel,
    onClassicSelected: (BoardType) -> Unit,
    onChallengeSelected: () -> Unit
)
```
to:
```kotlin
fun MenuScreen(
    homeViewModel: HomeViewModel,
    onClassicSelected: (BoardType) -> Unit,
    onChallengeSelected: () -> Unit,
    onSettingsClick: () -> Unit
)
```

Add a gear icon row at the top of the `Column` content (after line 55, before the `Text("Peg Solitaire"...)` block):
```kotlin
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onSettingsClick) {
                Text("\u2699", style = MaterialTheme.typography.headlineMedium)
            }
        }
```

Add `IconButton` import at the top:
```kotlin
import androidx.compose.material3.IconButton
```

- [ ] **Step 4: Update MenuScreenRoute in App.kt to pass onSettingsClick**

Update `MenuScreenRoute` signature (line 52-54) to accept the new parameter:
```kotlin
private fun MenuScreenRoute(
    onClassicSelected: (BoardType) -> Unit,
    onChallengeSelected: () -> Unit,
    onSettingsClick: () -> Unit
)
```

Update the `MenuScreen` call (lines 57-61) to pass `onSettingsClick`:
```kotlin
    MenuScreen(
        homeViewModel = homeViewModel,
        onClassicSelected = onClassicSelected,
        onChallengeSelected = onChallengeSelected,
        onSettingsClick = onSettingsClick
    )
```

Update the `MenuScreenRoute` call site in `App` (lines 31-34) to pass the lambda:
```kotlin
            is Screen.Menu -> MenuScreenRoute(
                onClassicSelected = { currentScreen = Screen.ClassicGame(it) },
                onChallengeSelected = { currentScreen = Screen.ChallengeLevelSelector },
                onSettingsClick = { currentScreen = Screen.Settings }
            )
```

- [ ] **Step 5: Verify Android compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```
Add Android settings screen with navigation
```

---

### Task 9: Create iOS SettingsViewModelWrapper and SettingsView

**Files:**
- Create: `iosApp/iosApp/ViewModel/SettingsViewModelWrapper.swift`
- Create: `iosApp/iosApp/Screen/SettingsView.swift`
- Modify: `iosApp/iosApp/ContentView.swift`
- Modify: `iosApp/iosApp/Screen/MenuView.swift`

- [ ] **Step 1: Create SettingsViewModelWrapper.swift**

Create `iosApp/iosApp/ViewModel/SettingsViewModelWrapper.swift`:
```swift
import SwiftUI
import Shared

class SettingsViewModelWrapper: ObservableObject {
    private let viewModel: SettingsViewModel
    private let stateCollector: FlowCollector<SettingsUiState>
    private let eventCollector: FlowCollector<SettingsEvent>

    @Published var uiState = SettingsUiState(
        soundEnabled: true,
        hapticEnabled: true,
        showResetConfirmation: false,
        error: nil
    )

    var onScoresReset: (() -> Void)?

    init() {
        viewModel = KoinHelper().getSettingsViewModel()
        stateCollector = FlowCollector(flow: viewModel.uiState)
        eventCollector = FlowCollector(flow: viewModel.events)

        stateCollector.collect { [weak self] state in
            guard let self = self, let state = state else { return }
            self.uiState = state
        }

        eventCollector.collect { [weak self] event in
            guard let self = self, let event = event else { return }
            if event is SettingsEvent.ScoresReset {
                self.onScoresReset?()
            }
        }
    }

    func toggleSound() { viewModel.toggleSound() }
    func toggleHaptic() { viewModel.toggleHaptic() }
    func requestResetScores() { viewModel.requestResetScores() }
    func confirmResetScores() { viewModel.confirmResetScores() }
    func dismissResetDialog() { viewModel.dismissResetDialog() }

    deinit {
        stateCollector.cancel()
        eventCollector.cancel()
        viewModel.onCleared()
    }
}
```

- [ ] **Step 2: Create SettingsView.swift**

Create `iosApp/iosApp/Screen/SettingsView.swift`:
```swift
import SwiftUI
import Shared

private let sectionSpacing: CGFloat = 24
private let screenPadding: CGFloat = 24
private let cardCornerRadius: CGFloat = 16
private let cardShadowRadius: CGFloat = 4
private let toggleVerticalPadding: CGFloat = 8

struct SettingsView: View {
    let onBack: () -> Void

    @StateObject private var viewModel = SettingsViewModelWrapper()

    var body: some View {
        VStack(spacing: sectionSpacing) {
            HStack {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                        .font(.title2)
                }
                Text("Settings")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .foregroundColor(.pink)
                Spacer()
            }

            VStack(spacing: 0) {
                Toggle("Sound Effects", isOn: Binding(
                    get: { viewModel.uiState.soundEnabled },
                    set: { _ in viewModel.toggleSound() }
                ))
                .padding(.vertical, toggleVerticalPadding)

                Toggle("Haptic Feedback", isOn: Binding(
                    get: { viewModel.uiState.hapticEnabled },
                    set: { _ in viewModel.toggleHaptic() }
                ))
                .padding(.vertical, toggleVerticalPadding)
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(cardCornerRadius)
            .shadow(radius: cardShadowRadius)

            Button(action: { viewModel.requestResetScores() }) {
                Text("Reset All Scores")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.red)

            Spacer()
        }
        .padding(screenPadding)
        .alert("Reset All Scores", isPresented: Binding(
            get: { viewModel.uiState.showResetConfirmation },
            set: { if !$0 { viewModel.dismissResetDialog() } }
        )) {
            Button("Cancel", role: .cancel) { viewModel.dismissResetDialog() }
            Button("Reset", role: .destructive) { viewModel.confirmResetScores() }
        } message: {
            Text("This will permanently delete all your classic mode best scores and challenge mode progress.")
        }
        .onAppear {
            viewModel.onScoresReset = onBack
        }
    }
}
```

- [ ] **Step 3: Add settings navigation to ContentView.swift**

Add to `AppScreen` enum (after the `challengeGame` case, before the closing brace):
```swift
    case settings
```

Add to the `switch` block in `ContentView` (after the `challengeGame` case, before the closing brace of `switch`):
```swift
            case .settings:
                SettingsView(
                    onBack: { router.currentScreen = .menu }
                )
```

- [ ] **Step 4: Add gear icon to MenuView.swift**

Update `MenuView` signature to add `onSettingsClick`:
```swift
struct MenuView: View {
    let bestScoreFor: (BoardType) -> GameScore?
    let onClassicSelected: (BoardType) -> Void
    let onChallengeSelected: () -> Void
    let onSettingsClick: () -> Void
```

Add a gear icon row at the top of the `VStack` content (after line 10 `VStack(spacing: 24) {`, before the `Text("Peg Solitaire")` line):
```swift
            HStack {
                Spacer()
                Button(action: onSettingsClick) {
                    Image(systemName: "gearshape")
                        .font(.title2)
                }
            }
```

- [ ] **Step 5: Update MenuView call in ContentView.swift**

Update the `MenuView` call (lines 12-19) to pass `onSettingsClick`:
```swift
                MenuView(
                    bestScoreFor: { homeViewModel.bestScore(for: $0) },
                    onClassicSelected: { boardType in
                        router.currentScreen = .classicGame(boardType: boardType)
                    },
                    onChallengeSelected: {
                        router.currentScreen = .challengeLevelSelector
                    },
                    onSettingsClick: {
                        router.currentScreen = .settings
                    }
                )
```

- [ ] **Step 6: Add new Swift files to Xcode project**

The new `.swift` files must be included in the Xcode project. Since they're placed in existing directories (`iosApp/iosApp/Screen/` and `iosApp/iosApp/ViewModel/`), Xcode should pick them up automatically if the directory is referenced as a group. Verify by opening the project in Xcode and checking file membership.

- [ ] **Step 7: Verify iOS shared compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 8: Commit**

```
Add iOS settings screen with navigation
```

---

### Task 10: Run full test suite and final verification

**Files:** None (verification only)

- [ ] **Step 1: Run existing unit tests**

Run: `./gradlew :shared:testDebugUnitTest`
Expected: All tests pass

- [ ] **Step 2: Verify Android compilation**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Verify iOS compilation**

Run: `./gradlew :shared:compileKotlinIosSimulatorArm64`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit (if any fixes were needed)**

Only if changes were required to fix issues found during verification.
