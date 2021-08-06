package com.tiagoalmeida.lottery.network.repository

import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.util.enums.LotteryType

interface PreferencesRepository {
    fun saveGame(game: UserGame)
    fun removeGame(game: UserGame)
    fun getGames(): List<UserGame>
    fun deleteAllGames()
    fun getLastSavedContestNumber(type: LotteryType): Int
    fun saveLastContestNumber(type: LotteryType, contestNumber: Int)
}