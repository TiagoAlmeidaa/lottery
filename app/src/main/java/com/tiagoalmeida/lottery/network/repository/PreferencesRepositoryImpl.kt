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
        sharedPreferences.putString(
            Constants.SHARED_PREFERENCES_GAMES,
            Gson().toJson(listOf<UserGame>())
        )
    }

    override fun getLastSavedContestNumber(type: LotteryType): Int {
        return sharedPreferences.getInt(getKey(type))
    }

    override fun saveLastContestNumber(type: LotteryType, contestNumber: Int) {
        sharedPreferences.putInt(getKey(type), contestNumber)
    }

    private fun getKey(type: LotteryType): String = when (type) {
        LotteryType.MEGASENA -> Constants.SHARED_PREFERENCES_LAST_MEGASENA
        LotteryType.LOTOFACIL -> Constants.SHARED_PREFERENCES_LAST_LOTOFACIL
        LotteryType.LOTOMANIA -> Constants.SHARED_PREFERENCES_LAST_LOTOMANIA
        LotteryType.QUINA -> Constants.SHARED_PREFERENCES_LAST_QUINA
        LotteryType.TIMEMANIA -> Constants.SHARED_PREFERENCES_LAST_TIMEMANIA
    }

}