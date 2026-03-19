package com.erman.pegsolitaire.data.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.erman.pegsolitaire.domain.model.HapticType

private const val SELECTION_DURATION_MILLIS = 30L
private const val SELECTION_AMPLITUDE = 80

private const val MOVE_DURATION_MILLIS = 50L
private const val MOVE_AMPLITUDE = 150

private const val ERROR_DURATION_MILLIS = 100L
private const val ERROR_AMPLITUDE = 255

private const val SUCCESS_DURATION_MILLIS = 100L
private const val SUCCESS_AMPLITUDE = 200

actual class HapticService(private val context: Context) {

    actual fun vibrate(type: HapticType) {
        val vibrator = resolveVibrator() ?: return
        val (duration, amplitude) = hapticParameters(type)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    private fun resolveVibrator(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private fun hapticParameters(type: HapticType): Pair<Long, Int> = when (type) {
        HapticType.SELECTION -> SELECTION_DURATION_MILLIS to SELECTION_AMPLITUDE
        HapticType.MOVE -> MOVE_DURATION_MILLIS to MOVE_AMPLITUDE
        HapticType.ERROR -> ERROR_DURATION_MILLIS to ERROR_AMPLITUDE
        HapticType.SUCCESS -> SUCCESS_DURATION_MILLIS to SUCCESS_AMPLITUDE
    }
}
