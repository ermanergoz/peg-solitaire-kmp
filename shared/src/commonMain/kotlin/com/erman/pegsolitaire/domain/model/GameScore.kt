package com.erman.pegsolitaire.domain.model

import com.erman.pegsolitaire.engine.BoardType

data class GameScore(
    val boardType: BoardType,
    val remainingPegs: Int,
    val elapsedTimeMillis: Long
)
