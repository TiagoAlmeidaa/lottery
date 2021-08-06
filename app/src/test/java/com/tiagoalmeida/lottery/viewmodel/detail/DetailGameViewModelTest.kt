package com.tiagoalmeida.lottery.viewmodel.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.network.repository.ConsultRepository
import com.tiagoalmeida.lottery.util.enums.LotteryType
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DetailGameViewModelTest {

    // region variables

    private val dispatcher = TestCoroutineDispatcher()

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK
    lateinit var consultRepository: ConsultRepository

    @MockK(relaxed = true)
    lateinit var observerState: Observer<DetailGameState>

    private lateinit var viewModel: DetailGameViewModel

    private val userGame = UserGame(
        startContestNumber = "",
        endContestNumber = "",
        type = LotteryType.QUINA,
        numbers = mutableListOf(1, 2, 3, 4, 5)
    )

    // endregion

    // region method: setup

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        viewModel = DetailGameViewModel(
            crashlytics,
            userGame,
            consultRepository
        )

        viewModel.viewState.observeForever(observerState)
    }

    @After
    fun finish() {
        viewModel.viewState.removeObserver(observerState)

        Dispatchers.resetMain()

        dispatcher.cleanupTestCoroutines()
    }

    // endregion

    // region method: consultContestsForGame

    @Test
    fun `consultContestsForGame when the last contestNumber is the same of UserGame startContestNumber it should add only the last LotteryResult`() {
        // Given
        userGame.startContestNumber = "1000"

        val lastLotteryResult = mockk<LotteryResult>()

        every {
            lastLotteryResult.contestNumber
        }.returns(userGame.startContestNumber)

        coEvery {
            consultRepository.consultLatestContest(userGame.type)
        }.returns(lastLotteryResult)

        // When
        viewModel.consultContestsForGame()

        // Then
        val state = viewModel.viewState.value as DetailGameState.ContestsReceived

        assertEquals(1, state.results.size)
        assertEquals(lastLotteryResult, state.results[0])

        coVerify(exactly = 1) { consultRepository.consultLatestContest(userGame.type) }
    }

    @Test
    fun `consultContestsForGame when an exception is thrown it should set an empty list`() {
        // Given
        val exception = Exception()

        coEvery {
            consultRepository.consultLatestContest(userGame.type)
        }.throws(exception)

        // When
        viewModel.consultContestsForGame()

        // Then
        val state = viewModel.viewState.value as DetailGameState.ContestsReceived

        assertEquals(0, state.results.size)

        verify(exactly = 1) { crashlytics.recordException(any()) }
    }

    // endregion

    // region method: consultContest

    @Test
    fun `consultContest should find LotteryResult when the contestNumber is the same`() {
        // Given
        val contestNumber = 1000
        val lotteryResult = mockk<LotteryResult>()

        val expectedState = DetailGameState.ContestFiltered(lotteryResult)

        every {
            lotteryResult.contestNumber
        }.returns("1000")

        coEvery {
            consultRepository.consultContest(userGame.type, contestNumber)
        }.returns(lotteryResult)

        // When
        viewModel.consultContest(contestNumber)

        // Then
        val state = viewModel.viewState.value as DetailGameState.ContestFiltered

        assertEquals(lotteryResult, state.lotteryResult)

        coVerify(exactly = 1) { consultRepository.consultContest(userGame.type, contestNumber) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `consultContest should not find LotteryResult when the contestNumber is different`() {
        // Given
        val contestNumber = 1000
        val lotteryResult = mockk<LotteryResult>()

        val expectedState = DetailGameState.ContestNotFound(contestNumber.toString())

        every {
            lotteryResult.contestNumber
        }.returns("1001")

        coEvery {
            consultRepository.consultContest(userGame.type, contestNumber)
        }.returns(lotteryResult)

        // When
        viewModel.consultContest(contestNumber)

        // Then
        val state = viewModel.viewState.value as DetailGameState.ContestNotFound

        assertEquals(contestNumber, state.contestNumber.toInt())

        coVerify(exactly = 1) { consultRepository.consultContest(userGame.type, contestNumber) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `consultContest should not find the LotteryResult when an exception is thrown`() {
        // Given
        val contestNumber = 1000
        val exception = mockk<Exception>()
        val exceptionMessage = "fakeMessage"

        every {
            exception.message
        }.returns(exceptionMessage)

        val expectedState = DetailGameState.ContestNotFound(contestNumber.toString())

        coEvery {
            consultRepository.consultContest(userGame.type, contestNumber)
        }.throws(exception)

        // When
        viewModel.consultContest(contestNumber)

        // Then
        val state = viewModel.viewState.value as DetailGameState.ContestNotFound

        assertEquals(contestNumber, state.contestNumber.toInt())

        verify(exactly = 1) { crashlytics.recordException(any()) }
        coVerify(exactly = 1) { consultRepository.consultContest(userGame.type, contestNumber) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    // endregion

    // region method: calculateContestRange

    @Test
    fun `calculateContestRange should return the range between 1008 and 1009 and also true for including the last game`() {
        // Given
        val start = 1000
        val end = 1010
        val last = 1010

        // When
        val triple = viewModel.calculateContestRange(start, end, last)

        // Then
        assertEquals(triple.first, 1008)
        assertEquals(triple.second, 1009)

        assertTrue(triple.third)
    }

    @Test
    fun `calculateContestRange should return the range between 1008 and 1010 and also false for including the last game`() {
        // Given
        val start = 1000
        val end = 1010
        val last = 1015

        // When
        val triple = viewModel.calculateContestRange(start, end, last)

        // Then
        assertEquals(triple.first, 1008)
        assertEquals(triple.second, 1010)

        assertFalse(triple.third)
    }

    // endregion

    // region method: getContestNumber

    @Test
    fun `getContestNumber should return a string with the startContestNumber and the infinity symbol`() {
        // Given
        userGame.startContestNumber = "1000"

        val expectedString = "1000 - ∞"

        // When
        val result = viewModel.getContestNumber()

        // Then
        assertEquals(expectedString, result)
    }

    @Test
    fun `getContestNumber should return a string with the startContestNumber and endContestNumber`() {
        // Given
        userGame.startContestNumber = "1000"
        userGame.endContestNumber = "1001"

        val expectedString = "1000 - 1001"

        // When
        val result = viewModel.getContestNumber()

        // Then
        assertEquals(expectedString, result)
    }

    // endregion

    // region method: getColorId

    @Test
    fun `getColorId should return the correct color id`() {
        // When
        val result = viewModel.getColorId()

        // Then
        assertEquals(LotteryType.QUINA.color, result)
    }

    // endregion

    // region method: getNumbers

    @Test
    fun `getNumbers should return the correct numbers`() {
        // Given
        val numbers = mutableListOf(1, 2, 3, 4, 5)

        userGame.numbers = numbers

        // When
        val result = viewModel.getNumbers()

        // Then
        assertEquals(numbers, result)

        assertEquals(5, result.size)
    }

    // endregion

    // region method: getUserGame

    @Test
    fun `getUserGame should return the correct UserGame`() {
        // When
        val result = viewModel.getUserGame()

        // Then
        assertEquals(userGame, result)
    }

    // endregion

}