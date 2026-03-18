package com.erman.pegsolitaire.engine

data class Move(
    val from: Position,
    val to: Position,
    val captured: Position
)
