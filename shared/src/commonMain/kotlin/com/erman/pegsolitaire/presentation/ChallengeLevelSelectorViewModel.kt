package com.erman.pegsolitaire.presentation

import com.erman.pegsolitaire.domain.model.LevelItem
import com.erman.pegsolitaire.domain.usecase.GetChallengeLevelsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 60

data class ChallengeLevelSelectorUiState(
    val levels: List<LevelItem> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

class ChallengeLevelSelectorViewModel(
    private val getChallengeLevelsUseCase: GetChallengeLevelsUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(ChallengeLevelSelectorUiState())
    val uiState: StateFlow<ChallengeLevelSelectorUiState> = _uiState.asStateFlow()

    private var nextFrom = 1

    fun loadInitialLevels() {
        nextFrom = 1
        _uiState.value = ChallengeLevelSelectorUiState(isLoading = true)
        scope.launch {
            try {
                val levels = getChallengeLevelsUseCase(nextFrom, PAGE_SIZE)
                nextFrom += PAGE_SIZE
                _uiState.value = ChallengeLevelSelectorUiState(
                    levels = levels,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = ChallengeLevelSelectorUiState(isLoading = false, error = GENERIC_ERROR_MESSAGE)
            }
        }
    }

    fun loadMoreLevels() {
        if (_uiState.value.isLoadingMore) return
        _uiState.update { it.copy(isLoadingMore = true) }
        scope.launch {
            try {
                val moreLevels = getChallengeLevelsUseCase(nextFrom, PAGE_SIZE)
                nextFrom += PAGE_SIZE
                _uiState.update { it.copy(levels = it.levels + moreLevels, isLoadingMore = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false, error = GENERIC_ERROR_MESSAGE) }
            }
        }
    }

    fun onCleared() {
        scope.cancel()
    }
}
