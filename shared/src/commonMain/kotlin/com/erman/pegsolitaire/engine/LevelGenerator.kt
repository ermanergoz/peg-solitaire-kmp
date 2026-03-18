package com.erman.pegsolitaire.engine

import kotlin.random.Random

private const val SEED_MULTIPLIER = 31337L
private const val MAX_RETRY_COUNT = 100
private const val FALLBACK_TARGET_PEGS = 3

class LevelGenerator(private val difficultyCalculator: DifficultyCalculator) {

    fun generate(levelNumber: Int): GeneratedLevel {
        require(levelNumber > 0) { "Level number must be positive" }

        val boardType = difficultyCalculator.getBoardType(levelNumber)
        val template = BoardFactory.createEmptyTemplate(boardType)
        val targetPegs = difficultyCalculator.calculateTargetPegs(levelNumber, template.countTotalSlots())

        return generateWithRetries(levelNumber, boardType, template, targetPegs)
            ?: createFallbackLevel(levelNumber, boardType)
    }

    private fun generateWithRetries(
        levelNumber: Int,
        boardType: BoardType,
        template: Board,
        targetPegs: Int
    ): GeneratedLevel? {
        for (retryCount in 0 until MAX_RETRY_COUNT) {
            val random = Random(levelNumber * SEED_MULTIPLIER + retryCount)
            val board = populateBoard(template, targetPegs, random)
            if (board != null) {
                return GeneratedLevel(levelNumber, boardType, board, targetPegs)
            }
        }
        return null
    }

    private fun createFallbackLevel(levelNumber: Int, boardType: BoardType): GeneratedLevel {
        val template = BoardFactory.createEmptyTemplate(boardType)
        val board = populateBoard(template, FALLBACK_TARGET_PEGS, Random(levelNumber * SEED_MULTIPLIER))
            ?: template.also { placeSinglePeg(it, Random(levelNumber * SEED_MULTIPLIER)) }
        return GeneratedLevel(levelNumber, boardType, board, board.countPegs())
    }

    private fun placeSinglePeg(board: Board, random: Random) {
        val emptyCells = board.findCells { it == CellState.EMPTY }
        val cell = emptyCells[random.nextInt(emptyCells.size)]
        board[cell.row, cell.col] = CellState.PEG
    }

    private fun populateBoard(template: Board, targetPegs: Int, random: Random): Board? {
        val board = template.copy()
        placeStartingPeg(board, random)
        return expandBoard(board, targetPegs, random)
    }

    private fun placeStartingPeg(board: Board, random: Random) {
        val emptyCells = board.findCells { it == CellState.EMPTY }
        val startCell = emptyCells[random.nextInt(emptyCells.size)]
        board[startCell.row, startCell.col] = CellState.PEG
    }

    private fun expandBoard(board: Board, targetPegs: Int, random: Random): Board? {
        var currentPegs = 1
        while (currentPegs < targetPegs) {
            val expansionMoves = findExpansionMoves(board)
            if (expansionMoves.isEmpty()) return null

            applyExpansionMove(board, expansionMoves[random.nextInt(expansionMoves.size)])
            currentPegs++
        }
        return board
    }

    private fun applyExpansionMove(board: Board, move: ExpansionMove) {
        board[move.existingPeg.row, move.existingPeg.col] = CellState.EMPTY
        board[move.newMidPeg.row, move.newMidPeg.col] = CellState.PEG
        board[move.newEndPeg.row, move.newEndPeg.col] = CellState.PEG
    }

    private fun findExpansionMoves(board: Board): List<ExpansionMove> = buildList {
        board.forEachPeg { row, col ->
            collectExpansionMoves(board, row, col, this)
        }
    }

    private fun collectExpansionMoves(board: Board, row: Int, col: Int, moves: MutableList<ExpansionMove>) {
        for (direction in Direction.ALL) {
            val midPosition = Position(row + direction.rowDelta, col + direction.colDelta)
            val endPosition = Position(row + direction.rowDelta * JUMP_DISTANCE, col + direction.colDelta * JUMP_DISTANCE)

            if (isValidExpansionTarget(board, midPosition, endPosition)) {
                moves.add(ExpansionMove(Position(row, col), midPosition, endPosition))
            }
        }
    }

    private fun isValidExpansionTarget(board: Board, mid: Position, end: Position): Boolean =
        board.isInBounds(mid.row, mid.col) &&
            board.isInBounds(end.row, end.col) &&
            board.isEmpty(mid.row, mid.col) &&
            board.isEmpty(end.row, end.col)

    private data class ExpansionMove(
        val existingPeg: Position,
        val newMidPeg: Position,
        val newEndPeg: Position
    )
}
