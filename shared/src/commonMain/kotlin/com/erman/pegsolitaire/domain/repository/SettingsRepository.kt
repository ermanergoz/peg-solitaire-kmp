package com.erman.pegsolitaire.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSoundEnabled(): Flow<Boolean>
    fun observeHapticEnabled(): Flow<Boolean>
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setHapticEnabled(enabled: Boolean)
}
