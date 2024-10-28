package com.tiagoalmeida.lottery.data.repository

import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.model.UserGame

interface PreferencesRepository {
    fun saveGame(game: UserGame)
    fun removeGame(game: UserGame)
    fun getGames(): List<UserGame>
    fun deleteAllGames()
    fun getLastSavedContestNumber(type: LotteryType): Int
    fun saveLastContestNumber(type: LotteryType, contestNumber: Int)
}
