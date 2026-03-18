package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.repository.ScoreRepository
import com.erman.pegsolitaire.engine.BoardType

class SaveScoreUseCase(private val scoreRepository: ScoreRepository) {

    suspend operator fun invoke(
        boardType: BoardType,
        remainingPegs: Int,
        elapsedTimeMillis: Long
    ) {
        scoreRepository.saveScore(boardType, remainingPegs, elapsedTimeMillis)
    }
}
