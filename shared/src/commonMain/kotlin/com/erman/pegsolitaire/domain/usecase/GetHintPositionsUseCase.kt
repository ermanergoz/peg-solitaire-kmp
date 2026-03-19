package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.engine.GameEngine
import com.erman.pegsolitaire.engine.Position

class GetHintPositionsUseCase(private val gameEngine: GameEngine) {

    operator fun invoke(board: Board): Set<Position> =
        gameEngine.getValidMoves(board).map { it.to }.toSet()
}
