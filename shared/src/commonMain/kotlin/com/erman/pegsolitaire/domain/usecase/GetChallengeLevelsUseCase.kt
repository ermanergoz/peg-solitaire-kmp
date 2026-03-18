package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.model.LevelItem
import com.erman.pegsolitaire.domain.repository.LevelRepository
import com.erman.pegsolitaire.engine.DifficultyCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetChallengeLevelsUseCase(
    private val levelRepository: LevelRepository,
    private val difficultyCalculator: DifficultyCalculator
) {

    suspend operator fun invoke(from: Int, count: Int): List<LevelItem> = coroutineScope {
        val highestCompleted = async { levelRepository.getHighestCompletedLevel() ?: 0 }
        val allProgress = async { levelRepository.getAllProgress() }

        val highestCompletedLevel = highestCompleted.await()
        val progressMap = allProgress.await().associateBy { it.levelNumber }

        (from until from + count).map { levelNumber ->
            val progress = progressMap[levelNumber]
            val boardType = difficultyCalculator.getBoardType(levelNumber)
            val totalPegs = difficultyCalculator.getTargetPegs(levelNumber)

            LevelItem(
                levelNumber = levelNumber,
                stars = progress?.stars ?: 0,
                totalPegs = totalPegs,
                boardType = boardType,
                isLocked = levelNumber > highestCompletedLevel + 1
            )
        }
    }
}
