package com.erman.pegsolitaire.di

import com.erman.pegsolitaire.data.local.DatabaseDriverFactory
import com.erman.pegsolitaire.data.local.PegSolitaireDatabase
import com.erman.pegsolitaire.data.repository.LevelRepositoryImpl
import com.erman.pegsolitaire.data.repository.ScoreRepositoryImpl
import com.erman.pegsolitaire.domain.repository.LevelRepository
import com.erman.pegsolitaire.domain.repository.ScoreRepository
import com.erman.pegsolitaire.domain.usecase.ClearChallengeProgressUseCase
import com.erman.pegsolitaire.domain.usecase.CreateBoardUseCase
import com.erman.pegsolitaire.domain.usecase.GenerateLevelUseCase
import com.erman.pegsolitaire.domain.usecase.GetAllBestScoresUseCase
import com.erman.pegsolitaire.domain.usecase.GetBestScoreUseCase
import com.erman.pegsolitaire.domain.usecase.GetChallengeLevelsUseCase
import com.erman.pegsolitaire.domain.usecase.GetHighestCompletedLevelUseCase
import com.erman.pegsolitaire.domain.usecase.ProcessCellClickUseCase
import com.erman.pegsolitaire.domain.usecase.SaveLevelProgressUseCase
import com.erman.pegsolitaire.domain.usecase.SaveScoreUseCase
import com.erman.pegsolitaire.engine.DifficultyCalculator
import com.erman.pegsolitaire.engine.GameEngine
import com.erman.pegsolitaire.engine.LevelGenerator
import com.erman.pegsolitaire.engine.MoveValidator
import com.erman.pegsolitaire.presentation.ChallengeLevelSelectorViewModel
import com.erman.pegsolitaire.presentation.GameViewModel
import com.erman.pegsolitaire.presentation.HomeViewModel
import org.koin.dsl.module

val sharedModule = module {
    single { MoveValidator() }
    single { DifficultyCalculator() }
    single { GameEngine(get()) }
    single { LevelGenerator(get()) }
    single { PegSolitaireDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<ScoreRepository> { ScoreRepositoryImpl(get()) }
    single<LevelRepository> { LevelRepositoryImpl(get()) }
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
    factory { GameViewModel(get(), get(), get(), get(), get()) }
    factory { HomeViewModel(get(), get(), get()) }
    factory { ChallengeLevelSelectorViewModel(get()) }
}
