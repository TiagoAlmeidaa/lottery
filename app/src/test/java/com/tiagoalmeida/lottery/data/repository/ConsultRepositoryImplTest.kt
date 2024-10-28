package com.tiagoalmeida.lottery.data.repository

import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.source.ConsultDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ConsultRepositoryImplTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @MockK
    lateinit var dataSource: ConsultDataSource

    private lateinit var repository: ConsultRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        repository = ConsultRepositoryImpl(dataSource)
    }

    @Test
    fun `consultLatestContest should be executed successfully`() = runTest {
        val lotteryType = LotteryType.MEGASENA
        val expectedLottery = mockk<LotteryResult>()

        coEvery {
            dataSource.consultContest(lotteryType)
        }.returns(expectedLottery)

        val result = repository.consultLatestContest(lotteryType)

        assertEquals(expectedLottery, result)
    }

    @Test
    fun `consultContestByNumber should be executed successfully`() = runTest {
        val contest = 1234
        val lotteryType = LotteryType.MEGASENA
        val expectedLottery = mockk<LotteryResult>()

        coEvery {
            dataSource.consultContestByNumber(lotteryType, contest)
        }.returns(expectedLottery)

        val result = repository.consultContestByNumber(lotteryType, contest)

        assertEquals(expectedLottery, result)
    }

}
