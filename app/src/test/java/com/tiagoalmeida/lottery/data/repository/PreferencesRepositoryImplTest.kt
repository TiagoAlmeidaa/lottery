package com.tiagoalmeida.lottery.data.repository

import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.extensions.getString
import com.tiagoalmeida.lottery.util.Constants
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PreferencesRepositoryImplTest {

    @MockK(relaxed = true)
    lateinit var sharedPreferences: SharedPreferences

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK(relaxed = true)
    lateinit var editor: SharedPreferences.Editor

    private lateinit var repository: PreferencesRepositoryImpl

    private val userGameOne = UserGame(
        startContestNumber = "1000",
        endContestNumber = "1001",
        type = LotteryType.QUINA,
        numbers = mutableListOf(1, 2, 3, 4, 5)
    )

    private val userGameTwo = UserGame(
        startContestNumber = "2000",
        endContestNumber = "2001",
        type = LotteryType.MEGASENA,
        numbers = mutableListOf(1, 2, 3, 4, 5, 6)
    )

    private val userGameNew = UserGame(
        startContestNumber = "3000",
        endContestNumber = "3001",
        type = LotteryType.QUINA,
        numbers = mutableListOf(1, 2, 3, 4, 5)
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        repository = PreferencesRepositoryImpl(sharedPreferences, crashlytics)
    }

    @Test
    fun `saveGame should save the games json correctly in preferences`() {
        val games = mutableListOf(userGameOne, userGameTwo)
        val json = Gson().toJson(games)

        val expectedGames = mutableListOf(userGameOne, userGameTwo, userGameNew)
        val expectedJson = Gson().toJson(expectedGames)

        every {
            sharedPreferences.edit()
        }.returns(editor)

        every {
            editor.putString(Constants.SHARED_PREFERENCES_GAMES, expectedJson)
        }.returns(editor)

        every {
            editor.apply()
        }.just(runs)

        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns(json)

        repository.saveGame(userGameNew)

        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
        verify(exactly = 1) { editor.putString(Constants.SHARED_PREFERENCES_GAMES, expectedJson) }
    }

    @Test
    fun `removeGame should remove the games json correctly in preferences`() {
        val games = mutableListOf(userGameOne, userGameTwo, userGameNew)
        val json = Gson().toJson(games)

        val expectedGames = mutableListOf(userGameOne, userGameTwo)
        val expectedJson = Gson().toJson(expectedGames)

        every {
            sharedPreferences.edit()
        }.returns(editor)

        every {
            editor.putString(Constants.SHARED_PREFERENCES_GAMES, expectedJson)
        }.returns(editor)

        every {
            editor.apply()
        }.just(runs)

        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns(json)

        repository.removeGame(userGameNew)

        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
        verify(exactly = 1) { editor.putString(Constants.SHARED_PREFERENCES_GAMES, expectedJson) }
    }

    @Test
    fun `getGames should return the games correctly`() {
        val games = mutableListOf(userGameOne, userGameTwo)
        val json = Gson().toJson(games)

        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns(json)

        val result = repository.getGames()

        assertEquals(games, result)

        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
    }

    @Test
    fun `getGames should return empty list with empty json`() {
        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns("")

        val result = repository.getGames()

        assertEquals(0, result.size)

        coVerify(exactly = 0) { crashlytics.recordException(any()) }
        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
    }

    @Test
    fun `getGames should throw an exception with invalid json`() {
        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns("1234")

        val result = repository.getGames()

        assertEquals(0, result.size)

        coVerify(exactly = 1) { crashlytics.recordException(any()) }
        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
    }

    @Test
    fun `deleteAllGames should be executed successfully`() {
        val json = Gson().toJson(listOf<UserGame>())

        every {
            sharedPreferences.edit()
        }.returns(editor)

        every {
            editor.putString(Constants.SHARED_PREFERENCES_GAMES, json)
        }.returns(editor)

        every {
            editor.apply()
        }.just(runs)

        repository.deleteAllGames()

        verify(exactly = 1) { editor.putString(Constants.SHARED_PREFERENCES_GAMES, json) }
    }
}
