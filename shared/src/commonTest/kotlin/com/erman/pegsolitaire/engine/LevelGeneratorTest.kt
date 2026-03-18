package com.erman.pegsolitaire.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelGeneratorTest {

    private val difficulty = DifficultyCalculator()
    private val generator = LevelGenerator(difficulty)

    @Test
    fun generatedLevelHasCorrectPegCount() {
        val level = generator.generate(1)
        assertEquals(level.totalPegs, level.board.countPegs())
    }

    @Test
    fun generatedBoardHasValidMoves() {
        val engine = GameEngine(MoveValidator())
        val level = generator.generate(5)
        val moves = engine.getValidMoves(level.board)
        assertTrue(moves.isNotEmpty())
    }

    @Test
    fun generationIsDeterministic() {
        val level1 = generator.generate(5)
        val level2 = generator.generate(5)
        assertEquals(level1.totalPegs, level2.totalPegs)
        assertEquals(level1.boardType, level2.boardType)
        assertEquals(level1.board, level2.board)
    }

    @Test
    fun highLevelNumberGeneratesSuccessfully() {
        val level = generator.generate(100)
        assertTrue(level.board.countPegs() > 0)
    }

    @Test
    fun generatedLevelMatchesExpectedBoardType() {
        val level = generator.generate(30)
        assertEquals(BoardType.FRENCH, level.boardType)
    }

    @Test
    fun generatedLevelHasCorrectBoardTypePerLevel() {
        assertEquals(BoardType.ENGLISH, generator.generate(1).boardType)
        assertEquals(BoardType.FRENCH, generator.generate(30).boardType)
        assertEquals(BoardType.DIAMOND, generator.generate(40).boardType)
        assertEquals(BoardType.ASYMMETRIC, generator.generate(50).boardType)
    }
}
