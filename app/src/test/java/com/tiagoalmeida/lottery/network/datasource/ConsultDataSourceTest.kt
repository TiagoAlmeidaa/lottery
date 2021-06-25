package com.tiagoalmeida.lottery.network.datasource

import com.tiagoalmeida.lottery.network.AppRetrofit
import com.tiagoalmeida.lottery.network.AppRetrofitService
import com.tiagoalmeida.lottery.util.enums.LotteryType
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ConsultDataSourceTest {

    // region variables

    private val dispatcher = TestCoroutineDispatcher()

    @MockK
    lateinit var retrofit: AppRetrofit

    @MockK
    lateinit var service: AppRetrofitService

    private lateinit var fakeToken: String

    private lateinit var dataSource: ConsultDataSource

    // endregion

    // region method: setup

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        every {
            retrofit.getService()
        }.returns(service)

        fakeToken = "fakeToken"

        dataSource = ConsultDataSource(retrofit, fakeToken)
    }

    // endregion

    // region method: consultContest

    @Test
    fun `consultContest should be executed successfully`() = runBlocking {
        // Given
        val lotteryType = LotteryType.MEGASENA
        val expectedResult = mockk<Response<String>>()

        coEvery {
            service.consultContest(fakeToken, lotteryType.url)
        }.returns(expectedResult)

        // When
        val result = dataSource.consultContest(lotteryType)

        // Then
        assertEquals(expectedResult, result)
    }

    // endregion

    // region method: consultContestByNumber

    @Test
    fun `consultContestByNumber should be executed successfully`() = runBlocking {
        // Given
        val contestNumber = 1234
        val lotteryType = LotteryType.MEGASENA
        val expectedResult = mockk<Response<String>>()

        coEvery {
            service.consultContestByNumber(fakeToken, lotteryType.url, contestNumber.toString())
        }.returns(expectedResult)

        // When
        val result = dataSource.consultContestByNumber(lotteryType, contestNumber)

        // Then
        assertEquals(expectedResult, result)
    }

    // endregion

}
