package com.tiagoalmeida.lottery.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameRegisterViewModelTest {

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK(relaxed = true)
    lateinit var analytics: FirebaseAnalytics

    @MockK
    lateinit var preferencesRepository: PreferencesRepository

    @MockK(relaxed = true)
    lateinit var observer: Observer<GameRegisterState>

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: GameRegisterViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        viewModel = GameRegisterViewModel(
            crashlytics,
            analytics,
            preferencesRepository
        )
        viewModel.viewState.observeForever(observer)
    }

    @After
    fun finish() {
        viewModel.viewState.removeObserver(observer)
    }

    @Test
    fun `validateLotteryType should set state to LotteryTypeOk`() {
        val lotteryType = LotteryType.MEGASENA
        val state = GameRegisterState.LotteryTypeOk

        viewModel.validateLotteryType(lotteryType)

        verify(exactly = 1) { observer.onChanged(state) }

        val userGame = viewModel.userGame.value!!
        assertEquals(userGame.type, lotteryType)
    }

    @Test
    fun `validateLotteryType should set state to LotteryTypeInvalid`() {
        val state = GameRegisterState.LotteryTypeInvalid

        viewModel.validateLotteryType(null)

        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `validateContest when userGame is null should set state ContestWithError`() {
        val expectedState = GameRegisterState.ContestWithError()

        viewModel.userGame.value = null

        viewModel.validateContest()

        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `validateContest when userGame only has endContestNumber should set state ContestWithError with start contest empty message`() {
        val expectedState = GameRegisterState.ContestWithError(R.string.game_register_error_start_contest_empty)
        val userGame = UserGame().apply { endContestNumber = "1111" }

        viewModel.userGame.value = userGame

        viewModel.validateContest()

        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.ContestWithError
        assertEquals(R.string.game_register_error_start_contest_empty, state.messageId)
    }

    @Test
    fun `validateContest when userGame only has startContestNumber should set state ContestWithError with end contest empty message`() {
        val expectedState = GameRegisterState.ContestWithError(R.string.game_register_error_end_contest_empty)
        val userGame = UserGame().apply { startContestNumber = "1111" }

        viewModel.userGame.value = userGame

        viewModel.validateContest()

        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.ContestWithError
        assertEquals(R.string.game_register_error_end_contest_empty, state.messageId)
    }

    @Test
    fun `validateContest when validForAllFutureContests are true when endContestNumber is empty should set state to ContestOk`() {
        val expectedState = GameRegisterState.ContestOk
        val userGame = UserGame().apply { startContestNumber = "1111" }

        viewModel.userGame.value = userGame

        viewModel.validForAllFutureContests.value = true

        viewModel.validateContest()

        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `validateContest when userGame startContestNumber is bigger than endContestNumber should set state ContestWithError with blocking message`() {
        val expectedState = GameRegisterState.ContestWithError(R.string.game_register_error_start_bigger_than_end)
        val userGame = UserGame().apply {
            startContestNumber = "1112"
            endContestNumber = "1111"
        }

        viewModel.userGame.value = userGame

        viewModel.validateContest()

        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.ContestWithError
        assertEquals(R.string.game_register_error_start_bigger_than_end, state.messageId)
    }

    @Test
    fun `validateContest when userGame is ok`() {
        val expectedState = GameRegisterState.ContestOk
        val userGame = UserGame().apply {
            startContestNumber = "1111"
            endContestNumber = "1111"
        }

        viewModel.userGame.value = userGame

        viewModel.validateContest()

        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `shouldValidateEndContest should be false when validForAllFutureContest is true`() {
        viewModel.validForAllFutureContests.postValue(true)

        val result = viewModel.shouldValidateEndContest()

        assertFalse(result)
    }

    @Test
    fun `shouldValidateEndContest should be false when singleGame is true`() {
        viewModel.singleGame.postValue(true)

        val result = viewModel.shouldValidateEndContest()

        assertFalse(result)
    }

    @Test
    fun `shouldValidateEndContest should be true in natural state`() {
        val result = viewModel.shouldValidateEndContest()

        assertTrue(result)
    }

    @Test
    fun `clearNumbers should clear all user games numbers`() {
        with(viewModel.userGame.value!!) {
            numbers = mutableListOf(10, 40, 30)

            viewModel.clearNumbers()

            assertTrue(numbers.isEmpty())
        }
    }

    @Test
    fun `containsNumber should return true when the list contains the number searched`() {
        viewModel.userGame.value!!.apply {
            numbers = mutableListOf(10, 40, 30)
        }

        val result = viewModel.containsNumber(40)

        assertTrue(result)
    }

    @Test
    fun `containsNumber should return false when the list not contains the number searched`() {
        viewModel.userGame.value!!.apply {
            numbers = mutableListOf(10, 40, 30)
        }

        val result = viewModel.containsNumber(4)

        assertFalse(result)
    }

    @Test
    fun `getLotteryType should be returned correctly`() {
        val expectedLotteryType = LotteryType.QUINA

        viewModel.validateLotteryType(expectedLotteryType)

        val result = viewModel.getLotteryType()

        assertEquals(expectedLotteryType, result)
    }

    @Test
    fun `getNumbersCount should return the correct count of numbers`() {
        val expectedNumbers = mutableListOf(10, 40, 30)

        viewModel.userGame.value!!.apply {
            numbers = expectedNumbers
        }

        val result = viewModel.getNumbersCount()

        assertEquals(expectedNumbers.size, result)
    }

    @Test
    fun `onNumberPicked should add the number ten`() {
        val expectedSize = 1
        val expectedNumber = 10

        viewModel.onNumberPicked(expectedNumber)

        val result = viewModel.userGame.value!!.numbers

        assertEquals(expectedSize, result.size)
        assertEquals(expectedNumber, result[0])
    }

    @Test
    fun `onNumberPicked should remove the number ten`() {
        val numberToBeRemoved = 10
        val expectedNumbers = mutableListOf(numberToBeRemoved, 40, 30)

        viewModel.userGame.value!!.apply {
            numbers = expectedNumbers
        }

        assertEquals(3, expectedNumbers.size)

        assertEquals(numberToBeRemoved, expectedNumbers[0])
        assertEquals(40, expectedNumbers[1])
        assertEquals(30, expectedNumbers[2])

        viewModel.onNumberPicked(numberToBeRemoved)

        val result = viewModel.userGame.value!!.numbers

        assertEquals(2, result.size)

        assertEquals(40, result[0])
        assertEquals(30, result[1])
    }

    @Test
    fun `onNumberPicked should send a minimum numbers required`() {
        val expectedState = GameRegisterState.OnNumberPicked(
            messageId = R.string.game_register_select_minimum_numbers,
            messageColorId = R.color.colorWarning,
            isSaveButtonEnabled = false
        )

        viewModel.userGame.value!!.apply {
            type = LotteryType.LOTOFACIL
            numbers = mutableListOf()
        }

        viewModel.onNumberPicked(1)

        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `onNumberPicked should send a minimum numbers selected`() {
        val expectedState = GameRegisterState.OnNumberPicked(
            messageId = R.string.game_register_minimum_numbers_selected,
            messageColorId = R.color.colorSuccess,
            isSaveButtonEnabled = true
        )

        viewModel.userGame.value!!.apply {
            type = LotteryType.LOTOFACIL
            numbers = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
        }

        viewModel.onNumberPicked(15)

        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `onNumberPicked should send a maximum numbers selected`() {
        val expectedState = GameRegisterState.OnNumberPicked(
            messageId = R.string.game_register_maximum_numbers_selected,
            messageColorId = R.color.colorWarning,
            isSaveButtonEnabled = true
        )

        viewModel.userGame.value!!.apply {
            type = LotteryType.LOTOFACIL
            numbers = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        }

        viewModel.onNumberPicked(18)

        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `onNumberPicked should not exceed the maximum allowed numbers`() {
        val expectedNumbers = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

        viewModel.userGame.value!!.apply {
            type = LotteryType.MEGASENA
            numbers = expectedNumbers
        }

        assertEquals(15, expectedNumbers.size)

        assertEquals(1, expectedNumbers[0])
        assertEquals(2, expectedNumbers[1])
        assertEquals(3, expectedNumbers[2])
        assertEquals(4, expectedNumbers[3])
        assertEquals(5, expectedNumbers[4])
        assertEquals(6, expectedNumbers[5])
        assertEquals(7, expectedNumbers[6])
        assertEquals(8, expectedNumbers[7])
        assertEquals(9, expectedNumbers[8])
        assertEquals(10, expectedNumbers[9])
        assertEquals(11, expectedNumbers[10])
        assertEquals(12, expectedNumbers[11])
        assertEquals(13, expectedNumbers[12])
        assertEquals(14, expectedNumbers[13])
        assertEquals(15, expectedNumbers[14])

        viewModel.onNumberPicked(16)

        val result = viewModel.userGame.value!!.numbers

        assertEquals(15, expectedNumbers.size)

        assertEquals(1, result[0])
        assertEquals(2, result[1])
        assertEquals(3, result[2])
        assertEquals(4, result[3])
        assertEquals(5, result[4])
        assertEquals(6, result[5])
        assertEquals(7, result[6])
        assertEquals(8, result[7])
        assertEquals(9, result[8])
        assertEquals(10, result[9])
        assertEquals(11, result[10])
        assertEquals(12, result[11])
        assertEquals(13, result[12])
        assertEquals(14, result[13])
        assertEquals(15, result[14])
    }

    @Test
    fun `saveGame should be executed successfully`() {
        val state = GameRegisterState.GameUpdated

        every {
            preferencesRepository.saveGame(any())
        }.just(runs)

        viewModel.saveGame()

        verify(exactly = 1) { preferencesRepository.saveGame(any()) }
        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `saveGame should set state to NumbersWithError`() {
        val exception = Exception()
        val state = GameRegisterState.NumbersWithError()

        every {
            preferencesRepository.saveGame(any())
        }.throws(exception)

        viewModel.saveGame()

        verify(exactly = 1) { preferencesRepository.saveGame(any()) }
        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `saveGame should set the startNumberContest as endNumberContest when singleGame is true`() {
        val contestNumber = "1001"
        val expectedUserGame = UserGame(startContestNumber = contestNumber, endContestNumber = contestNumber)
        val userGame = UserGame(startContestNumber = contestNumber)

        every {
            preferencesRepository.saveGame(any())
        }.just(runs)

        viewModel.userGame.value = userGame
        viewModel.singleGame.postValue(true)
        viewModel.saveGame()

        assertEquals(expectedUserGame, userGame)
    }

    @Test
    fun `saveGame should sort the numbers`() {
        val expectedUserGame = UserGame(numbers = mutableListOf(1, 2, 3, 4, 5))
        val userGame = UserGame(numbers = mutableListOf(5, 4, 3, 2, 1))

        every {
            preferencesRepository.saveGame(any())
        }.just(runs)

        viewModel.userGame.value = userGame
        viewModel.saveGame()

        verify(exactly = 1) { preferencesRepository.saveGame(expectedUserGame) }
    }
}
