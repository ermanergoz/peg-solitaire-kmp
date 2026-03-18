package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.repository.LevelRepository

class ClearChallengeProgressUseCase(private val levelRepository: LevelRepository) {

    suspend operator fun invoke() {
        levelRepository.clearAllProgress()
    }
}
