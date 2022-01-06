package com.tiagoalmeida.lottery.data.model

import com.tiagoalmeida.lottery.data.model.LotteryAward
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LotteryResultTest {

    // region variables

    private val megasena = "MEGA-SENA"
    private val lotofacil = "LOTOFÁCIL"
    private val quina = "QUINA"
    private val lotomania = "LOTOMANIA"

    private val expectedContestNumber = "1101"
    private val expectedContestDate = 1000L
    private val expectedNumbersDrawn = mutableListOf("01", "02", "03", "04", "05", "06")
    private val expectedAwardOne = mockk<LotteryAward>()
    private val expectedAwardTwo = mockk<LotteryAward>()
    private val expectedNextContestDate = 1001L
    private val expectedNextContestPrize = 100.00

    private val megasenaLotteryResult = LotteryResult(
        name = megasena,
        contestNumber = expectedContestNumber,
        contestDate = expectedContestDate,
        numbersDrawn = expectedNumbersDrawn,
        awards = mutableListOf(expectedAwardOne, expectedAwardTwo),
        nextContestDate = expectedNextContestDate,
        nextContestPrize = expectedNextContestPrize
    )

    private val lotofacilLotteryResult = LotteryResult(
        name = lotofacil,
        contestNumber = "",
        contestDate = 0,
        numbersDrawn = mutableListOf(),
        awards = mutableListOf(),
        nextContestDate = 0,
        nextContestPrize = 0.0
    )

    private val quinaLotteryResult = LotteryResult(
        name = quina,
        contestNumber = "",
        contestDate = 0,
        numbersDrawn = mutableListOf(),
        awards = mutableListOf(),
        nextContestDate = 0,
        nextContestPrize = 0.0
    )

    private val lotomaniaLotteryResult = LotteryResult(
        name = lotomania,
        contestNumber = "",
        contestDate = 0,
        numbersDrawn = mutableListOf(),
        awards = mutableListOf(),
        nextContestDate = 0,
        nextContestPrize = 0.0
    )

    // endregion

    // region method: awards

    @Test
    fun `awards should return two objects`() {
        // When
        val results = megasenaLotteryResult.awards

        // Then
        assertEquals(2, results.size)

        assertEquals(expectedAwardOne, results[0])
        assertEquals(expectedAwardTwo, results[1])
    }

    // endregion

    // region method: contestDate

    @Test
    fun `contestDate should be a thousand`() {
        // When
        val result = megasenaLotteryResult.contestDate

        // Then
        assertEquals(expectedContestDate, result)
    }

    // endregion

    // region method: contestNumber

    @Test
    fun `contestNumber should be a thousand and a hundred and one`() {
        // When
        val result = megasenaLotteryResult.contestNumber

        // Then
        assertEquals(expectedContestNumber, result)
    }

    // endregion

    // region method: getLotteryType

    @Test
    fun `getLotteryType should be megasena`() {
        // When
        val result = megasenaLotteryResult.getLotteryType()

        // Then
        assertEquals(LotteryType.MEGASENA, result)
    }

    @Test
    fun `getLotteryType should be lotofacil`() {
        // When
        val result = lotofacilLotteryResult.getLotteryType()

        // Then
        assertEquals(LotteryType.LOTOFACIL, result)
    }

    @Test
    fun `getLotteryType should be quina`() {
        // When
        val result = quinaLotteryResult.getLotteryType()

        // Then
        assertEquals(LotteryType.QUINA, result)
    }

    @Test
    fun `getLotteryType should be lotomania`() {
        // When
        val result = lotomaniaLotteryResult.getLotteryType()

        // Then
        assertEquals(LotteryType.LOTOMANIA, result)
    }

    // endregion

    // region method: nextContestDate

    @Test
    fun `name should be MEGA-SENA`() {
        // When
        val result = megasenaLotteryResult.name

        // Then
        assertEquals(megasena, result)
    }

    // endregion

    // region method: nextContestDate

    @Test
    fun `nextContestDate should be a thousand and one`() {
        // When
        val result = megasenaLotteryResult.nextContestDate

        // Then
        assertEquals(expectedNextContestDate, result)
    }

    // endregion

    // region method: nextContestPrize

    @Test
    fun `nextContestPrize should be a hundred`() {
        // When
        val result = megasenaLotteryResult.nextContestPrize

        // Then
        assertEquals(expectedNextContestPrize, result, 0.0)
    }

    // endregion

    // region method: nextContestPrize

    @Test
    fun `numbersDrawn should be a list of six number`() {
        // When
        val result = megasenaLotteryResult.numbersDrawn

        // Then
        assertEquals(expectedNumbersDrawn.size, result.size)

        assertEquals(expectedNumbersDrawn, result)
    }

    // endregion

}