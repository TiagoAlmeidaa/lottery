package com.tiagoalmeida.lottery.network.repository

import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.util.Constants
import com.tiagoalmeida.lottery.util.enums.LotteryType
import com.tiagoalmeida.lottery.util.extensions.getString
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class PreferencesRepositoryImplTest {

    // region variables

    private val dispatcher = TestCoroutineDispatcher()

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

    // endregion

    // region method: setup

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        repository = PreferencesRepositoryImpl(sharedPreferences, crashlytics)
    }

    @After
    fun finish() {
        Dispatchers.resetMain()

        dispatcher.cleanupTestCoroutines()
    }

    // endregion

    // region method: saveGame

    @Test
    fun `saveGame should save the games json correctly in preferences`() {
        // Given
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

        // When
        repository.saveGame(userGameNew)

        // Then
        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
        verify(exactly = 1) { editor.putString(Constants.SHARED_PREFERENCES_GAMES, expectedJson) }
    }

    // endregion

    // region method: removeGame

    @Test
    fun `removeGame should remove the games json correctly in preferences`() {
        // Given
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

        // When
        repository.removeGame(userGameNew)

        // Then
        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
        verify(exactly = 1) { editor.putString(Constants.SHARED_PREFERENCES_GAMES, expectedJson) }
    }

    // endregion

    // region method: getGames

    @Test
    fun `getGames should return the games correctly`() {
        // Given
        val games = mutableListOf(userGameOne, userGameTwo)
        val json = Gson().toJson(games)

        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns(json)

        // When
        val result = repository.getGames()

        // Then
        assertEquals(games, result)

        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
    }

    @Test
    fun `getGames should return empty list with empty json`() {
        // Given
        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns("")

        // When
        val result = repository.getGames()

        // Then
        assertEquals(0, result.size)

        coVerify(exactly = 0) { crashlytics.recordException(any()) }
        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
    }

    @Test
    fun `getGames should throw an exception with invalid json`() {
        // Given
        every {
            sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES)
        }.returns("1234")

        // When
        val result = repository.getGames()

        // Then
        assertEquals(0, result.size)

        coVerify(exactly = 1) { crashlytics.recordException(any()) }
        verify(exactly = 1) { sharedPreferences.getString(Constants.SHARED_PREFERENCES_GAMES) }
    }

    // endregion

    // region method: deleteAllGames

    @Test
    fun `deleteAllGames should be executed successfully`() {
        // Given
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

        // When
        repository.deleteAllGames()

        // Then
        verify(exactly = 1) { editor.putString(Constants.SHARED_PREFERENCES_GAMES, json) }
    }

    // endregion

}