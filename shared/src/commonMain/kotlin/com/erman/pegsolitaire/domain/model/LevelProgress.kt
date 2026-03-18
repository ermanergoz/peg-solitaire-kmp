package com.erman.pegsolitaire.domain.model

data class LevelProgress(
    val levelNumber: Int,
    val stars: Int,
    val remainingPegs: Int,
    val elapsedTimeMillis: Long
)
