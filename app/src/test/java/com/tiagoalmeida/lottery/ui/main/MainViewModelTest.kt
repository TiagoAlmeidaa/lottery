package com.tiagoalmeida.lottery.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.domain.ConsultLatestResultsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val dispatcher = UnconfinedTestDispatcher()

    @MockK(relaxed = true)
    lateinit var crashlytics: FirebaseCrashlytics

    @MockK
    lateinit var preferencesRepository: PreferencesRepository

    @MockK
    lateinit var consultLatestResultsUseCase: ConsultLatestResultsUseCase

    @MockK(relaxed = true)
    lateinit var observerResults: Observer<List<LotteryResult>>

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        viewModel = MainViewModel(
            crashlytics,
            preferencesRepository,
            consultLatestResultsUseCase
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
    }

    @Test
    fun `consultLastResults should be executed successfully`() {
        val games = listOf<LotteryResult>()

        coEvery {
            consultLatestResultsUseCase()
        }.returns(games)

        viewModel.consultLastResults()

        verify(exactly = 1) { observerResults.onChanged(games) }
    }

    @Test
    fun `consultLastResults when exception is thrown should register in crashlytics`() {
        val exception = Exception()

        coEvery {
            consultLatestResultsUseCase()
        }.throws(exception)

        viewModel.consultLastResults()

        verify(exactly = 1) { crashlytics.recordException(any()) }
        verify(exactly = 1) { observerResults.onChanged(listOf()) }
    }

    @Test
    fun `updateLastGamesContestNumbers should be executed correctly`() {
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

        viewModel.updateLastGamesContestNumbers(list)

        verify(exactly = 1) { preferencesRepository.saveLastContestNumber(LotteryType.MEGASENA, 1) }
        verify(exactly = 1) { preferencesRepository.saveLastContestNumber(LotteryType.LOTOFACIL, 2) }
    }

    @Test
    fun `updateLastGamesContestNumbers should not be executed`() {
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

        viewModel.updateLastGamesContestNumbers(list)

        verify(exactly = 0) { preferencesRepository.saveLastContestNumber(LotteryType.MEGASENA, 1) }
        verify(exactly = 0) { preferencesRepository.saveLastContestNumber(LotteryType.LOTOFACIL, 2) }
    }
}
