package com.tiagoalmeida.lottery.ui.games

import com.tiagoalmeida.lottery.data.model.UserGame

sealed class GamesState {
    object GameRemoved : GamesState()
    object InternalError : GamesState()
    data class GamesReceived(val games: List<UserGame>) : GamesState()
    object Timeout : GamesState()
}