package com.erman.pegsolitaire.data.service

import com.erman.pegsolitaire.domain.model.HapticType
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

actual class HapticService {

    actual fun vibrate(type: HapticType) {
        when (type) {
            HapticType.SELECTION -> impactFeedback(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
            HapticType.MOVE -> impactFeedback(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
            HapticType.ERROR -> notificationFeedback(UINotificationFeedbackType.UINotificationFeedbackTypeError)
            HapticType.SUCCESS -> notificationFeedback(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
        }
    }

    private fun impactFeedback(style: UIImpactFeedbackStyle) {
        val generator = UIImpactFeedbackGenerator(style)
        generator.prepare()
        generator.impactOccurred()
    }

    private fun notificationFeedback(type: UINotificationFeedbackType) {
        val generator = UINotificationFeedbackGenerator()
        generator.prepare()
        generator.notificationOccurred(type)
    }
}
