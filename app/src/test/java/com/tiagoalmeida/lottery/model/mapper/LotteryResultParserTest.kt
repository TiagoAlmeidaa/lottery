package com.tiagoalmeida.lottery.model.mapper

import com.google.gson.Gson
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.util.enums.LotteryType
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class LotteryResultParserTest {

    // region variables

    private lateinit var parser: LotteryResultParser

    private val lotteryResult = LotteryResult(
        name = LotteryType.MEGASENA.name,
        contestNumber = "5000",
        contestDate = 0,
        numbersDrawn = listOf(),
        awards = listOf(),
        nextContestDate = 0,
        nextContestPrize = 0.0
    )

    // endregion

    // region method: setup

    @Before
    fun setup() {
        parser = LotteryResultParser()
    }

    // endregion

    // region method: from

    @Test
    fun `from should return null when response is not successful`() {
        // Given
        val mockResponse = mockk<Response<String>>()

        every {
            mockResponse.isSuccessful
        }.returns(false)

        // When
        val result = parser.from(mockResponse)

        // Then
        assertNull(result)
    }

    @Test
    fun `from should return null when response json is incorrect`() {
        // Given
        val json = "123"
        val mockResponse = mockk<Response<String>>()

        every {
            mockResponse.isSuccessful
        }.returns(true)

        every {
            mockResponse.body()
        }.returns(json)

        // When
        val result = parser.from(mockResponse)

        // Then
        assertNull(result)
    }

    @Test
    fun `from should return the correct object`() {
        // Given
        val json = Gson().toJson(lotteryResult)
        val mockResponse = mockk<Response<String>>()

        every {
            mockResponse.isSuccessful
        }.returns(true)

        every {
            mockResponse.body()
        }.returns(json)

        // When
        val result = parser.from(mockResponse)

        // Then
        assertNotNull(result)

        assertEquals(lotteryResult, result)
    }

    // endregion

}