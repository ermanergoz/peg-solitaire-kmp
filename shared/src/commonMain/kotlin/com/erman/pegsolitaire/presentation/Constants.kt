package com.erman.pegsolitaire.presentation

const val SCORE_SEPARATOR = " / "
internal const val GENERIC_ERROR_MESSAGE = "Something went wrong. Please try again."

private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val TIME_PAD_LENGTH = 2
private const val TIME_PAD_CHAR = '0'
private const val TIME_SEPARATOR = ":"

fun formatElapsedTime(millis: Long): String {
    val totalSeconds = millis / MILLIS_PER_SECOND
    val minutes = (totalSeconds / SECONDS_PER_MINUTE).toString().padStart(TIME_PAD_LENGTH, TIME_PAD_CHAR)
    val seconds = (totalSeconds % SECONDS_PER_MINUTE).toString().padStart(TIME_PAD_LENGTH, TIME_PAD_CHAR)
    return "$minutes$TIME_SEPARATOR$seconds"
}
