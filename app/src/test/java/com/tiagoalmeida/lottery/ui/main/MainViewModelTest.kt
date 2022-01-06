package com.tiagoalmeida.lottery.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.repository.ConsultRepository
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.ui.main.MainViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {

    // region variables

    private val dispatcher = TestCoroutineDispatcher()

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK
    lateinit var consultRepository: ConsultRepository

    @MockK
    lateinit var preferencesRepository: PreferencesRepository

    @MockK(relaxed = true)
    lateinit var observerResults: Observer<List<LotteryResult>>

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    // endregion

    // region method: setup

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        viewModel = MainViewModel(
            crashlytics,
            consultRepository,
            preferencesRepository
        )

        with(viewModel) {
            results.observeForever(observerResults)
        }
    }

    @After
    fun finish() {
        with(viewModel) {
            results.removeObserver(observerResults)
        }

        Dispatchers.resetMain()

        dispatcher.cleanupTestCoroutines()
    }

    // endregion

    // region method: consultLastResults

    @Test
    fun `consultLastResults should be executed successfully`() {
        // Given
        val games = listOf<LotteryResult>()

        coEvery {
            consultRepository.consultAll()
        }.returns(games)

        // When
        viewModel.consultLastResults()

        // Then
        verify(exactly = 1) { observerResults.onChanged(games) }
    }

    @Test
    fun `consultLastResults when exception is thrown should register in crashlytics`() {
        // Given
        val exception = Exception()

        coEvery {
            consultRepository.consultAll()
        }.throws(exception)

        // When
        viewModel.consultLastResults()

        // Then
        verify(exactly = 1) { crashlytics.recordException(any()) }
        verify(exactly = 1) { observerResults.onChanged(listOf()) }
    }

    // endregion

    // region method: updateLastGamesContestNumbers

    @Test
    fun `updateLastGamesContestNumbers should be executed correctly`() {
        // Given
        val megasenaResult = mockk<LotteryResult>()
        val lotofacilResult = mockk<LotteryResult>()

        every {
            preferencesRepository.getLastSavedContestNumber(any())
        }.returns(0)

        every {
            megasenaResult.getLotteryType()
        }.returns(LotteryType.MEGASENA)

        every {
            megasenaResult.contestNumber
        }.returns("1")

        every {
            lotofacilResult.getLotteryType()
        }.returns(LotteryType.LOTOFACIL)

        every {
            lotofacilResult.contestNumber
        }.returns("2")

        every {
            preferencesRepository.saveLastContestNumber(any(), any())
        }.just(runs)

        val list = listOf(megasenaResult, lotofacilResult)

        // When
        viewModel.updateLastGamesContestNumbers(list)

        // Then
        verify(exactly = 1) { preferencesRepository.saveLastContestNumber(LotteryType.MEGASENA, 1) }
        verify(exactly = 1) { preferencesRepository.saveLastContestNumber(LotteryType.LOTOFACIL, 2) }
    }

    @Test
    fun `updateLastGamesContestNumbers should not be executed`() {
        // Given
        val megasenaResult = mockk<LotteryResult>()
        val lotofacilResult = mockk<LotteryResult>()

        every {
            preferencesRepository.getLastSavedContestNumber(any())
        }.returns(1000)

        every {
            megasenaResult.getLotteryType()
        }.returns(LotteryType.MEGASENA)

        every {
            megasenaResult.contestNumber
        }.returns("1")

        every {
            lotofacilResult.getLotteryType()
        }.returns(LotteryType.LOTOFACIL)

        every {
            lotofacilResult.contestNumber
        }.returns("2")

        every {
            preferencesRepository.saveLastContestNumber(any(), any())
        }.just(runs)

        val list = listOf(megasenaResult, lotofacilResult)

        // When
        viewModel.updateLastGamesContestNumbers(list)

        // Then
        verify(exactly = 0) { preferencesRepository.saveLastContestNumber(LotteryType.MEGASENA, 1) }
        verify(exactly = 0) { preferencesRepository.saveLastContestNumber(LotteryType.LOTOFACIL, 2) }
    }

    // endregion

}
