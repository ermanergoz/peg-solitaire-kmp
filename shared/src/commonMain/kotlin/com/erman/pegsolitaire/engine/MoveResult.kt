package com.erman.pegsolitaire.engine

sealed class MoveResult {
    data class Selected(val position: Position, val board: Board) : MoveResult()
    data class Moved(val move: Move, val board: Board) : MoveResult()
    data class Deselected(val board: Board) : MoveResult()
    data object Invalid : MoveResult()
}
