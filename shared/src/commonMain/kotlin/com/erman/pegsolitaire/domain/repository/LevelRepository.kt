package com.erman.pegsolitaire.domain.repository

import com.erman.pegsolitaire.domain.model.LevelProgress

interface LevelRepository {
    suspend fun getLevelProgress(levelNumber: Int): LevelProgress?
    suspend fun getAllProgress(): List<LevelProgress>
    suspend fun getHighestCompletedLevel(): Int?
    suspend fun saveLevelProgress(progress: LevelProgress)
    suspend fun clearAllProgress()
}
