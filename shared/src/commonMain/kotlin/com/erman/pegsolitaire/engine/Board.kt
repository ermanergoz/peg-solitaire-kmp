package com.erman.pegsolitaire.engine

class Board(private val grid: Array<Array<CellState>>) {

    val rows: Int get() = grid.size
    val cols: Int get() = if (grid.isNotEmpty()) grid[0].size else 0

    fun copy(): Board = Board(grid.map { it.copyOf() }.toTypedArray())

    operator fun get(row: Int, col: Int): CellState = grid[row][col]

    internal operator fun set(row: Int, col: Int, value: CellState) {
        grid[row][col] = value
    }

    fun isInBounds(row: Int, col: Int): Boolean =
        row in 0 until rows && col in 0 until cols

    fun isPeg(row: Int, col: Int): Boolean {
        val value = grid[row][col]
        return value == CellState.PEG || value == CellState.SELECTED
    }

    fun isSelected(row: Int, col: Int): Boolean =
        grid[row][col] == CellState.SELECTED

    fun isEmpty(row: Int, col: Int): Boolean =
        grid[row][col] == CellState.EMPTY

    fun isDead(row: Int, col: Int): Boolean =
        grid[row][col] == CellState.DEAD

    fun countPegs(): Int = grid.sumOf { row ->
        row.count { it == CellState.PEG || it == CellState.SELECTED }
    }

    fun countTotalSlots(): Int = grid.sumOf { row ->
        row.count { it != CellState.DEAD }
    }

    fun findCells(predicate: (CellState) -> Boolean): List<Position> {
        val result = mutableListOf<Position>()
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (predicate(grid[row][col])) result.add(Position(row, col))
            }
        }
        return result
    }

    inline fun forEachPeg(action: (row: Int, col: Int) -> Unit) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (isPeg(row, col)) action(row, col)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Board) return false
        if (rows != other.rows || cols != other.cols) return false
        for (row in grid.indices) {
            if (!grid[row].contentEquals(other.grid[row])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = rows
        for (row in grid) {
            result = 31 * result + row.contentHashCode()
        }
        return result
    }
}
