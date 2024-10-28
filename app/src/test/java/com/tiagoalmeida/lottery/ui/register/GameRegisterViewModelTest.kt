package com.tiagoalmeida.lottery.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
    fun `onNumberPicked should not add number in full numbers list and should set state NumbersWithError with message`() {
        val expectedState = GameRegisterState.NumbersWithError(R.string.game_register_error_maximum)
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

        val state = viewModel.viewState.value as GameRegisterState.NumbersWithError
        assertEquals(R.string.game_register_error_maximum, state.messageId)

        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `validateNumbers should set state to ProceedToSaveNumbers`() {
        val state = GameRegisterState.ProceedToSaveNumbers(viewModel.userGame.value!!)

        viewModel.userGame.value!!.apply {
            type = LotteryType.MEGASENA
            numbers = mutableListOf(1, 2, 3, 4, 5, 6)
        }

        viewModel.validateNumbers()

        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `validateNumbers should set the state to NumbersWithError`() {
        val expectedNumbers = mutableListOf(1, 2, 3, 4)
        val expectedState = GameRegisterState.NumbersWithError(R.string.game_register_error_minimum)

        viewModel.userGame.value!!.apply {
            type = LotteryType.MEGASENA
            numbers = expectedNumbers
        }

        viewModel.validateNumbers()

        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.NumbersWithError
        assertEquals(R.string.game_register_error_minimum, state.messageId)
    }

    @Test
    fun `saveNumbers should be executed successfully`() {
        val state = GameRegisterState.GameUpdated
        val userGame = mockk<UserGame>()

        every {
            preferencesRepository.saveGame(userGame)
        }.just(runs)

        viewModel.saveNumbers(userGame)

        verify(exactly = 1) { preferencesRepository.saveGame(userGame) }
        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `saveNumbers should set state to NumbersWithError`() {
        val exception = Exception()
        val state = GameRegisterState.NumbersWithError()
        val userGame = mockk<UserGame>()

        every {
            preferencesRepository.saveGame(userGame)
        }.throws(exception)

        viewModel.saveNumbers(userGame)

        verify(exactly = 1) { preferencesRepository.saveGame(userGame) }
        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `saveNumbers should set the startNumberContest as endNumberContest when singleGame is true`() {
        val contestNumber = "1001"
        val expectedUserGame = UserGame(
            contestNumber,
            contestNumber
        )

        val state = GameRegisterState.GameUpdated
        val userGame = UserGame(
            startContestNumber = contestNumber
        )

        every {
            preferencesRepository.saveGame(userGame)
        }.just(runs)

        viewModel.singleGame.postValue(true)

        viewModel.saveNumbers(userGame)

        verify(exactly = 1) { preferencesRepository.saveGame(userGame) }
        verify(exactly = 1) { observer.onChanged(state) }

        assertEquals(expectedUserGame, userGame)
    }
}
