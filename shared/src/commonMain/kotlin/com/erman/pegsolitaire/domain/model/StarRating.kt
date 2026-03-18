package com.erman.pegsolitaire.domain.model

private const val THREE_STAR_THRESHOLD = 1
private const val TWO_STAR_THRESHOLD = 3
private const val THREE_STARS = 3
private const val TWO_STARS = 2
private const val ONE_STAR = 1

fun calculateStars(remainingPegs: Int): Int = when {
    remainingPegs == THREE_STAR_THRESHOLD -> THREE_STARS
    remainingPegs <= TWO_STAR_THRESHOLD -> TWO_STARS
    else -> ONE_STAR
}
