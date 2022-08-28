package com.tiagoalmeida.lottery.ui.games

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.ui.games.GamesFilter
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.ui.games.GamesState
import com.tiagoalmeida.lottery.ui.games.GamesViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
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
class GamesViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK
    lateinit var preferencesRepository: PreferencesRepository

    @MockK(relaxed = true)
    lateinit var observerState: Observer<GamesState>

    @MockK(relaxed = true)
    lateinit var observerFilter: Observer<GamesFilter>

    private lateinit var viewModel: GamesViewModel

    private val megasena = UserGame(
        startContestNumber = "1000",
        endContestNumber = "1000",
        type = LotteryType.MEGASENA,
        numbers = mutableListOf(1, 2, 3, 4, 5, 6)
    )

    private val quina = UserGame(
        startContestNumber = "1500",
        endContestNumber = "1500",
        type = LotteryType.QUINA,
        numbers = mutableListOf(1, 2, 3, 4, 5)
    )

    private val lotofacil = UserGame(
        startContestNumber = "2000",
        endContestNumber = "2000",
        type = LotteryType.LOTOFACIL,
        numbers = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        viewModel = GamesViewModel(
            crashlytics,
            preferencesRepository
        )

        with(viewModel) {
            viewState.observeForever(observerState)
            gamesFilter.observeForever(observerFilter)
        }
    }

    @After
    fun finish() {
        with(viewModel) {
            viewState.removeObserver(observerState)
            gamesFilter.removeObserver(observerFilter)
        }

        Dispatchers.resetMain()

        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `findGames with LotteryType filter should find the correct user game`() {
        val fakeGames = mutableListOf(megasena, lotofacil, quina)

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        viewModel.setFilter(
            GamesFilter(lotteryType = megasena.type)
        )

        viewModel.findGames()

        val state = viewModel.viewState.value as GamesState.GamesReceived

        with(state) {
            assertEquals(1, games.size)

            assertEquals(megasena.startContestNumber, games[0].startContestNumber)
            assertEquals(megasena.type, games[0].type)
        }

        verify(exactly = 1) { preferencesRepository.getGames() }
    }

    @Test
    fun `findGames with ContestNumber filter should find the correct user game`() {
        val fakeGames = mutableListOf(megasena, lotofacil, quina)

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        viewModel.setFilter(
            GamesFilter(contestNumber = lotofacil.startContestNumber)
        )

        viewModel.findGames()

        val state = viewModel.viewState.value as GamesState.GamesReceived

        with(state) {
            assertEquals(1, games.size)

            assertEquals(lotofacil.startContestNumber, games[0].startContestNumber)
            assertEquals(lotofacil.type, games[0].type)
        }

        verify(exactly = 1) { preferencesRepository.getGames() }
    }

    @Test
    fun `findGames should return all games when hideOldNumbers filter is active but the lastContestNumber is ZERO`() {
        val validForFutureContestsGame = UserGame(
            startContestNumber = "1500",
            endContestNumber = ""
        )
        val oldSingleGame = UserGame(
            startContestNumber = "1000",
            endContestNumber = "1000",
        )
        val rangedContestGame = UserGame(
            startContestNumber = "1510",
            endContestNumber = "1525"
        )
        val fakeGames = listOf(
            validForFutureContestsGame,
            oldSingleGame,
            rangedContestGame
        )

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        every {
            preferencesRepository.getLastSavedContestNumber(any())
        }.returns(0)

        viewModel.setFilter(GamesFilter(hideOldNumbers = true))

        viewModel.findGames()

        verify(exactly = 1) { preferencesRepository.getGames() }
        verify(exactly = 3) { preferencesRepository.getLastSavedContestNumber(LotteryType.MEGASENA) }
        val orderedExpectedList = listOf(rangedContestGame, validForFutureContestsGame, oldSingleGame)
        val expectedState = GamesState.GamesReceived(orderedExpectedList)
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `findGames should return validForFutureContestGame and rangedContestGame when hideOldNumbers filter is active and lastContestNumber is 1510`() {
        val validForFutureContestsGame = UserGame(
            startContestNumber = "1500",
            endContestNumber = ""
        )
        val oldSingleGame = UserGame(
            startContestNumber = "1000",
            endContestNumber = "1000",
        )
        val rangedContestGame = UserGame(
            startContestNumber = "1510",
            endContestNumber = "1525"
        )
        val fakeGames = listOf(
            validForFutureContestsGame,
            oldSingleGame,
            rangedContestGame
        )

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        every {
            preferencesRepository.getLastSavedContestNumber(any())
        }.returns(1510)

        viewModel.setFilter(GamesFilter(hideOldNumbers = true))

        viewModel.findGames()

        verify(exactly = 1) { preferencesRepository.getGames() }
        verify(exactly = 3) { preferencesRepository.getLastSavedContestNumber(LotteryType.MEGASENA) }
        val orderedExpectedList = listOf(rangedContestGame, validForFutureContestsGame)
        val expectedState = GamesState.GamesReceived(orderedExpectedList)
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `findGames should return validForFutureContestGame and rangedContestGame when hideOldNumbers filter is active and lastContestNumber is 1525`() {
        val validForFutureContestsGame = UserGame(
            startContestNumber = "1500",
            endContestNumber = ""
        )
        val oldSingleGame = UserGame(
            startContestNumber = "1000",
            endContestNumber = "1000",
        )
        val rangedContestGame = UserGame(
            startContestNumber = "1510",
            endContestNumber = "1525"
        )
        val fakeGames = listOf(
            validForFutureContestsGame,
            oldSingleGame,
            rangedContestGame
        )

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        every {
            preferencesRepository.getLastSavedContestNumber(any())
        }.returns(1525)

        viewModel.setFilter(GamesFilter(hideOldNumbers = true))

        viewModel.findGames()

        verify(exactly = 1) { preferencesRepository.getGames() }
        verify(exactly = 3) { preferencesRepository.getLastSavedContestNumber(LotteryType.MEGASENA) }
        val orderedExpectedList = listOf(rangedContestGame, validForFutureContestsGame)
        val expectedState = GamesState.GamesReceived(orderedExpectedList)
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `findGames should return only validForFutureContestGame when hideOldNumbers filter is active and lastContestNumber is 1526`() {
        val validForFutureContestsGame = UserGame(
            startContestNumber = "1500",
            endContestNumber = ""
        )
        val oldSingleGame = UserGame(
            startContestNumber = "1000",
            endContestNumber = "1000",
        )
        val rangedContestGame = UserGame(
            startContestNumber = "1510",
            endContestNumber = "1525"
        )
        val fakeGames = listOf(
            validForFutureContestsGame,
            oldSingleGame,
            rangedContestGame
        )

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        every {
            preferencesRepository.getLastSavedContestNumber(any())
        }.returns(1526)

        viewModel.setFilter(GamesFilter(hideOldNumbers = true))

        viewModel.findGames()

        verify(exactly = 1) { preferencesRepository.getGames() }
        verify(exactly = 3) { preferencesRepository.getLastSavedContestNumber(LotteryType.MEGASENA) }
        val orderedExpectedList = listOf(validForFutureContestsGame)
        val expectedState = GamesState.GamesReceived(orderedExpectedList)
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `findGames with no filter should set state with the full list ordered by startContestNumber`() {
        val fakeGames = mutableListOf(megasena, lotofacil, quina)

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        viewModel.findGames()

        val state = viewModel.viewState.value as GamesState.GamesReceived

        with(state) {
            assertEquals(3, games.size)

            assertEquals(lotofacil.startContestNumber, games[0].startContestNumber)
            assertEquals(lotofacil.type, games[0].type)

            assertEquals(quina.startContestNumber, games[1].startContestNumber)
            assertEquals(quina.type, games[1].type)

            assertEquals(megasena.startContestNumber, games[2].startContestNumber)
            assertEquals(megasena.type, games[2].type)
        }

        verify(exactly = 1) { preferencesRepository.getGames() }
    }

    @Test
    fun `findGames with full filters should find the correct user game`() {
        val fakeGames = mutableListOf(megasena, lotofacil, quina)

        every {
            preferencesRepository.getGames()
        }.returns(fakeGames)

        every {
            preferencesRepository.getLastSavedContestNumber(LotteryType.MEGASENA)
        }.returns(megasena.getStartContestInt() -1)

        viewModel.setFilter(
            GamesFilter(
                lotteryType = megasena.type,
                contestNumber = megasena.startContestNumber,
                hideOldNumbers = true
            )
        )

        viewModel.findGames()

        val state = viewModel.viewState.value as GamesState.GamesReceived

        with(state) {
            assertEquals(1, games.size)

            assertEquals(megasena.startContestNumber, games[0].startContestNumber)
            assertEquals(megasena.type, games[0].type)
        }

        verify(exactly = 1) { preferencesRepository.getGames() }
        verify(exactly = 1) { preferencesRepository.getLastSavedContestNumber(LotteryType.MEGASENA) }
    }

    @Test
    fun `findGames when preferencesRepository throws an Exception should register in crashlytics`() {
        val exception = mockk<Exception>()

        every {
            preferencesRepository.getGames()
        }.throws(exception)

        viewModel.findGames()

        verify(exactly = 1) { crashlytics.recordException(any()) }
    }

    @Test
    fun `removeGame when user is null should remove user game from preferences`() {
        val userGame = mockk<UserGame>()

        val expectedState = GamesState.GameRemoved

        coEvery {
            preferencesRepository.removeGame(userGame)
        }.just(runs)

        viewModel.removeGame(userGame)

        coVerify(exactly = 1) { preferencesRepository.removeGame(userGame) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `removeGame should set InternalError state when an Exception is thrown`() {
        val userGame = mockk<UserGame>()
        val exception = mockk<Exception>()
        val exceptionMessage = "fakeMessage"

        every {
            exception.message
        }.returns(exceptionMessage)

        val expectedState = GamesState.InternalError

        coEvery {
            preferencesRepository.removeGame(userGame)
        }.throws(exception)

        viewModel.removeGame(userGame)

        verify(exactly = 1) { crashlytics.recordException(any()) }
        coVerify(exactly = 1) { preferencesRepository.removeGame(userGame) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `removeGame should set Timeout state when an TimeoutCancellationException is thrown`() {
        val exception = mockk<TimeoutCancellationException>(relaxed = true)
        val userGame = mockk<UserGame>()

        val expectedState = GamesState.Timeout

        coEvery {
            preferencesRepository.removeGame(userGame)
        }.throws(exception)

        viewModel.removeGame(userGame)

        verify(exactly = 1) { crashlytics.recordException(any()) }
        verify(exactly = 1) { observerState.onChanged(expectedState) }
    }

    @Test
    fun `setFilter should set filter correctly`() {
        val filter = mockk<GamesFilter>()

        viewModel.setFilter(filter)

        verify(exactly = 1) { observerFilter.onChanged(filter) }
    }

    @Test
    fun `getFilter should set filter correctly`() {
        val expectedFilter = mockk<GamesFilter>()

        viewModel.setFilter(expectedFilter)

        val result = viewModel.getFilter()

        assertEquals(expectedFilter, result)
    }

    @Test
    fun `hasGamesInPreferences should return true when games is not empty`() {
        val games = mutableListOf(megasena)

        coEvery {
            preferencesRepository.getGames()
        }.returns(games)

        val result = viewModel.hasGamesInPreferences()

        coVerify(exactly = 1) { preferencesRepository.getGames() }

        assertTrue(result)
    }

    @Test
    fun `hasGamesInPreferences should return false when games is empty`() {
        val games = mutableListOf<UserGame>()

        coEvery {
            preferencesRepository.getGames()
        }.returns(games)

        val result = viewModel.hasGamesInPreferences()

        coVerify(exactly = 1) { preferencesRepository.getGames() }

        assertFalse(result)
    }

    @Test
    fun `deleteAllGamesInPreferences should be executed successfully`() {
        coEvery {
            preferencesRepository.deleteAllGames()
        }.just(runs)

        viewModel.deleteAllGamesInPreferences()

        coVerify(exactly = 1) { preferencesRepository.deleteAllGames() }
    }
}