package com.erman.pegsolitaire.di

import com.erman.pegsolitaire.data.local.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
}
