package com.erman.pegsolitaire.data.service

import com.erman.pegsolitaire.domain.model.HapticType

expect class HapticService {
    fun vibrate(type: HapticType)
}
