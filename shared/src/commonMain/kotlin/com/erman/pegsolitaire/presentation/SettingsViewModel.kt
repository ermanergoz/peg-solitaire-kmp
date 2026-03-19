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
