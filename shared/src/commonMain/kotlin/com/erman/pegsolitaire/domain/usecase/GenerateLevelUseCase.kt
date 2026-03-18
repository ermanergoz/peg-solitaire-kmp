package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.engine.GeneratedLevel
import com.erman.pegsolitaire.engine.LevelGenerator

class GenerateLevelUseCase(private val levelGenerator: LevelGenerator) {

    operator fun invoke(levelNumber: Int): GeneratedLevel {
        return levelGenerator.generate(levelNumber)
    }
}
