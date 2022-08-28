package com.tiagoalmeida.lottery.util

import com.tiagoalmeida.lottery.data.model.RangedLottery

object ContestMath {
    fun calculateLastContestNumber(contestNumber: Int, difference: Int): RangedLottery {
        val end = contestNumber - 1
        return RangedLottery(
            startContestNumber = if (difference > 10) {
                end - 9
            } else {
                contestNumber - difference
            },
            endContestNumber = end
        )
    }
}