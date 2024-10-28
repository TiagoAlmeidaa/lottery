package com.tiagoalmeida.lottery.data.datasource

import com.tiagoalmeida.lottery.data.LotteryApi
import com.tiagoalmeida.lottery.data.LotteryApiService
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.source.ConsultDataSourceImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
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
class ConsultDataSourceImplTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @MockK
    internal lateinit var retrofit: LotteryApi

    @MockK
    internal lateinit var service: LotteryApiService

    private lateinit var fakeToken: String

    private lateinit var dataSource: ConsultDataSourceImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        every {
            retrofit.getService()
        }.returns(service)

        fakeToken = "fakeToken"

        dataSource = ConsultDataSourceImpl(retrofit, fakeToken, dispatcher)
    }

    @Test
    fun `consultContest should be executed successfully`() = runTest {
        val lotteryType = LotteryType.MEGASENA
        val expectedResult = mockk<LotteryResult>()

        coEvery {
            service.consultContest(fakeToken, lotteryType.url)
        }.returns(expectedResult)

        val result = dataSource.consultContest(lotteryType)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `consultContestByNumber should be executed successfully`() = runTest {
        val contestNumber = 1234
        val lotteryType = LotteryType.MEGASENA
        val expectedResult = mockk<LotteryResult>()

        coEvery {
            service.consultContestByNumber(fakeToken, lotteryType.url, contestNumber.toString())
        }.returns(expectedResult)

        val result = dataSource.consultContestByNumber(lotteryType, contestNumber)

        assertEquals(expectedResult, result)
    }
}
