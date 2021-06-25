package com.tiagoalmeida.lottery.viewmodel.games

import com.tiagoalmeida.lottery.model.vo.UserGame

sealed class GamesState {
    object GameRemoved : GamesState()
    object InternalError : GamesState()
    data class GamesReceived(val games: List<UserGame>) : GamesState()
    object Timeout : GamesState()
}