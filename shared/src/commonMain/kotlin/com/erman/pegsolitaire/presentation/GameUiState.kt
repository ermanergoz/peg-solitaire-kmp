package com.erman.pegsolitaire.presentation

import com.erman.pegsolitaire.domain.model.GameState

data class GameUiState(
    val gameState: GameState? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
