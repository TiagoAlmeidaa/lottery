package com.tiagoalmeida.lottery.ui.games.adapter.listener

import android.view.View
import com.tiagoalmeida.lottery.model.vo.UserGame

interface GamesAdapterEvents {
    fun onGameClicked(userGame: UserGame, view: View)
    fun onGameLongClicked(userGame: UserGame): Boolean
}