package com.erman.pegsolitaire.data.repository

import com.erman.pegsolitaire.domain.repository.SettingsRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

private const val KEY_SOUND_ENABLED = "sound_enabled"
private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
private const val DEFAULT_SOUND_ENABLED = true
private const val DEFAULT_HAPTIC_ENABLED = true

@OptIn(ExperimentalSettingsApi::class)
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
