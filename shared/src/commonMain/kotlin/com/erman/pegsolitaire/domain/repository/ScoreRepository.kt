package com.erman.pegsolitaire.domain.repository

import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.engine.BoardType

interface ScoreRepository {
    suspend fun getBestScore(boardType: BoardType): GameScore?
    suspend fun saveScore(boardType: BoardType, remainingPegs: Int, elapsedTimeMillis: Long)
}
