package com.erman.pegsolitaire.domain.model

import com.erman.pegsolitaire.engine.BoardType

data class LevelItem(
    val levelNumber: Int,
    val stars: Int,
    val totalPegs: Int,
    val boardType: BoardType,
    val isLocked: Boolean
)
