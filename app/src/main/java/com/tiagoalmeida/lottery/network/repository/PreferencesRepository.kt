package com.tiagoalmeida.lottery.network.repository

import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.util.Constants
import com.tiagoalmeida.lottery.util.enums.LotteryType
import com.tiagoalmeida.lottery.util.extensions.getInt
import com.tiagoalmeida.lottery.util.extensions.getString
import com.tiagoalmeida.lottery.util.extensions.putInt
import com.tiagoalmeida.lottery.util.extensions.putString

class PreferencesRepository(
    private val sharedPreferences: SharedPreferences,
    private val crashlytics: FirebaseCrashlytics
) {

    fun saveGame(game: UserGame) {
        val games = mutableListOf<UserGame>().apply {
            addAll(getGames())
            add(game)
        }

        sharedPreferences.putString(Constants.SHARED_PREFERENCES_GAMES, Gson().toJson(games))
    }

    fun removeGame(game: UserGame) {
        val games = getGames().toMutableList()

        if (games.contains(game)) {
            games.remove(game)
        }

        sharedPreferences.putString(Constants.SHARED_PREFERENCES_GAMES, Gson().toJson(games))
    }

    fun getGames(): List<UserGame> {
        val json = sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        return try {
            val type = object: TypeToken<List<UserGame>>(){}.type
            Gson().fromJson(json, type)
        } catch (exception: Exception) {
            crashlytics.recordException(exception)

            listOf()
        }
    }

    fun deleteAllGames() {
        sharedPreferences.putString(Constants.SHARED_PREFERENCES_GAMES, Gson().toJson(listOf<UserGame>()))
    }

    fun getLastSavedContestNumber(type: LotteryType): Int {
        val key = when (type) {
            LotteryType.MEGASENA -> Constants.SHARED_PREFERENCES_LAST_MEGASENA
            LotteryType.LOTOFACIL -> Constants.SHARED_PREFERENCES_LAST_LOTOFACIL
            LotteryType.LOTOMANIA -> Constants.SHARED_PREFERENCES_LAST_LOTOMANIA
            else -> Constants.SHARED_PREFERENCES_LAST_QUINA
        }
        return sharedPreferences.getInt(key)
    }

    fun saveLastContestNumber(type: LotteryType, contestNumber: Int) {
        val key = when (type) {
            LotteryType.MEGASENA -> Constants.SHARED_PREFERENCES_LAST_MEGASENA
            LotteryType.LOTOFACIL -> Constants.SHARED_PREFERENCES_LAST_LOTOFACIL
            LotteryType.LOTOMANIA -> Constants.SHARED_PREFERENCES_LAST_LOTOMANIA
            else -> Constants.SHARED_PREFERENCES_LAST_QUINA
        }
        sharedPreferences.putInt(key, contestNumber)
    }

}