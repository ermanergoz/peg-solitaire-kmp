package com.erman.pegsolitaire.ui.theme

import androidx.compose.ui.graphics.Color
import com.erman.pegsolitaire.engine.BoardType

val CandyCoral = Color(0xFFFF6B8A)
val CandyCoralDark = Color(0xFFE85D7A)
val CandyLavender = Color(0xFFA78BFA)
val CandyMint = Color(0xFF5EEAD4)

val BackgroundLight = Color(0xFFFEFAFF)
val BackgroundDark = Color(0xFF1A1025)

val PegColor = Color(0xFFE83A5D)
val PegColorCenter = Color(0xFFFF7B95)
val MarkedPegColor = Color(0xFFFFD700)
val MarkedPegGlow = Color(0x40FFD700)
val MarkedPegCenter = Color(0xFFFFF064)

val PegSlotColorLight = Color(0xFFBEC4D0)
val PegSlotInnerLight = Color(0xFFB0B6C2)
val PegSlotColorDark = Color(0xFF3A3F4A)
val PegSlotInnerDark = Color(0xFF32373F)

val TextPrimaryLight = Color(0xFF2D1B4E)
val TextSecondaryLight = Color(0xFF6B5B7B)
val TextPrimaryDark = Color(0xFFF0E6FF)
val TextSecondaryDark = Color(0xFFB0A0C0)

val CardBackgroundLight = Color(0xFFFFFFFF)
val CardBackgroundDark = Color(0xFF2A1F3A)

// Game bar pill/badge colors
val BadgePurple = Color(0xFF6C63FF)
val BadgeGreen = Color(0xFF4ADE80)
val BadgeBlue = Color(0xFF60A5FA)
val BadgeRed = Color(0xFFEF4444)
val BadgeGray = Color(0xFF9CA3AF)

// Board-themed backgrounds inspired by country flags
private val EnglishBackgroundLight = Color(0xFFDCE4F8)
private val EnglishBackgroundDark = Color(0xFF1A1E2E)

private val FrenchBackgroundLight = Color(0xFFF5DAE0)
private val FrenchBackgroundDark = Color(0xFF2C1B1D)

private val GermanBackgroundLight = Color(0xFFF5EDDA)
private val GermanBackgroundDark = Color(0xFF2A2418)

private val AsymmetricBackgroundLight = Color(0xFFDAF0E8)
private val AsymmetricBackgroundDark = Color(0xFF182A22)

private val DiamondBackgroundLight = Color(0xFFF0DAEA)
private val DiamondBackgroundDark = Color(0xFF2A1828)

fun boardBackgroundColor(boardType: BoardType, isDark: Boolean): Color = when (boardType) {
    BoardType.ENGLISH -> if (isDark) EnglishBackgroundDark else EnglishBackgroundLight
    BoardType.FRENCH -> if (isDark) FrenchBackgroundDark else FrenchBackgroundLight
    BoardType.GERMAN -> if (isDark) GermanBackgroundDark else GermanBackgroundLight
    BoardType.ASYMMETRIC -> if (isDark) AsymmetricBackgroundDark else AsymmetricBackgroundLight
    BoardType.DIAMOND -> if (isDark) DiamondBackgroundDark else DiamondBackgroundLight
}
