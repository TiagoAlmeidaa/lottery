package com.tiagoalmeida.lottery.viewmodel.register.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.network.repository.PreferencesRepository
import com.tiagoalmeida.lottery.util.enums.LotteryType
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameRegisterViewModelTest {

    // region variables

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK
    lateinit var preferencesRepository: PreferencesRepository

    @MockK(relaxed = true)
    lateinit var observer: Observer<GameRegisterState>

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: GameRegisterViewModel

    // endregion

    // region method: setup

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

    // endregion

    // region method: validateLotteryType

    @Test
    fun `validateLotteryType should set state to LotteryTypeOk`() {
        // Given
        val lotteryType = LotteryType.MEGASENA
        val state = GameRegisterState.LotteryTypeOk

        // When
        viewModel.validateLotteryType(lotteryType)

        // Then
        verify(exactly = 1) { observer.onChanged(state) }

        val userGame = viewModel.userGame.value!!
        assertEquals(userGame.type, lotteryType)
    }

    @Test
    fun `validateLotteryType should set state to LotteryTypeInvalid`() {
        // Given
        val state = GameRegisterState.LotteryTypeInvalid

        // When
        viewModel.validateLotteryType(null)

        // Then
        verify(exactly = 1) { observer.onChanged(state) }
    }

    // endregion

    // region method: validateContest

    @Test
    fun `validateContest when userGame is null should set state ContestWithError`() {
        // Given
        val expectedState = GameRegisterState.ContestWithError()

        viewModel.userGame.value = null

        // When
        viewModel.validateContest()

        // Then
        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `validateContest when userGame only has endContestNumber should set state ContestWithError with start contest empty message`() {
        // Given
        val expectedState = GameRegisterState.ContestWithError(R.string.game_register_error_start_contest_empty)
        val userGame = UserGame().apply { endContestNumber = "1111" }

        viewModel.userGame.value = userGame

        // When
        viewModel.validateContest()

        // Then
        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.ContestWithError
        assertEquals(R.string.game_register_error_start_contest_empty, state.messageId)
    }

    @Test
    fun `validateContest when userGame only has startContestNumber should set state ContestWithError with end contest empty message`() {
        // Given
        val expectedState = GameRegisterState.ContestWithError(R.string.game_register_error_end_contest_empty)
        val userGame = UserGame().apply { startContestNumber = "1111" }

        viewModel.userGame.value = userGame

        // When
        viewModel.validateContest()

        // Then
        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.ContestWithError
        assertEquals(R.string.game_register_error_end_contest_empty, state.messageId)
    }

    @Test
    fun `validateContest when validForAllFutureContests are true when endContestNumber is empty should set state to ContestOk`() {
        // Given
        val expectedState = GameRegisterState.ContestOk
        val userGame = UserGame().apply { startContestNumber = "1111" }

        viewModel.userGame.value = userGame

        viewModel.validForAllFutureContests.value = true

        // When
        viewModel.validateContest()

        // Then
        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    @Test
    fun `validateContest when userGame startContestNumber is bigger than endContestNumber should set state ContestWithError with blocking message`() {
        // Given
        val expectedState = GameRegisterState.ContestWithError(R.string.game_register_error_start_bigger_than_end)
        val userGame = UserGame().apply {
            startContestNumber = "1112"
            endContestNumber = "1111"
        }

        viewModel.userGame.value = userGame

        // When
        viewModel.validateContest()

        // Then
        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.ContestWithError
        assertEquals(R.string.game_register_error_start_bigger_than_end, state.messageId)
    }

    @Test
    fun `validateContest when userGame is ok`() {
        // Given
        val expectedState = GameRegisterState.ContestOk
        val userGame = UserGame().apply {
            startContestNumber = "1111"
            endContestNumber = "1111"
        }

        viewModel.userGame.value = userGame

        // When
        viewModel.validateContest()

        // Then
        verify(exactly = 1) { observer.onChanged(expectedState) }
    }

    // endregion

    // region method: shouldValidateEndContest

    @Test
    fun `shouldValidateEndContest should be false when validForAllFutureContest is true`() {
        // Given
        viewModel.validForAllFutureContests.postValue(true)

        // When
        val result = viewModel.shouldValidateEndContest()

        //
        assertFalse(result)
    }

    @Test
    fun `shouldValidateEndContest should be false when singleGame is true`() {
        // Given
        viewModel.singleGame.postValue(true)

        // When
        val result = viewModel.shouldValidateEndContest()

        //
        assertFalse(result)
    }

    @Test
    fun `shouldValidateEndContest should be true in natural state`() {
        // When
        val result = viewModel.shouldValidateEndContest()

        //
        assertTrue(result)
    }

    // endregion

    // region method: clearNumbers

    @Test
    fun `clearNumbers should clear all user games numbers`() {
        with(viewModel.userGame.value!!) {
            // Given
            numbers = mutableListOf(10, 40, 30)

            // When
            viewModel.clearNumbers()

            // Then
            assertTrue(numbers.isEmpty())
        }
    }

    // endregion

    // region method: containsNumber

    @Test
    fun `containsNumber should return true when the list contains the number searched`() {
        // Given
        viewModel.userGame.value!!.apply {
            numbers = mutableListOf(10, 40, 30)
        }

        // When
        val result = viewModel.containsNumber(40)

        // Then
        assertTrue(result)
    }

    @Test
    fun `containsNumber should return false when the list not contains the number searched`() {
        // Given
        viewModel.userGame.value!!.apply {
            numbers = mutableListOf(10, 40, 30)
        }

        // When
        val result = viewModel.containsNumber(4)

        // Then
        assertFalse(result)
    }

    // endregion

    // region method: getLotteryType

    @Test
    fun `getLotteryType should be returned correctly`() {
        // Given
        val expectedLotteryType = LotteryType.QUINA

        viewModel.validateLotteryType(expectedLotteryType)

        // When
        val result = viewModel.getLotteryType()

        // Then
        assertEquals(expectedLotteryType, result)
    }

    // endregion

    // region method: getNumbersCount

    @Test
    fun `getNumbersCount should return the correct count of numbers`() {
        // Given
        val expectedNumbers = mutableListOf(10, 40, 30)

        viewModel.userGame.value!!.apply {
            numbers = expectedNumbers
        }

        // When
        val result = viewModel.getNumbersCount()

        // Then
        assertEquals(expectedNumbers.size, result)
    }

    // endregion

    // region method: onNumberPicked

    @Test
    fun `onNumberPicked should add the number ten`() {
        // Given
        val expectedSize = 1
        val expectedNumber = 10

        // When
        viewModel.onNumberPicked(expectedNumber)

        // Then
        val result = viewModel.userGame.value!!.numbers

        assertEquals(expectedSize, result.size)
        assertEquals(expectedNumber, result[0])
    }

    @Test
    fun `onNumberPicked should remove the number ten`() {
        // Given
        val numberToBeRemoved = 10
        val expectedNumbers = mutableListOf(numberToBeRemoved, 40, 30)

        viewModel.userGame.value!!.apply {
            numbers = expectedNumbers
        }

        assertEquals(3, expectedNumbers.size)

        assertEquals(numberToBeRemoved, expectedNumbers[0])
        assertEquals(40, expectedNumbers[1])
        assertEquals(30, expectedNumbers[2])

        // When
        viewModel.onNumberPicked(numberToBeRemoved)

        // Then
        val result = viewModel.userGame.value!!.numbers

        assertEquals(2, result.size)

        assertEquals(40, result[0])
        assertEquals(30, result[1])
    }

    @Test
    fun `onNumberPicked should not add number in full numbers list and should set state NumbersWithError with message`() {
        // Given
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

        // When
        viewModel.onNumberPicked(16)

        // Then
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

    // endregion

    // region method: validateNumbers

    @Test
    fun `validateNumbers should set state to ProceedToSaveNumbers`() {
        // Given
        val state = GameRegisterState.ProceedToSaveNumbers(viewModel.userGame.value!!)

        viewModel.userGame.value!!.apply {
            type = LotteryType.MEGASENA
            numbers = mutableListOf(1, 2, 3, 4, 5, 6)
        }

        // When
        viewModel.validateNumbers()

        // Then
        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `validateNumbers should set the state to NumbersWithError`() {
        // Given
        val expectedNumbers = mutableListOf(1, 2, 3, 4)
        val expectedState = GameRegisterState.NumbersWithError(R.string.game_register_error_minimum)

        viewModel.userGame.value!!.apply {
            type = LotteryType.MEGASENA
            numbers = expectedNumbers
        }

        // When
        viewModel.validateNumbers()

        // Then
        verify(exactly = 1) { observer.onChanged(expectedState) }

        val state = viewModel.viewState.value as GameRegisterState.NumbersWithError
        assertEquals(R.string.game_register_error_minimum, state.messageId)
    }

    // endregion

    // region method: saveNumbers

    @Test
    fun `saveNumbers should be executed successfully`() {
        // Given
        val state = GameRegisterState.GameUpdated
        val userGame = mockk<UserGame>()

        every {
            preferencesRepository.saveGame(userGame)
        }.just(runs)

        // When
        viewModel.saveNumbers(userGame)

        // Then
        verify(exactly = 1) { preferencesRepository.saveGame(userGame) }
        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `saveNumbers should set state to NumbersWithError`() {
        // Given
        val exception = Exception()
        val state = GameRegisterState.NumbersWithError()
        val userGame = mockk<UserGame>()

        every {
            preferencesRepository.saveGame(userGame)
        }.throws(exception)

        // When
        viewModel.saveNumbers(userGame)

        // Then
        verify(exactly = 1) { preferencesRepository.saveGame(userGame) }
        verify(exactly = 1) { observer.onChanged(state) }
    }

    @Test
    fun `saveNumbers should set the startNumberContest as endNumberContest when singleGame is true`() {
        // Given
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

        // When
        viewModel.saveNumbers(userGame)

        // Then
        verify(exactly = 1) { preferencesRepository.saveGame(userGame) }
        verify(exactly = 1) { observer.onChanged(state) }

        assertEquals(expectedUserGame, userGame)
    }

    // endregion

}
