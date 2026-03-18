package com.erman.pegsolitaire.di

import com.erman.pegsolitaire.data.local.DatabaseDriverFactory
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(get()) }
}
