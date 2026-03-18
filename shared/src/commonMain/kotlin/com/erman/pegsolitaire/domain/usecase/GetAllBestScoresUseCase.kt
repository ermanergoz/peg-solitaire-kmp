package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.domain.repository.ScoreRepository
import com.erman.pegsolitaire.engine.BoardType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GetAllBestScoresUseCase(private val scoreRepository: ScoreRepository) {

    suspend operator fun invoke(): Map<BoardType, GameScore> = coroutineScope {
        BoardType.entries
            .map { boardType -> async { boardType to scoreRepository.getBestScore(boardType) } }
            .awaitAll()
            .mapNotNull { (type, score) -> score?.let { type to it } }
            .toMap()
    }
}
