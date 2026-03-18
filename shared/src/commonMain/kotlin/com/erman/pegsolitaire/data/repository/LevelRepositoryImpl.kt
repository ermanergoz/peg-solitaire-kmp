package com.erman.pegsolitaire.data.repository

import com.erman.pegsolitaire.data.local.PegSolitaireDatabase
import com.erman.pegsolitaire.domain.model.LevelProgress
import com.erman.pegsolitaire.domain.repository.LevelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LevelRepositoryImpl(database: PegSolitaireDatabase) : LevelRepository {

    private val queries = database.pegSolitaireQueries

    override suspend fun getLevelProgress(levelNumber: Int): LevelProgress? = withContext(Dispatchers.IO) {
        queries.getLevelProgress(levelNumber.toLong()).executeAsOneOrNull()?.let {
            mapToLevelProgress(it.levelNumber, it.stars, it.remainingPegs, it.elapsedTimeMillis)
        }
    }

    override suspend fun getAllProgress(): List<LevelProgress> = withContext(Dispatchers.IO) {
        queries.getAllLevelProgress().executeAsList().map {
            mapToLevelProgress(it.levelNumber, it.stars, it.remainingPegs, it.elapsedTimeMillis)
        }
    }

    override suspend fun getHighestCompletedLevel(): Int? = withContext(Dispatchers.IO) {
        queries.getHighestCompletedLevel()
            .executeAsOneOrNull()
            ?.MAX
            ?.toInt()
    }

    override suspend fun saveLevelProgress(progress: LevelProgress) = withContext(Dispatchers.IO) {
        queries.upsertLevelProgress(
            levelNumber = progress.levelNumber.toLong(),
            stars = progress.stars.toLong(),
            remainingPegs = progress.remainingPegs.toLong(),
            elapsedTimeMillis = progress.elapsedTimeMillis
        )
    }

    override suspend fun clearAllProgress() = withContext(Dispatchers.IO) {
        queries.clearAllLevelProgress()
    }

    private fun mapToLevelProgress(
        levelNumber: Long,
        stars: Long,
        remainingPegs: Long,
        elapsedTimeMillis: Long
    ) = LevelProgress(
        levelNumber = levelNumber.toInt(),
        stars = stars.toInt(),
        remainingPegs = remainingPegs.toInt(),
        elapsedTimeMillis = elapsedTimeMillis
    )
}
