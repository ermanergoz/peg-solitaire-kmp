package com.erman.pegsolitaire.presentation

import com.erman.pegsolitaire.domain.model.GameState
import com.erman.pegsolitaire.engine.Move

data class GameUiState(
    val gameState: GameState? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val pendingMove: Move? = null,
    val pendingInvalidMove: Boolean = false
)
