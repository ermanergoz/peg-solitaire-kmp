package com.erman.pegsolitaire.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class BoardFactoryTest {

    @Test
    fun englishBoardHasCorrectDimensions() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        assertEquals(7, board.rows)
        assertEquals(7, board.cols)
    }

    @Test
    fun englishBoardHasCorrectPegCount() {
        val board = BoardFactory.create(BoardType.ENGLISH)
        assertEquals(32, board.countPegs())
    }

    @Test
    fun frenchBoardHasCorrectDimensions() {
        val board = BoardFactory.create(BoardType.FRENCH)
        assertEquals(7, board.rows)
        assertEquals(7, board.cols)
    }

    @Test
    fun frenchBoardHasCorrectPegCount() {
        val board = BoardFactory.create(BoardType.FRENCH)
        assertEquals(36, board.countPegs())
    }

    @Test
    fun germanBoardHasCorrectDimensions() {
        val board = BoardFactory.create(BoardType.GERMAN)
        assertEquals(9, board.rows)
        assertEquals(9, board.cols)
    }

    @Test
    fun germanBoardHasCorrectPegCount() {
        val board = BoardFactory.create(BoardType.GERMAN)
        assertEquals(44, board.countPegs())
    }

    @Test
    fun diamondBoardHasCorrectDimensions() {
        val board = BoardFactory.create(BoardType.DIAMOND)
        assertEquals(9, board.rows)
        assertEquals(9, board.cols)
    }

    @Test
    fun diamondBoardHasCorrectPegCount() {
        val board = BoardFactory.create(BoardType.DIAMOND)
        assertEquals(40, board.countPegs())
    }

    @Test
    fun asymmetricBoardHasCorrectDimensions() {
        val board = BoardFactory.create(BoardType.ASYMMETRIC)
        assertEquals(8, board.rows)
        assertEquals(8, board.cols)
    }

    @Test
    fun asymmetricBoardHasCorrectPegCount() {
        val board = BoardFactory.create(BoardType.ASYMMETRIC)
        assertEquals(38, board.countPegs())
    }

    @Test
    fun emptyTemplateHasNoPegs() {
        for (type in BoardType.entries) {
            val template = BoardFactory.createEmptyTemplate(type)
            assertEquals(0, template.countPegs())
        }
    }

    @Test
    fun emptyTemplatePreservesTotalSlots() {
        for (type in BoardType.entries) {
            val original = BoardFactory.create(type)
            val template = BoardFactory.createEmptyTemplate(type)
            assertEquals(original.countTotalSlots(), template.countTotalSlots())
        }
    }

    @Test
    fun boardCopyIsIndependent() {
        val original = BoardFactory.create(BoardType.ENGLISH)
        val copy = original.copy()
        copy[3, 3] = CellState.PEG
        assertEquals(CellState.EMPTY, original[3, 3])
        assertEquals(CellState.PEG, copy[3, 3])
    }

    @Test
    fun everyBoardHasExactlyOneEmptyCell() {
        for (type in BoardType.entries) {
            val board = BoardFactory.create(type)
            val emptyCells = board.findCells { it == CellState.EMPTY }
            assertEquals(1, emptyCells.size)
        }
    }

    @Test
    fun boardCenterIsEmptyForSymmetricBoards() {
        val english = BoardFactory.create(BoardType.ENGLISH)
        assertEquals(CellState.EMPTY, english[3, 3])

        val german = BoardFactory.create(BoardType.GERMAN)
        assertEquals(CellState.EMPTY, german[4, 4])

        val diamond = BoardFactory.create(BoardType.DIAMOND)
        assertEquals(CellState.EMPTY, diamond[4, 4])
    }

    @Test
    fun noBoardHasSelectedPegsOnCreation() {
        for (type in BoardType.entries) {
            val board = BoardFactory.create(type)
            val selected = board.findCells { it == CellState.SELECTED }
            assertEquals(0, selected.size)
        }
    }
}
