package com.tiagoalmeida.lottery.network.datasource

import com.tiagoalmeida.lottery.network.AppRetrofit
import com.tiagoalmeida.lottery.util.Keys
import com.tiagoalmeida.lottery.util.enums.LotteryType

class ConsultDataSource(
    private val retrofit: AppRetrofit,
    private val token: String = Keys.apiKey()
) {

    suspend fun consultContest(type: LotteryType) =
        retrofit
            .getService()
            .consultContest(token, type.url)

    suspend fun consultContestByNumber(type: LotteryType, contestNumber: Int) =
        retrofit
            .getService()
            .consultContestByNumber(token, type.url, contestNumber.toString())

}
