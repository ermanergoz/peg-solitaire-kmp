package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.model.LevelProgress
import com.erman.pegsolitaire.domain.model.calculateStars
import com.erman.pegsolitaire.domain.repository.LevelRepository

class SaveLevelProgressUseCase(private val levelRepository: LevelRepository) {

    suspend operator fun invoke(
        levelNumber: Int,
        remainingPegs: Int,
        elapsedTimeMillis: Long
    ): Int {
        val stars = calculateStars(remainingPegs)

        val existing = levelRepository.getLevelProgress(levelNumber)
        if (shouldUpdateProgress(existing, stars, remainingPegs)) {
            levelRepository.saveLevelProgress(
                LevelProgress(levelNumber, stars, remainingPegs, elapsedTimeMillis)
            )
        }

        return stars
    }

    private fun shouldUpdateProgress(existing: LevelProgress?, newStars: Int, newRemainingPegs: Int): Boolean =
        existing == null ||
            newStars > existing.stars ||
            (newStars == existing.stars && newRemainingPegs < existing.remainingPegs)
}
