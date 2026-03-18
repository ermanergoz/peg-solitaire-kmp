package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.engine.Board
import com.erman.pegsolitaire.engine.BoardFactory
import com.erman.pegsolitaire.engine.BoardType

class CreateBoardUseCase {

    operator fun invoke(boardType: BoardType): Board {
        return BoardFactory.create(boardType)
    }
}
