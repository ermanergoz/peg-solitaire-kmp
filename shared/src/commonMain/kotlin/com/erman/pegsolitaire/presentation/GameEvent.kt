package com.erman.pegsolitaire.presentation

import com.erman.pegsolitaire.engine.Position

sealed class GameEvent {
    data class GameOver(val scoreText: String, val stars: Int? = null) : GameEvent()
    data object InvalidMove : GameEvent()
    data class PegMoved(val from: Position, val to: Position) : GameEvent()
}
