package com.erman.pegsolitaire.domain.usecase

import com.erman.pegsolitaire.engine.Move

sealed class CellClickEvent {
    data object Selected : CellClickEvent()
    data object Deselected : CellClickEvent()
    data object Invalid : CellClickEvent()
    data class Moved(val move: Move) : CellClickEvent()
}
