package com.erman.pegsolitaire.di

import android.content.Context
import com.erman.pegsolitaire.data.local.DatabaseDriverFactory
import com.erman.pegsolitaire.data.service.HapticService
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.dsl.module

private const val SETTINGS_PREFERENCES_NAME = "settings"

@OptIn(ExperimentalSettingsApi::class)
val androidModule = module {
    single { DatabaseDriverFactory(get()) }
    single { HapticService(get()) }
    single<FlowSettings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE)
        ).toFlowSettings()
    }
}
