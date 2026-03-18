package com.erman.pegsolitaire.engine

data class Direction(val rowDelta: Int, val colDelta: Int) {

    companion object {
        internal val ALL = arrayOf(
            Direction(-1, 0),
            Direction(1, 0),
            Direction(0, -1),
            Direction(0, 1)
        )
    }
}
