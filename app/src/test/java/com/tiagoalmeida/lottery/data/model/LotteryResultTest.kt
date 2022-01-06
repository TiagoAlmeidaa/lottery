package com.tiagoalmeida.lottery.data.model

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LotteryResultTest {

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

    @Test
    fun `awards should return two objects`() {
        val results = megasenaLotteryResult.awards

        assertEquals(2, results.size)

        assertEquals(expectedAwardOne, results[0])
        assertEquals(expectedAwardTwo, results[1])
    }

    @Test
    fun `contestDate should be a thousand`() {
        val result = megasenaLotteryResult.contestDate

        assertEquals(expectedContestDate, result)
    }

    @Test
    fun `contestNumber should be a thousand and a hundred and one`() {
        val result = megasenaLotteryResult.contestNumber

        assertEquals(expectedContestNumber, result)
    }

    @Test
    fun `getLotteryType should be megasena`() {
        val result = megasenaLotteryResult.getLotteryType()

        assertEquals(LotteryType.MEGASENA, result)
    }

    @Test
    fun `getLotteryType should be lotofacil`() {
        val result = lotofacilLotteryResult.getLotteryType()

        assertEquals(LotteryType.LOTOFACIL, result)
    }

    @Test
    fun `getLotteryType should be quina`() {
        val result = quinaLotteryResult.getLotteryType()

        assertEquals(LotteryType.QUINA, result)
    }

    @Test
    fun `getLotteryType should be lotomania`() {
        val result = lotomaniaLotteryResult.getLotteryType()

        assertEquals(LotteryType.LOTOMANIA, result)
    }

    @Test
    fun `name should be MEGA-SENA`() {
        val result = megasenaLotteryResult.name

        assertEquals(megasena, result)
    }

    @Test
    fun `nextContestDate should be a thousand and one`() {
        val result = megasenaLotteryResult.nextContestDate

        assertEquals(expectedNextContestDate, result)
    }

    @Test
    fun `nextContestPrize should be a hundred`() {
        val result = megasenaLotteryResult.nextContestPrize

        assertEquals(expectedNextContestPrize, result, 0.0)
    }

    @Test
    fun `numbersDrawn should be a list of six number`() {
        val result = megasenaLotteryResult.numbersDrawn

        assertEquals(expectedNumbersDrawn.size, result.size)

        assertEquals(expectedNumbersDrawn, result)
    }
}