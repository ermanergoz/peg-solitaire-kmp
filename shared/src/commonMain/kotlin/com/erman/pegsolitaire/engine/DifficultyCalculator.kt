package com.erman.pegsolitaire.engine

private const val PHASE_ONE_MAX = 10
private const val ENGLISH_MAX_LEVEL = 25
private const val FRENCH_MAX_LEVEL = 35
private const val DIAMOND_MAX_LEVEL = 45
private const val ASYMMETRIC_MAX_LEVEL = 55
private const val CYCLE_START_LEVEL = 56

private const val BASE_PEGS_PHASE_ONE = 3
private const val PHASE_TWO_START_PEGS = 13
private const val PHASE_THREE_START_PEGS = 10
private const val PHASE_THREE_STEP = 2
private const val PHASE_FOUR_STEP = 3

private const val CYCLE_BASE_PEGS = 15
private const val CYCLE_PEG_INCREMENT = 2

private val TOTAL_SLOTS = mapOf(
    BoardType.ENGLISH to 33,
    BoardType.FRENCH to 37,
    BoardType.GERMAN to 45,
    BoardType.ASYMMETRIC to 39,
    BoardType.DIAMOND to 41
)

class DifficultyCalculator {

    fun getBoardType(levelNumber: Int): BoardType {
        if (levelNumber <= ASYMMETRIC_MAX_LEVEL) {
            return getBoardTypeForEarlyLevel(levelNumber)
        }
        return getBoardTypeForCycledLevel(levelNumber)
    }

    fun getTargetPegs(levelNumber: Int): Int {
        val boardType = getBoardType(levelNumber)
        val maxValidCells = TOTAL_SLOTS.getValue(boardType)
        return calculateTargetPegs(levelNumber, maxValidCells)
    }

    internal fun calculateTargetPegs(levelNumber: Int, maxValidCells: Int): Int {
        val pegs = calculateRawPegs(levelNumber)
        return pegs.coerceAtMost(maxValidCells - 1)
    }

    private fun getBoardTypeForEarlyLevel(levelNumber: Int): BoardType = when {
        levelNumber <= ENGLISH_MAX_LEVEL -> BoardType.ENGLISH
        levelNumber <= FRENCH_MAX_LEVEL -> BoardType.FRENCH
        levelNumber <= DIAMOND_MAX_LEVEL -> BoardType.DIAMOND
        else -> BoardType.ASYMMETRIC
    }

    private fun getBoardTypeForCycledLevel(levelNumber: Int): BoardType {
        val cycleIndex = (levelNumber - CYCLE_START_LEVEL) % BoardType.entries.size
        return BoardType.entries[cycleIndex]
    }

    private fun calculateRawPegs(levelNumber: Int): Int = when {
        levelNumber <= PHASE_ONE_MAX ->
            BASE_PEGS_PHASE_ONE + (levelNumber - 1)
        levelNumber <= ENGLISH_MAX_LEVEL ->
            PHASE_TWO_START_PEGS + (levelNumber - PHASE_ONE_MAX - 1)
        levelNumber <= FRENCH_MAX_LEVEL ->
            PHASE_THREE_START_PEGS + (levelNumber - ENGLISH_MAX_LEVEL - 1) * PHASE_THREE_STEP
        levelNumber <= DIAMOND_MAX_LEVEL ->
            PHASE_THREE_START_PEGS + (levelNumber - FRENCH_MAX_LEVEL - 1) * PHASE_FOUR_STEP
        levelNumber <= ASYMMETRIC_MAX_LEVEL ->
            PHASE_THREE_START_PEGS + (levelNumber - DIAMOND_MAX_LEVEL - 1) * PHASE_FOUR_STEP
        else ->
            calculateCycledPegs(levelNumber)
    }

    private fun calculateCycledPegs(levelNumber: Int): Int {
        val cyclesCompleted = (levelNumber - CYCLE_START_LEVEL) / BoardType.entries.size
        val posInCycle = (levelNumber - CYCLE_START_LEVEL) % BoardType.entries.size
        return CYCLE_BASE_PEGS + cyclesCompleted * CYCLE_PEG_INCREMENT + posInCycle
    }
}
