package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.repository.LevelRepository

class GetHighestCompletedLevelUseCase(private val levelRepository: LevelRepository) {

    suspend operator fun invoke(): Int? {
        return levelRepository.getHighestCompletedLevel()
    }
}
