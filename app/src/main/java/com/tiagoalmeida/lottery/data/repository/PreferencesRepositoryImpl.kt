package com.tiagoalmeida.lottery.data.repository

import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.extensions.getInt
import com.tiagoalmeida.lottery.extensions.getString
import com.tiagoalmeida.lottery.extensions.putInt
import com.tiagoalmeida.lottery.extensions.putString
import com.tiagoalmeida.lottery.util.Constants

internal class PreferencesRepositoryImpl(
        private val sharedPreferences: SharedPreferences,
        private val crashlytics: FirebaseCrashlytics
) : PreferencesRepository {

    override fun saveGame(game: UserGame) {
        val games = mutableListOf<UserGame>().apply {
            addAll(getGames())
            add(game)
        }

        sharedPreferences.putString(Constants.SHARED_PREFERENCES_GAMES, Gson().toJson(games))
    }

    override fun removeGame(game: UserGame) {
        val games = getGames().toMutableList()

        if (games.contains(game)) {
            games.remove(game)
        }

        sharedPreferences.putString(Constants.SHARED_PREFERENCES_GAMES, Gson().toJson(games))
    }

    override fun getGames(): List<UserGame> {
        val json = sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)

        if (json.isEmpty())
            return listOf()

        return try {
            val type = object : TypeToken<List<UserGame>>() {}.type
            Gson().fromJson(json, type)
        } catch (exception: Exception) {
            crashlytics.recordException(exception)

            listOf()
        }
    }

    override fun deleteAllGames() {
        sharedPreferences.putString(Constants.SHARED_PREFERENCES_GAMES, Gson().toJson(listOf<UserGame>()))
    }

    override fun getLastSavedContestNumber(type: LotteryType): Int {
        val key = when (type) {
            LotteryType.MEGASENA -> Constants.SHARED_PREFERENCES_LAST_MEGASENA
            LotteryType.LOTOFACIL -> Constants.SHARED_PREFERENCES_LAST_LOTOFACIL
            LotteryType.LOTOMANIA -> Constants.SHARED_PREFERENCES_LAST_LOTOMANIA
            LotteryType.QUINA -> Constants.SHARED_PREFERENCES_LAST_QUINA
        }
        return sharedPreferences.getInt(key)
    }

    override fun saveLastContestNumber(type: LotteryType, contestNumber: Int) {
        val key = when (type) {
            LotteryType.MEGASENA -> Constants.SHARED_PREFERENCES_LAST_MEGASENA
            LotteryType.LOTOFACIL -> Constants.SHARED_PREFERENCES_LAST_LOTOFACIL
            LotteryType.LOTOMANIA -> Constants.SHARED_PREFERENCES_LAST_LOTOMANIA
            LotteryType.QUINA -> Constants.SHARED_PREFERENCES_LAST_QUINA
        }
        sharedPreferences.putInt(key, contestNumber)
    }

}
