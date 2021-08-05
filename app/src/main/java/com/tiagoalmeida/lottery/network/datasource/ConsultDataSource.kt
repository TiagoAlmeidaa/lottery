package com.tiagoalmeida.lottery.network.datasource

import com.tiagoalmeida.lottery.util.enums.LotteryType
import retrofit2.Response

interface ConsultDataSource {
    suspend fun consultContest(type: LotteryType): Response<String>
    suspend fun consultContestByNumber(type: LotteryType, contestNumber: Int): Response<String>
}