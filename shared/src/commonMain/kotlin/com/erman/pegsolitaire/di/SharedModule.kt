package com.erman.pegsolitaire.di

import com.erman.pegsolitaire.data.local.DatabaseDriverFactory
import com.erman.pegsolitaire.data.local.PegSolitaireDatabase
import com.erman.pegsolitaire.data.repository.LevelRepositoryImpl
import com.erman.pegsolitaire.data.repository.ScoreRepositoryImpl
import com.erman.pegsolitaire.data.repository.SettingsRepositoryImpl
import com.erman.pegsolitaire.domain.repository.LevelRepository
import com.erman.pegsolitaire.domain.repository.ScoreRepository
import com.erman.pegsolitaire.domain.repository.SettingsRepository
import com.erman.pegsolitaire.domain.usecase.ClearAllScoresUseCase
import com.erman.pegsolitaire.domain.usecase.ClearChallengeProgressUseCase
import com.erman.pegsolitaire.domain.usecase.CreateBoardUseCase
import com.erman.pegsolitaire.domain.usecase.GenerateLevelUseCase
import com.erman.pegsolitaire.domain.usecase.GetAllBestScoresUseCase
import com.erman.pegsolitaire.domain.usecase.GetBestScoreUseCase
import com.erman.pegsolitaire.domain.usecase.GetChallengeLevelsUseCase
import com.erman.pegsolitaire.domain.usecase.GetHighestCompletedLevelUseCase
import com.erman.pegsolitaire.domain.usecase.GetSettingsUseCase
import com.erman.pegsolitaire.domain.usecase.PerformHapticUseCase
import com.erman.pegsolitaire.domain.usecase.ProcessCellClickUseCase
import com.erman.pegsolitaire.domain.usecase.ResetAllScoresUseCase
import com.erman.pegsolitaire.domain.usecase.SaveLevelProgressUseCase
import com.erman.pegsolitaire.domain.usecase.SaveScoreUseCase
import com.erman.pegsolitaire.domain.usecase.UpdateSettingUseCase
import com.erman.pegsolitaire.engine.DifficultyCalculator
import com.erman.pegsolitaire.engine.GameEngine
import com.erman.pegsolitaire.engine.LevelGenerator
import com.erman.pegsolitaire.engine.MoveValidator
import com.erman.pegsolitaire.presentation.ChallengeLevelSelectorViewModel
import com.erman.pegsolitaire.presentation.GameViewModel
import com.erman.pegsolitaire.presentation.HomeViewModel
import com.erman.pegsolitaire.presentation.SettingsViewModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val sharedModule = module {
    single { MoveValidator() }
    single { DifficultyCalculator() }
    single { GameEngine(get()) }
    single { LevelGenerator(get()) }
    single { PegSolitaireDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<ScoreRepository> { ScoreRepositoryImpl(get()) }
    single<LevelRepository> { LevelRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get<FlowSettings>()) }
    factory { ProcessCellClickUseCase(get()) }
    factory { CreateBoardUseCase() }
    factory { SaveScoreUseCase(get()) }
    factory { GetBestScoreUseCase(get()) }
    factory { GetAllBestScoresUseCase(get()) }
    factory { SaveLevelProgressUseCase(get()) }
    factory { GetChallengeLevelsUseCase(get<LevelRepository>(), get<DifficultyCalculator>()) }
    factory { GenerateLevelUseCase(get()) }
    factory { GetHighestCompletedLevelUseCase(get()) }
    factory { ClearChallengeProgressUseCase(get()) }
    factory { ClearAllScoresUseCase(get()) }
    factory { GetSettingsUseCase(get()) }
    factory { UpdateSettingUseCase(get()) }
    factory { PerformHapticUseCase(get(), get()) }
    factory { ResetAllScoresUseCase(get(), get()) }
    factory { GameViewModel(get(), get(), get(), get(), get(), get()) }
    factory { HomeViewModel(get(), get(), get()) }
    factory { ChallengeLevelSelectorViewModel(get()) }
    factory { SettingsViewModel(get(), get(), get()) }
}
