package com.erman.pegsolitaire.di

import com.erman.pegsolitaire.presentation.ChallengeLevelSelectorViewModel
import com.erman.pegsolitaire.presentation.GameViewModel
import com.erman.pegsolitaire.presentation.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(sharedModule, iosModule)
    }
}

class KoinHelper : KoinComponent {
    fun getGameViewModel(): GameViewModel = get()
    fun getHomeViewModel(): HomeViewModel = get()
    fun getChallengeLevelSelectorViewModel(): ChallengeLevelSelectorViewModel = get()
}
