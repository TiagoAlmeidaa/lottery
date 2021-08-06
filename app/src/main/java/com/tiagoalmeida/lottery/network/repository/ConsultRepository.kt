package com.tiagoalmeida.lottery.network.repository

import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.util.enums.LotteryType

interface ConsultRepository {
    suspend fun consultAll(): List<LotteryResult>
    suspend fun consultLatestContest(type: LotteryType): LotteryResult?
    suspend fun consultContest(type: LotteryType, contestNumber: Int): LotteryResult?
    suspend fun consultContests(type: LotteryType, startContest: Int, endContest: Int): List<LotteryResult>
}