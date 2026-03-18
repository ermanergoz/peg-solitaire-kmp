package com.erman.pegsolitaire

import android.app.Application
import com.erman.pegsolitaire.di.androidModule
import com.erman.pegsolitaire.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PegSolitaireApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PegSolitaireApplication)
            modules(sharedModule, androidModule)
        }
    }
}
