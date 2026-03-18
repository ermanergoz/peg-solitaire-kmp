package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.engine.Position

data class CellClickResult(
    val board: Board,
    val selectedCell: Position?,
    val remainingPegs: Int,
    val isGameOver: Boolean,
    val boardSnapshot: Board?,
    val event: CellClickEvent
)
