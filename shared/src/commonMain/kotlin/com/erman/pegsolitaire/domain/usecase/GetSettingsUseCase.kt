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
