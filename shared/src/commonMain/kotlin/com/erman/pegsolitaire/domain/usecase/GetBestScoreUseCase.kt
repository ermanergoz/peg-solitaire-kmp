package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.domain.repository.ScoreRepository
import com.erman.pegsolitaire.engine.BoardType

class GetBestScoreUseCase(private val scoreRepository: ScoreRepository) {

    suspend operator fun invoke(boardType: BoardType): GameScore? {
        return scoreRepository.getBestScore(boardType)
    }
}
