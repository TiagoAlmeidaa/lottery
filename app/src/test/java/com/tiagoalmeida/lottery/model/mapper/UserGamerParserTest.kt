package com.tiagoalmeida.lottery.model.mapper

import com.google.gson.Gson
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.util.enums.LotteryType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserGamerParserTest {

    // variables

    private val userGame = UserGame(
        startContestNumber = "2222",
        endContestNumber = "2222",
        type = LotteryType.MEGASENA,
        numbers = mutableListOf(1, 2, 3, 4, 5, 6)
    )

    // endregion

    // region method: from

    @Test
    fun `from should convert the json to an object`() {
        // Given
        val json = Gson().toJson(userGame)

        // When
        val result = UserGameParser.from(json)

        // Then
        assertNotNull(result)

        assertEquals(userGame, result)
    }

    // endregion
}