package com.erman.pegsolitaire.localization

import pegsolitaire.shared.generated.resources.Res
import pegsolitaire.shared.generated.resources.app_title
import pegsolitaire.shared.generated.resources.browse_all_levels
import pegsolitaire.shared.generated.resources.cancel
import pegsolitaire.shared.generated.resources.challenge_levels
import pegsolitaire.shared.generated.resources.challenge_mode
import pegsolitaire.shared.generated.resources.classic_mode
import pegsolitaire.shared.generated.resources.game_over
import pegsolitaire.shared.generated.resources.generic_error
import pegsolitaire.shared.generated.resources.haptic_feedback
import pegsolitaire.shared.generated.resources.level_n
import pegsolitaire.shared.generated.resources.next
import pegsolitaire.shared.generated.resources.not_played
import pegsolitaire.shared.generated.resources.play
import pegsolitaire.shared.generated.resources.quit
import pegsolitaire.shared.generated.resources.reset
import pegsolitaire.shared.generated.resources.reset_all_scores
import pegsolitaire.shared.generated.resources.reset_all_scores_confirm
import pegsolitaire.shared.generated.resources.restart
import pegsolitaire.shared.generated.resources.retry
import pegsolitaire.shared.generated.resources.score_left
import pegsolitaire.shared.generated.resources.settings
import pegsolitaire.shared.generated.resources.sound_effects

object AppStrings {
    fun appTitle(): String = StringHelper.get(Res.string.app_title)
    fun challengeMode(): String = StringHelper.get(Res.string.challenge_mode)
    fun classicMode(): String = StringHelper.get(Res.string.classic_mode)
    fun play(): String = StringHelper.get(Res.string.play)
    fun browseAllLevels(): String = StringHelper.get(Res.string.browse_all_levels)
    fun levelN(n: Int): String = StringHelper.get(Res.string.level_n, n)
    fun settings(): String = StringHelper.get(Res.string.settings)
    fun soundEffects(): String = StringHelper.get(Res.string.sound_effects)
    fun hapticFeedback(): String = StringHelper.get(Res.string.haptic_feedback)
    fun resetAllScores(): String = StringHelper.get(Res.string.reset_all_scores)
    fun resetAllScoresConfirm(): String = StringHelper.get(Res.string.reset_all_scores_confirm)
    fun reset(): String = StringHelper.get(Res.string.reset)
    fun cancel(): String = StringHelper.get(Res.string.cancel)
    fun challengeLevels(): String = StringHelper.get(Res.string.challenge_levels)
    fun gameOver(): String = StringHelper.get(Res.string.game_over)
    fun quit(): String = StringHelper.get(Res.string.quit)
    fun restart(): String = StringHelper.get(Res.string.restart)
    fun next(): String = StringHelper.get(Res.string.next)
    fun retry(): String = StringHelper.get(Res.string.retry)
    fun notPlayed(): String = StringHelper.get(Res.string.not_played)
    fun genericError(): String = StringHelper.get(Res.string.generic_error)
    fun scoreLeft(n: Int): String = StringHelper.get(Res.string.score_left, n)
}
