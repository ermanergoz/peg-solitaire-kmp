package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.repository.ScoreRepository

class ClearAllScoresUseCase(private val scoreRepository: ScoreRepository) {

    suspend operator fun invoke() {
        scoreRepository.clearAllScores()
    }
}
