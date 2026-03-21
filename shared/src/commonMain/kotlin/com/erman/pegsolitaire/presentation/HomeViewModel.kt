package com.erman.pegsolitaire.presentation

import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.domain.usecase.ClearChallengeProgressUseCase
import com.erman.pegsolitaire.domain.usecase.GetAllBestScoresUseCase
import com.erman.pegsolitaire.domain.usecase.GetHighestCompletedLevelUseCase
import com.erman.pegsolitaire.engine.BoardType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val bestScores: Map<BoardType, GameScore> = emptyMap(),
    val hasOngoingChallenge: Boolean = false,
    val currentChallengeLevel: Int = 1,
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val getAllBestScoresUseCase: GetAllBestScoresUseCase,
    private val getHighestCompletedLevelUseCase: GetHighestCompletedLevelUseCase,
    private val clearChallengeProgressUseCase: ClearChallengeProgressUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadData() {
        loadJob?.cancel()
        _uiState.value = HomeUiState(isLoading = true)
        loadJob = scope.launch {
            try {
                val scoresDeferred = async { getAllBestScoresUseCase() }
                val highestCompletedDeferred = async { getHighestCompletedLevelUseCase() ?: 0 }

                val bestScores = scoresDeferred.await()
                val highestCompleted = highestCompletedDeferred.await()

                _uiState.value = HomeUiState(
                    bestScores = bestScores,
                    hasOngoingChallenge = highestCompleted > 0,
                    currentChallengeLevel = highestCompleted + 1,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(isLoading = false, error = genericErrorMessage())
            }
        }
    }

    fun clearChallengeProgress() {
        scope.launch {
            try {
                clearChallengeProgressUseCase()
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = genericErrorMessage())
            }
        }
    }

    fun onCleared() {
        scope.cancel()
    }
}
