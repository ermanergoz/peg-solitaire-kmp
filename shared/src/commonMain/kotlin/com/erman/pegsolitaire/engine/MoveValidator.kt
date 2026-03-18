package com.erman.pegsolitaire.engine

import kotlin.math.abs

class MoveValidator {

    fun findValidMoves(board: Board): List<Move> = buildList {
        board.forEachPeg { row, col ->
            collectMovesFrom(board, row, col, this)
        }
    }

    fun hasMoves(board: Board, row: Int, col: Int): Boolean {
        if (!board.isPeg(row, col)) return false
        return Direction.ALL.any { hasValidJumpInDirection(board, row, col, it) }
    }

    fun tryMove(board: Board, from: Position, to: Position): Move? {
        if (!board.isPeg(from.row, from.col)) return null

        val dRow = to.row - from.row
        val dCol = to.col - from.col

        if (!isJumpDistance(dRow, dCol)) return null

        val captured = Position(from.row + dRow / JUMP_DISTANCE, from.col + dCol / JUMP_DISTANCE)

        if (!isLandingValid(board, captured, to)) return null

        return Move(from, to, captured)
    }

    private fun collectMovesFrom(board: Board, row: Int, col: Int, moves: MutableList<Move>) {
        for (direction in Direction.ALL) {
            val jump = calculateJump(row, col, direction)
            if (isLandingValid(board, jump.captured, jump.destination)) {
                moves.add(Move(Position(row, col), jump.destination, jump.captured))
            }
        }
    }

    private fun hasValidJumpInDirection(board: Board, row: Int, col: Int, direction: Direction): Boolean {
        val jump = calculateJump(row, col, direction)
        return isLandingValid(board, jump.captured, jump.destination)
    }

    private fun calculateJump(row: Int, col: Int, direction: Direction): JumpTarget {
        val captured = Position(row + direction.rowDelta, col + direction.colDelta)
        val destination = Position(row + direction.rowDelta * JUMP_DISTANCE, col + direction.colDelta * JUMP_DISTANCE)
        return JumpTarget(captured, destination)
    }

    private fun isJumpDistance(dRow: Int, dCol: Int): Boolean =
        (dRow == 0 && abs(dCol) == JUMP_DISTANCE) ||
            (dCol == 0 && abs(dRow) == JUMP_DISTANCE)

    private fun isLandingValid(board: Board, captured: Position, dest: Position): Boolean =
        board.isInBounds(captured.row, captured.col) &&
            board.isInBounds(dest.row, dest.col) &&
            board.isPeg(captured.row, captured.col) &&
            board.isEmpty(dest.row, dest.col)

    private data class JumpTarget(val captured: Position, val destination: Position)
}
