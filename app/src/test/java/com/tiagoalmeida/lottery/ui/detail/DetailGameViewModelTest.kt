package com.tiagoalmeida.lottery.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.data.repository.ConsultRepository
import com.tiagoalmeida.lottery.domain.ConsultRangedResultsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DetailGameViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK
    lateinit var consultRepository: ConsultRepository

    @MockK
    lateinit var consultRangedResultsUseCase: ConsultRangedResultsUseCase

    @MockK(relaxed = true)
    lateinit var observerState: Observer<DetailGameState>

    private lateinit var viewModel: DetailGameViewModel

    private val userGame = UserGame(
        startContestNumber = "",
        endContestNumber = "",
        type = LotteryType.QUINA,
        numbers = mutableListOf(1, 2, 3, 4, 5)
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        viewModel = DetailGameViewModel(
            crashlytics,
            userGame,
            consultRepository,
            consultRangedResultsUseCase
        )

        viewModel.viewState.observeForever(observerState)
    }

    @After
    fun finish() {
        viewModel.viewState.removeObserver(observerState)
    }

    @Test
    fun `consultContestsForGame when the game is valid for future contest it should call consultLatestContest`() {
        userGame.startContestNumber = "1000"
        userGame.endContestNumber = ""

        val lotteryResult = mockk<LotteryResult>()

        every {
            lotteryResult.contestNumber
        }.returns(userGame.startContestNumber)

        coEvery {
            consultRepository.consultLatestContest(userGame.type)
        }.returns(lotteryResult)

        viewModel.consultContestsForGame()

        val state = viewModel.viewState.value as DetailGameState.ContestsReceived

        assertEquals(1, state.results.size)
        assertEquals(lotteryResult, state.results[0])

        coVerify(exactly = 1) { consultRepository.consultLatestContest(userGame.type) }
    }

    @Test
    fun `consultContestsForGame when the game has same values for start and end it should call consultContest`() {
        userGame.startContestNumber = "1000"
        userGame.endContestNumber = "1000"

        val lotteryResult = mockk<LotteryResult>()

        every {
            lotteryResult.contestNumber
        }.returns(userGame.startContestNumber)

        coEvery {
            consultRepository.consultContestByNumber(userGame.type, 1000)
        }.returns(lotteryResult)

        viewModel.consultContestsForGame()

        val state = viewModel.viewState.value as DetailGameState.ContestsReceived

        assertEquals(1, state.results.size)
        assertEquals(lotteryResult, state.results[0])

        coVerify(exactly = 1) { consultRepository.consultContestByNumber(userGame.type, 1000) }
    }

    @Test
    fun `consultContestsForGame when the game start is different from end but end is not zero it should call consultContest`() {
        userGame.startContestNumber = "1000"
        userGame.endContestNumber = "1001"

        val firstLotteryResult = mockk<LotteryResult>()
        val secondLotteryResult = mockk<LotteryResult>()

        every {
            firstLotteryResult.contestNumber
        }.returns("1001")

        every {
            secondLotteryResult.contestNumber
        }.returns("1000")

        coEvery {
            consultRepository.consultContestByNumber(userGame.type, 1001)
        }.returns(firstLotteryResult)

        coEvery {
            consultRangedResultsUseCase(userGame.type, 1000, 1000)
        }.returns(listOf(secondLotteryResult))

        viewModel.consultContestsForGame()

        val state = viewModel.viewState.value as DetailGameState.ContestsReceived

        assertEquals(2, state.results.size)
        assertEquals(firstLotteryResult, state.results[0])
        assertEquals(secondLotteryResult, state.results[1])

        coVerify(exactly = 1) { consultRepository.consultContestByNumber(userGame.type, 1001) }
        coVerify(exactly = 1) { consultRangedResultsUseCase(userGame.type, 1000, 1000) }
    }

    @Test
    fun `consultContestsForGame when an exception is thrown it should set an empty list`() {
        val exception = Exception()

        coEvery {
            consultRepository.consultLatestContest(userGame.type)
        }.throws(exception)

        viewModel.consultContestsForGame()

        val state = viewModel.viewState.value as DetailGameState.ContestsReceived

        assertEquals(0, state.results.size)

        verify(exactly = 1) { crashlytics.recordException(any()) }
    }

    @Test
    fun `consultContest should find LotteryResult when the contestNumber is the same`() {
        val contestNumber = 1000
        val lotteryResult = mockk<LotteryResult>()

        val expectedState = DetailGameState.ContestFiltered(lotteryResult)

        every {
            lotteryResult.contestNumber
        }.returns("1000")

        coEvery {
            consultRepository.consultContestByNumber(userGame.type, contestNumber)
        }.returns(lotteryResult)

        viewModel.consultContest(contestNumber)

        val state = viewModel.viewState.value as DetailGameState.ContestFiltered

        assertEquals(lotteryResult, state.lotteryResult)

        coVerify(exactly = 1) { consultRepository.consultContestByNumber(userGame.type, contestNumber) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `consultContest should not find LotteryResult when the contestNumber is different`() {
        val contestNumber = 1000
        val lotteryResult = mockk<LotteryResult>()

        val expectedState = DetailGameState.ContestNotFound(contestNumber.toString())

        every {
            lotteryResult.contestNumber
        }.returns("1001")

        coEvery {
            consultRepository.consultContestByNumber(userGame.type, contestNumber)
        }.returns(lotteryResult)

        viewModel.consultContest(contestNumber)

        val state = viewModel.viewState.value as DetailGameState.ContestNotFound

        assertEquals(contestNumber, state.contestNumber.toInt())

        coVerify(exactly = 1) { consultRepository.consultContestByNumber(userGame.type, contestNumber) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `consultContest should not find the LotteryResult when an exception is thrown`() {
        val contestNumber = 1000
        val exception = mockk<Exception>()
        val exceptionMessage = "fakeMessage"

        every {
            exception.message
        }.returns(exceptionMessage)

        val expectedState = DetailGameState.ContestNotFound(contestNumber.toString())

        coEvery {
            consultRepository.consultContestByNumber(userGame.type, contestNumber)
        }.throws(exception)

        viewModel.consultContest(contestNumber)

        val state = viewModel.viewState.value as DetailGameState.ContestNotFound

        assertEquals(contestNumber, state.contestNumber.toInt())

        verify(exactly = 1) { crashlytics.recordException(any()) }
        coVerify(exactly = 1) { consultRepository.consultContestByNumber(userGame.type, contestNumber) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `calculateRange should return the range between 1008 and 1009 and also true for including the last game`() {
        val start = 1010
        val difference = 20

        val result = viewModel.calculateRange(start, difference)

        assertEquals(result.startContestNumber, 1000)
        assertEquals(result.endContestNumber, 1009)
    }

    @Test
    fun `calculateRange should return the range between 1008 and 1010 and also false for including the last game`() {
        // Given
        val start = 1010
        val difference = 5

        // When
        val result = viewModel.calculateRange(start, difference)

        // Then
        assertEquals(result.startContestNumber, 1005)
        assertEquals(result.endContestNumber, 1009)
    }

    @Test
    fun `getContestNumber should return a string with the startContestNumber and the infinity symbol`() {
        userGame.startContestNumber = "1000"

        val expectedString = "1000 - ∞"

        val result = viewModel.getContestNumber()

        assertEquals(expectedString, result)
    }

    @Test
    fun `getContestNumber should return a string with the startContestNumber and endContestNumber`() {
        userGame.startContestNumber = "1000"
        userGame.endContestNumber = "1001"

        val expectedString = "1000 - 1001"

        val result = viewModel.getContestNumber()

        assertEquals(expectedString, result)
    }

    @Test
    fun `getContestNumber should return a the startContestNumber when singleGame returns true`() {
        userGame.startContestNumber = "1000"
        userGame.endContestNumber = "1000"

        val expectedString = "1000"

        val result = viewModel.getContestNumber()

        assertEquals(expectedString, result)
    }

    @Test
    fun `getColorId should return the correct color id`() {
        val result = viewModel.getColorId()

        assertEquals(LotteryType.QUINA.primaryColor, result)
    }

    @Test
    fun `getNumbers should return the correct numbers`() {
        val numbers = mutableListOf(1, 2, 3, 4, 5)

        userGame.numbers = numbers

        val result = viewModel.getNumbers()

        assertEquals(numbers, result)

        assertEquals(5, result.size)
    }

    @Test
    fun `getUserGame should return the correct UserGame`() {
        val result = viewModel.getUserGame()

        assertEquals(userGame, result)
    }
}
