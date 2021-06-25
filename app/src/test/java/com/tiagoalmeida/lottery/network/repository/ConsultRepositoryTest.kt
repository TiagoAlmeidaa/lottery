package com.tiagoalmeida.lottery.network.repository

import com.tiagoalmeida.lottery.model.mapper.LotteryResultParser
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.network.datasource.ConsultDataSource
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
class ConsultRepositoryTest {

    // region variables

    private val dispatcher = TestCoroutineDispatcher()

    @MockK
    lateinit var dataSource: ConsultDataSource

    @MockK
    lateinit var parser: LotteryResultParser

    private lateinit var repository: ConsultRepository

    // endregion

    // region method: setup

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(dispatcher)

        repository = ConsultRepository(parser, dataSource)
    }

    // endregion

    // region method: consultAll

    @Test
    fun `consultAll should return a list with one of each lottery`() = runBlocking {
        // Given
        val responseMegasena = mockk<Response<String>>()
        val responseLotofacil = mockk<Response<String>>()
        val responseLotomania = mockk<Response<String>>()
        val responseQuina = mockk<Response<String>>()

        val expectedMegasena = mockk<LotteryResult>()
        val expectedLotofacil = mockk<LotteryResult>()
        val expectedLotomania = mockk<LotteryResult>()
        val expectedQuina = mockk<LotteryResult>()

        coEvery {
            dataSource.consultContest(LotteryType.MEGASENA)
        }.returns(responseMegasena)

        coEvery {
            dataSource.consultContest(LotteryType.LOTOFACIL)
        }.returns(responseLotofacil)

        coEvery {
            dataSource.consultContest(LotteryType.LOTOMANIA)
        }.returns(responseLotomania)

        coEvery {
            dataSource.consultContest(LotteryType.QUINA)
        }.returns(responseQuina)

        every {
            expectedMegasena.getLotteryType()
        }.returns(LotteryType.MEGASENA)

        every {
            expectedLotofacil.getLotteryType()
        }.returns(LotteryType.LOTOFACIL)

        every {
            expectedLotomania.getLotteryType()
        }.returns(LotteryType.LOTOMANIA)

        every {
            expectedQuina.getLotteryType()
        }.returns(LotteryType.QUINA)

        every {
            parser.from(responseMegasena)
        }.returns(expectedMegasena)

        every {
            parser.from(responseLotofacil)
        }.returns(expectedLotofacil)

        every {
            parser.from(responseLotomania)
        }.returns(expectedLotomania)

        every {
            parser.from(responseQuina)
        }.returns(expectedQuina)

        // When
        val results = repository.consultAll()

        // Then
        assertEquals(4, results.size)

        assertEquals(expectedMegasena, results[0])
        assertEquals(LotteryType.MEGASENA, results[0].getLotteryType())

        assertEquals(expectedLotofacil, results[1])
        assertEquals(LotteryType.LOTOFACIL, results[1].getLotteryType())

        assertEquals(expectedLotomania, results[2])
        assertEquals(LotteryType.LOTOMANIA, results[2].getLotteryType())

        assertEquals(expectedQuina, results[3])
        assertEquals(LotteryType.QUINA, results[3].getLotteryType())
    }

    // endregion

    // region method: consultLatestContest

    @Test
    fun `consultLatestContest should be executed successfully`() = runBlocking {
        // Given
        val lotteryType = LotteryType.MEGASENA
        val expectedResult = mockk<Response<String>>()
        val expectedLottery = mockk<LotteryResult>()

        coEvery {
            dataSource.consultContest(lotteryType)
        }.returns(expectedResult)

        every {
            parser.from(expectedResult)
        }.returns(expectedLottery)

        // When
        val result = repository.consultLatestContest(lotteryType)

        // Then
        assertEquals(expectedLottery, result)
    }

    // endregion

    // region method: consultContest

    @Test
    fun `consultContest should be executed successfully`() = runBlocking {
        // Given
        val contest = 1234
        val lotteryType = LotteryType.MEGASENA
        val expectedResult = mockk<Response<String>>()
        val expectedLottery = mockk<LotteryResult>()

        coEvery {
            dataSource.consultContestByNumber(lotteryType, contest)
        }.returns(expectedResult)

        every {
            parser.from(expectedResult)
        }.returns(expectedLottery)

        // When
        val result = repository.consultContest(lotteryType, contest)

        // Then
        assertEquals(expectedLottery, result)
    }

    @Test
    fun `consultContests should be executed successfully`() = runBlocking {
        // Given
        val startContest = 1
        val endContest = 3
        val lotteryType = LotteryType.MEGASENA

        val expectedResponseFirst = mockk<Response<String>>()
        val expectedResponseSecond = mockk<Response<String>>()
        val expectedResponseThird = mockk<Response<String>>()

        val expectedLotteryFirst = mockk<LotteryResult>()
        val expectedLotterySecond = mockk<LotteryResult>()
        val expectedLotteryThird = mockk<LotteryResult>()

        every {
            expectedLotteryFirst.contestNumber
        }.returns("1")

        every {
            expectedLotterySecond.contestNumber
        }.returns("2")

        every {
            expectedLotteryThird.contestNumber
        }.returns("3")

        every {
            parser.from(expectedResponseFirst)
        }.returns(expectedLotteryFirst)

        every {
            parser.from(expectedResponseSecond)
        }.returns(expectedLotterySecond)

        every {
            parser.from(expectedResponseThird)
        }.returns(expectedLotteryThird)

        coEvery {
            dataSource.consultContestByNumber(lotteryType, 1)
        }.returns(expectedResponseFirst)

        coEvery {
            dataSource.consultContestByNumber(lotteryType, 2)
        }.returns(expectedResponseSecond)

        coEvery {
            dataSource.consultContestByNumber(lotteryType, 3)
        }.returns(expectedResponseThird)


        // When
        val result = repository.consultContests(lotteryType, startContest, endContest)

        // Then
        assertEquals(3, result.size)

        assertEquals(result[0], expectedLotteryThird)
        assertEquals(result[0].contestNumber, "3")

        assertEquals(result[1], expectedLotterySecond)
        assertEquals(result[1].contestNumber, "2")
        
        assertEquals(result[2], expectedLotteryFirst)
        assertEquals(result[2].contestNumber, "1")
    }

    // endregion

}
