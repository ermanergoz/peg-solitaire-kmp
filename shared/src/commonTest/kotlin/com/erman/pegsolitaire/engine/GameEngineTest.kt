package com.erman.pegsolitaire.engine

import com.erman.pegsolitaire.engine.CellState.DEAD
import com.erman.pegsolitaire.engine.CellState.EMPTY
import com.erman.pegsolitaire.engine.CellState.PEG
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GameEngineTest {

    private val engine = GameEngine(MoveValidator())

    @Test
    fun selectingPegWithValidMovesReturnsSelected() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, null, Position(1, 3))
        assertIs<MoveResult.Selected>(result)
        assertEquals(Position(1, 3), result.position)
    }

    @Test
    fun selectingEmptyCellReturnsInvalid() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, null, Position(3, 3))
        assertIs<MoveResult.Invalid>(result)
    }

    @Test
    fun selectingDeadCellReturnsInvalid() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, null, Position(0, 0))
        assertIs<MoveResult.Invalid>(result)
    }

    @Test
    fun clickingSelectedPegAgainReturnsDeselected() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(1, 3), Position(1, 3))
        assertIs<MoveResult.Deselected>(result)
    }

    @Test
    fun validMoveReturnsMoved() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(1, 3), Position(3, 3))
        assertIs<MoveResult.Moved>(result)
        assertEquals(EMPTY, result.board[1, 3])
        assertEquals(EMPTY, result.board[2, 3])
        assertEquals(PEG, result.board[3, 3])
    }

    @Test
    fun invalidMoveToFarCellReturnsInvalid() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(1, 3), Position(4, 3))
        assertIs<MoveResult.Invalid>(result)
    }

    @Test
    fun isGameOverReturnsFalseOnFreshBoard() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        assertFalse(engine.isGameOver(board))
    }

    @Test
    fun isGameOverReturnsTrueWhenSinglePegRemains() {
        val board = Board(
            arrayOf(
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD),
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD),
                arrayOf(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY),
                arrayOf(EMPTY, EMPTY, EMPTY, PEG,   EMPTY, EMPTY, EMPTY),
                arrayOf(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY),
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD),
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD)
            )
        )
        assertTrue(engine.isGameOver(board))
    }

    @Test
    fun getValidMovesReturnsCorrectCountOnFreshEnglishBoard() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val moves = engine.getValidMoves(board)
        assertEquals(4, moves.size)
    }

    @Test
    fun canPegBeSelectedReturnsFalseForPegWithNoMoves() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        assertFalse(engine.canPegBeSelected(board, Position(0, 2)))
    }

    @Test
    fun canPegBeSelectedReturnsTrueForPegWithMoves() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        assertTrue(engine.canPegBeSelected(board, Position(1, 3)))
    }

    @Test
    fun clickingDeadCellWithSelectionReturnsDeselected() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(1, 3), Position(0, 0))
        assertIs<MoveResult.Deselected>(result)
    }

    @Test
    fun selectingAnotherMovablePegSwitchesSelection() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(1, 3), Position(3, 1))
        assertIs<MoveResult.Selected>(result)
        assertEquals(Position(3, 1), result.position)
    }

    @Test
    fun movePreservesFromAndToPositions() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(1, 3), Position(3, 3))
        assertIs<MoveResult.Moved>(result)
        assertEquals(Position(1, 3), result.move.from)
        assertEquals(Position(3, 3), result.move.to)
        assertEquals(Position(2, 3), result.move.captured)
    }

    @Test
    fun clickingDeadCellWhileSelectedReturnsDeselected() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(2, 2), Position(0, 0))
        assertIs<MoveResult.Deselected>(result)
    }

    @Test
    fun diagonalMoveReturnsInvalid() {
        val board = Board(
            arrayOf(
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD),
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD),
                arrayOf(EMPTY, EMPTY, PEG,   EMPTY, EMPTY, EMPTY, EMPTY),
                arrayOf(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY),
                arrayOf(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY),
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD),
                arrayOf(DEAD,  DEAD,  EMPTY, EMPTY, EMPTY, DEAD,  DEAD)
            )
        )
        val result = engine.onCellClicked(board, Position(2, 2), Position(4, 4))
        assertIs<MoveResult.Invalid>(result)
    }

    @Test
    fun selectingImmovablePegWithSelectionReturnsInvalid() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, Position(1, 3), Position(0, 2))
        assertIs<MoveResult.Invalid>(result)
    }

    @Test
    fun selectedBoardStateIsSetOnSelection() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val result = engine.onCellClicked(board, null, Position(1, 3))
        assertIs<MoveResult.Selected>(result)
        assertTrue(result.board.isSelected(1, 3))
    }

    @Test
    fun deselectedBoardStateClearsSelection() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        val selected = engine.onCellClicked(board, null, Position(1, 3))
        assertIs<MoveResult.Selected>(selected)

        val deselected = engine.onCellClicked(selected.board, Position(1, 3), Position(1, 3))
        assertIs<MoveResult.Deselected>(deselected)
        assertFalse(deselected.board.isSelected(1, 3))
        assertTrue(deselected.board.isPeg(1, 3))
    }
}
