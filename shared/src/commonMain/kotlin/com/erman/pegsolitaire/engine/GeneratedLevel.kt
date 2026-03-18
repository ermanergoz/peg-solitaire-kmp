package com.erman.pegsolitaire.engine

data class GeneratedLevel(
    val levelNumber: Int,
    val boardType: BoardType,
    val board: Board,
    val totalPegs: Int
)
