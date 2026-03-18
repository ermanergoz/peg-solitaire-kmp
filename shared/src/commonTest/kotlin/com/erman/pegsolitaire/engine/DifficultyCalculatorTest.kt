package com.erman.pegsolitaire.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DifficultyCalculatorTest {

    private val calculator = DifficultyCalculator()

    @Test
    fun level1IsEnglish() {
        assertEquals(BoardType.ENGLISH, calculator.getBoardType(1))
    }

    @Test
    fun level25IsEnglish() {
        assertEquals(BoardType.ENGLISH, calculator.getBoardType(25))
    }

    @Test
    fun level26IsFrench() {
        assertEquals(BoardType.FRENCH, calculator.getBoardType(26))
    }

    @Test
    fun level35IsFrench() {
        assertEquals(BoardType.FRENCH, calculator.getBoardType(35))
    }

    @Test
    fun level36IsDiamond() {
        assertEquals(BoardType.DIAMOND, calculator.getBoardType(36))
    }

    @Test
    fun level45IsDiamond() {
        assertEquals(BoardType.DIAMOND, calculator.getBoardType(45))
    }

    @Test
    fun level46IsAsymmetric() {
        assertEquals(BoardType.ASYMMETRIC, calculator.getBoardType(46))
    }

    @Test
    fun level55IsAsymmetric() {
        assertEquals(BoardType.ASYMMETRIC, calculator.getBoardType(55))
    }

    @Test
    fun level56CyclesBackToFirstBoardType() {
        assertEquals(BoardType.entries[0], calculator.getBoardType(56))
    }

    @Test
    fun targetPegsNeverExceedBoardCapacity() {
        for (level in 1..100) {
            val boardType = calculator.getBoardType(level)
            val maxSlots = BoardFactory.createEmptyTemplate(boardType).countTotalSlots()
            val targetPegs = calculator.getTargetPegs(level)
            assertTrue(targetPegs < maxSlots, "Level $level: $targetPegs pegs >= $maxSlots slots")
        }
    }

    @Test
    fun targetPegsStartsAt3ForLevel1() {
        assertEquals(3, calculator.getTargetPegs(1))
    }

    @Test
    fun targetPegsAt12ForLevel10() {
        assertEquals(12, calculator.getTargetPegs(10))
    }

    @Test
    fun targetPegsIncreasesMonotonicallyWithinSamePhase() {
        for (level in 1..9) {
            assertTrue(
                calculator.getTargetPegs(level + 1) > calculator.getTargetPegs(level),
                "Target pegs did not increase from level $level to ${level + 1}"
            )
        }
    }
}
