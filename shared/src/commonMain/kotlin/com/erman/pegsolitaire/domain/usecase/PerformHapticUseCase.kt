package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.data.service.HapticService
import com.erman.pegsolitaire.domain.model.HapticType
import com.erman.pegsolitaire.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PerformHapticUseCase(
    private val settingsRepository: SettingsRepository,
    private val hapticService: HapticService
) {
    private var hapticEnabled = true

    fun startObserving(scope: CoroutineScope) {
        scope.launch {
            settingsRepository.observeHapticEnabled().collect { enabled ->
                hapticEnabled = enabled
            }
        }
    }

    operator fun invoke(type: HapticType) {
        if (hapticEnabled) {
            hapticService.vibrate(type)
        }
    }
}
