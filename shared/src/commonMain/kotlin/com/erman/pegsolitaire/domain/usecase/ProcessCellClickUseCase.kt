package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.engine.GameEngine
import com.erman.pegsolitaire.engine.MoveResult
import com.erman.pegsolitaire.engine.Position

class ProcessCellClickUseCase(private val gameEngine: GameEngine) {

    operator fun invoke(board: Board, selectedCell: Position?, target: Position): CellClickResult {
        val result = gameEngine.onCellClicked(board, selectedCell, target)
        return mapResult(result, board, selectedCell)
    }

    private fun mapResult(result: MoveResult, originalBoard: Board, selectedCell: Position?): CellClickResult =
        when (result) {
            is MoveResult.Selected -> CellClickResult(
                board = result.board,
                selectedCell = result.position,
                remainingPegs = result.board.countPegs(),
                isGameOver = false,
                boardSnapshot = null,
                event = CellClickEvent.Selected
            )
            is MoveResult.Moved -> CellClickResult(
                board = result.board,
                selectedCell = null,
                remainingPegs = result.board.countPegs(),
                isGameOver = gameEngine.isGameOver(result.board),
                boardSnapshot = originalBoard.copyWithoutSelection(),
                event = CellClickEvent.Moved(result.move)
            )
            is MoveResult.Deselected -> CellClickResult(
                board = result.board,
                selectedCell = null,
                remainingPegs = result.board.countPegs(),
                isGameOver = false,
                boardSnapshot = null,
                event = CellClickEvent.Deselected
            )
            is MoveResult.Invalid -> CellClickResult(
                board = originalBoard,
                selectedCell = selectedCell,
                remainingPegs = originalBoard.countPegs(),
                isGameOver = false,
                boardSnapshot = null,
                event = CellClickEvent.Invalid
            )
        }
}
