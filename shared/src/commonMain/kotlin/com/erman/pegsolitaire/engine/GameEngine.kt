package com.erman.pegsolitaire.engine

class GameEngine(private val moveValidator: MoveValidator) {

    fun onCellClicked(board: Board, selectedCell: Position?, target: Position): MoveResult {
        if (!isCellAccessible(board, target)) {
            return if (selectedCell != null) deselect(board, selectedCell) else MoveResult.Invalid
        }

        if (selectedCell == null) {
            return selectIfMovable(board, target)
        }

        return handleSecondClick(board, selectedCell, target)
    }

    fun isGameOver(board: Board): Boolean =
        moveValidator.findValidMoves(board).isEmpty()

    fun getValidMoves(board: Board): List<Move> =
        moveValidator.findValidMoves(board)

    fun canPegBeSelected(board: Board, position: Position): Boolean =
        moveValidator.hasMoves(board, position.row, position.col)

    private fun isCellAccessible(board: Board, position: Position): Boolean =
        board.isInBounds(position.row, position.col) && !board.isDead(position.row, position.col)

    private fun selectIfMovable(board: Board, target: Position): MoveResult {
        if (!moveValidator.hasMoves(board, target.row, target.col)) return MoveResult.Invalid
        val newBoard = board.copy()
        newBoard[target.row, target.col] = CellState.SELECTED
        return MoveResult.Selected(target, newBoard)
    }

    private fun handleSecondClick(board: Board, selectedCell: Position, target: Position): MoveResult {
        if (target == selectedCell) {
            return deselect(board, selectedCell)
        }

        if (board.isPeg(target.row, target.col)) {
            return switchSelection(board, selectedCell, target)
        }

        return attemptMove(board, selectedCell, target)
    }

    private fun switchSelection(board: Board, previousSelection: Position, target: Position): MoveResult {
        if (!moveValidator.hasMoves(board, target.row, target.col)) return MoveResult.Invalid
        val newBoard = board.copy()
        newBoard[previousSelection.row, previousSelection.col] = CellState.PEG
        newBoard[target.row, target.col] = CellState.SELECTED
        return MoveResult.Selected(target, newBoard)
    }

    private fun deselect(board: Board, selectedCell: Position): MoveResult {
        val newBoard = board.copy()
        newBoard[selectedCell.row, selectedCell.col] = CellState.PEG
        return MoveResult.Deselected(newBoard)
    }

    private fun applyMove(board: Board, move: Move): Board {
        val newBoard = board.copy()
        newBoard[move.from.row, move.from.col] = CellState.EMPTY
        newBoard[move.captured.row, move.captured.col] = CellState.EMPTY
        newBoard[move.to.row, move.to.col] = CellState.PEG
        return newBoard
    }

    private fun attemptMove(board: Board, from: Position, to: Position): MoveResult {
        val move = moveValidator.tryMove(board, from, to) ?: return MoveResult.Invalid
        val newBoard = applyMove(board, move)
        return MoveResult.Moved(move, newBoard)
    }
}
