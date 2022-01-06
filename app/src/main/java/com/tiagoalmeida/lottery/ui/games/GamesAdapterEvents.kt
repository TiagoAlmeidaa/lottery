package com.tiagoalmeida.lottery.ui.games

import android.view.View
import com.tiagoalmeida.lottery.data.model.UserGame

interface GamesAdapterEvents {
    fun onGameClicked(userGame: UserGame, view: View)
    fun onGameLongClicked(userGame: UserGame): Boolean
}