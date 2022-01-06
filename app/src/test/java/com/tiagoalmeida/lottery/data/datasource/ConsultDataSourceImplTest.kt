package com.tiagoalmeida.lottery.data.datasource

import com.tiagoalmeida.lottery.data.LotteryApi
import com.tiagoalmeida.lottery.data.LotteryApiService
import com.tiagoalmeida.lottery.data.source.ConsultDataSourceImpl
import com.tiagoalmeida.lottery.data.model.LotteryType
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ConsultDataSourceImplTest {

    // region variables

    private val dispatcher = TestCoroutineDispatcher()

    @MockK
    internal lateinit var retrofit: LotteryApi

    @MockK
    internal lateinit var service: LotteryApiService

    private lateinit var fakeToken: String

    private lateinit var dataSource: ConsultDataSourceImpl

    // endregion

    // region method: setup

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every {
            retrofit.getService()
        }.returns(service)

        fakeToken = "fakeToken"

        dataSource = ConsultDataSourceImpl(retrofit, fakeToken, dispatcher)
    }

    @After
    fun finish() {
        Dispatchers.resetMain()

        dispatcher.cleanupTestCoroutines()
    }

    // endregion

    // region method: consultContest

    @Test
    fun `consultContest should be executed successfully`() = runBlockingTest {
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
    fun `consultContestByNumber should be executed successfully`() = runBlockingTest {
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
