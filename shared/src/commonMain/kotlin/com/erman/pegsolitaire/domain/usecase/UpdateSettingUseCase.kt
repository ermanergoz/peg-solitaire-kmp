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
