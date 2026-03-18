package com.erman.pegsolitaire.engine

import com.erman.pegsolitaire.engine.CellState.DEAD
import com.erman.pegsolitaire.engine.CellState.EMPTY
import com.erman.pegsolitaire.engine.CellState.PEG

object BoardFactory {

    fun create(type: BoardType): Board = when (type) {
        BoardType.ENGLISH -> createEnglishBoard()
        BoardType.FRENCH -> createFrenchBoard()
        BoardType.GERMAN -> createGermanBoard()
        BoardType.ASYMMETRIC -> createAsymmetricBoard()
        BoardType.DIAMOND -> createDiamondBoard()
    }

    fun createEmptyTemplate(type: BoardType): Board {
        val board = create(type)
        for (row in 0 until board.rows) {
            for (col in 0 until board.cols) {
                if (board[row, col] != DEAD) {
                    board[row, col] = EMPTY
                }
            }
        }
        return board
    }

    private fun createEnglishBoard(): Board = boardOf(
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(PEG,   PEG,   PEG,   EMPTY, PEG,   PEG,   PEG),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD)
    )

    private fun createFrenchBoard(): Board = boardOf(
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  PEG,   PEG,   PEG,   PEG,   PEG,   DEAD),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(PEG,   PEG,   EMPTY, PEG,   PEG,   PEG,   PEG),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(DEAD,  PEG,   PEG,   PEG,   PEG,   PEG,   DEAD),
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD)
    )

    private fun createGermanBoard(): Board = boardOf(
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(PEG,   PEG,   PEG,   PEG,   EMPTY, PEG,   PEG,   PEG,   PEG),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD)
    )

    private fun createAsymmetricBoard(): Board = boardOf(
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(PEG,   PEG,   PEG,   PEG,   EMPTY, PEG,   PEG,   PEG),
        row(PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD)
    )

    private fun createDiamondBoard(): Board = boardOf(
        row(DEAD,  DEAD,  DEAD,  DEAD,  PEG,   DEAD,  DEAD,  DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD),
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   DEAD),
        row(PEG,   PEG,   PEG,   PEG,   EMPTY, PEG,   PEG,   PEG,   PEG),
        row(DEAD,  PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   PEG,   DEAD),
        row(DEAD,  DEAD,  PEG,   PEG,   PEG,   PEG,   PEG,   DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  PEG,   PEG,   PEG,   DEAD,  DEAD,  DEAD),
        row(DEAD,  DEAD,  DEAD,  DEAD,  PEG,   DEAD,  DEAD,  DEAD,  DEAD)
    )

    private fun row(vararg cells: CellState): Array<CellState> = arrayOf(*cells)

    private fun boardOf(vararg rows: Array<CellState>): Board = Board(arrayOf(*rows))
}
