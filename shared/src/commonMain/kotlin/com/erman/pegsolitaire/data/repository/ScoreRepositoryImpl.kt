package com.erman.pegsolitaire.data.repository

import com.erman.pegsolitaire.data.local.PegSolitaireDatabase
import com.erman.pegsolitaire.domain.model.GameScore
import com.erman.pegsolitaire.domain.repository.ScoreRepository
import com.erman.pegsolitaire.engine.BoardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ScoreRepositoryImpl(database: PegSolitaireDatabase) : ScoreRepository {

    private val queries = database.pegSolitaireQueries

    override suspend fun getBestScore(boardType: BoardType): GameScore? = withContext(Dispatchers.IO) {
        queries.getBestScore(boardType.name).executeAsOneOrNull()?.let {
            val parsedBoardType = try {
                BoardType.valueOf(it.boardType)
            } catch (_: IllegalArgumentException) {
                return@let null
            }
            GameScore(
                boardType = parsedBoardType,
                remainingPegs = it.remainingPegs.toInt(),
                elapsedTimeMillis = it.elapsedTimeMillis
            )
        }
    }

    override suspend fun saveScore(
        boardType: BoardType,
        remainingPegs: Int,
        elapsedTimeMillis: Long
    ) = withContext(Dispatchers.IO) {
        queries.insertScore(boardType.name, remainingPegs.toLong(), elapsedTimeMillis)
        queries.cleanupOldScores()
    }

    override suspend fun clearAllScores() = withContext(Dispatchers.IO) {
        queries.deleteAllScores()
    }
}
