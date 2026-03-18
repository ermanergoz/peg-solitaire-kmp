package com.erman.pegsolitaire.domain.model

import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.engine.BoardType
import com.erman.pegsolitaire.engine.Position

data class GameState(
    val board: Board,
    val boardType: BoardType,
    val gameMode: GameMode,
    val selectedCell: Position? = null,
    val remainingPegs: Int,
    val totalPegs: Int,
    val elapsedTimeMillis: Long = 0L,
    val isGameOver: Boolean = false,
    val canUndo: Boolean = false,
    val levelNumber: Int? = null
)
