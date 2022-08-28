package com.tiagoalmeida.lottery.data.repository

import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType

interface ConsultRepository {
    suspend fun consultLatestContest(type: LotteryType): LotteryResult
    suspend fun consultContestByNumber(type: LotteryType, contestNumber: Int): LotteryResult
}