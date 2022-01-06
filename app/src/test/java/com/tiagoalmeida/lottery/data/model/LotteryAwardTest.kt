package com.tiagoalmeida.lottery.data.model

import com.tiagoalmeida.lottery.data.model.LotteryAward
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LotteryAwardTest {

    // region variables

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

    // endregion

    // region method: name

    @Test
    fun `name should return the correct name`() {
        // Given
        val result = award.name

        // When
        assertEquals(expectedName, result)
    }

    // endregion

    // region method: winnersCount

    @Test
    fun `winnersCount should return the correct count`() {
        // Given
        val result = award.winnersCount

        // When
        assertEquals(expectedWinnersCount, result)
    }

    // endregion

    // region method: value

    @Test
    fun `value should return the correct total value`() {
        // Given
        val result = award.value

        // When
        assertEquals(expectedValue, result)
    }

    // endregion

    // region method: hits

    @Test
    fun `hits should return the correct hits`() {
        // Given
        val result = award.hits

        // When
        assertEquals(expectedHits, result)
    }

    // endregion

}