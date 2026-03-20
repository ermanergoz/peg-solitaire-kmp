# Haptic Feedback Implementation

## Overview

Wire actual platform vibration to game events, gated by the existing haptic setting toggle.

## Haptic Types

| Enum Value  | Trigger           | Android VibrationEffect        | iOS Generator                          |
|-------------|-------------------|-------------------------------|----------------------------------------|
| `SELECTION` | Peg selected      | `createOneShot(30, 80)`       | `UIImpactFeedbackGenerator(.light)`    |
| `MOVE`      | Valid move         | `createOneShot(50, 150)`      | `UIImpactFeedbackGenerator(.medium)`   |
| `ERROR`     | Invalid move       | `createOneShot(100, 255)`     | `UINotificationFeedbackGenerator(.error)` |
| `SUCCESS`   | Game over          | `createOneShot(80, 200)`      | `UINotificationFeedbackGenerator(.success)` |

## Components

### Domain Layer

**`HapticType`** enum in `domain/model/`:
`SELECTION`, `MOVE`, `ERROR`, `SUCCESS`

**`PerformHapticUseCase`** in `domain/usecase/`:
- Constructor: `(settingsRepository: SettingsRepository, hapticService: HapticService)`
- On init: launches coroutine to collect `observeHapticEnabled()` into a cached `Boolean` field
- `operator fun invoke(type: HapticType)`: if cached setting is `true`, calls `hapticService.vibrate(type)`
- Requires a `CoroutineScope` parameter for the flow collection

### Data Layer (expect/actual)

**`expect class HapticService`** in `commonMain/data/service/`:
- `fun vibrate(type: HapticType)`

**Android actual** in `androidMain/data/service/`:
- Constructor takes `Context`
- Uses `Vibrator` system service
- API 26+: `VibrationEffect.createOneShot(millis, amplitude)`
- Below 26: `vibrator.vibrate(millis)` (deprecated but functional)

**iOS actual** in `iosMain/data/service/`:
- Uses `UIImpactFeedbackGenerator` for `SELECTION`/`MOVE`
- Uses `UINotificationFeedbackGenerator` for `ERROR`/`SUCCESS`

### Presentation Layer

**`GameViewModel`** changes:
- Add `PerformHapticUseCase` as constructor parameter
- Call `performHapticUseCase(HapticType.SELECTION)` on `CellClickEvent.Selected`
- Call `performHapticUseCase(HapticType.MOVE)` on `CellClickEvent.Moved`
- Call `performHapticUseCase(HapticType.ERROR)` on `CellClickEvent.Invalid`
- Call `performHapticUseCase(HapticType.SUCCESS)` in `saveGameResult()` after save completes

### DI

- `HapticService` registered as `single` in Android/iOS platform modules
- `PerformHapticUseCase` registered as `factory` in shared module

## No UI Changes

Haptic setting toggle already exists. No new screens or composables needed.
