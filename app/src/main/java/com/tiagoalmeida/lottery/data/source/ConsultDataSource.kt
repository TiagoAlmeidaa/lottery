package com.tiagoalmeida.lottery.data.source

import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType

interface ConsultDataSource {
    suspend fun consultContest(type: LotteryType): LotteryResult
    suspend fun consultContestByNumber(type: LotteryType, contestNumber: Int): LotteryResult
}