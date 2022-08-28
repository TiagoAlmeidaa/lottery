package com.tiagoalmeida.lottery.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LotteryAwardTest {

    private val expectedName = "name"
    private val expectedWinnersCount = "1"
    private val expectedValue = "100"
    private val expectedHits = 1

    private val award = LotteryAward(
        name = expectedName,
        winnersCount = expectedWinnersCount,
        value = expectedValue,
        hits = expectedHits
    )

    @Test
    fun `name should return the correct name`() {
        val result = award.name

        assertEquals(expectedName, result)
    }

    @Test
    fun `winnersCount should return the correct count`() {
        val result = award.winnersCount

        assertEquals(expectedWinnersCount, result)
    }

    @Test
    fun `value should return the correct total value`() {
        val result = award.value

        assertEquals(expectedValue, result)
    }

    @Test
    fun `hits should return the correct hits`() {
        val result = award.hits

        assertEquals(expectedHits, result)
    }
}