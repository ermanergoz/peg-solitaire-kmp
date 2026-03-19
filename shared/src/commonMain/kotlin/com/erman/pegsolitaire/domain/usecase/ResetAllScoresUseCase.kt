package com.erman.pegsolitaire.domain.usecase

class ResetAllScoresUseCase(
    private val clearAllScoresUseCase: ClearAllScoresUseCase,
    private val clearChallengeProgressUseCase: ClearChallengeProgressUseCase
) {

    suspend operator fun invoke() {
        try {
            clearAllScoresUseCase()
        } catch (e: Exception) {
            // Continue to clear challenge progress even if scores fail
        }
        clearChallengeProgressUseCase()
    }
}
