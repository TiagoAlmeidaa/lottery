package com.tiagoalmeida.lottery.network.datasource

import com.tiagoalmeida.lottery.network.AppRetrofit
import com.tiagoalmeida.lottery.util.Keys
import com.tiagoalmeida.lottery.util.enums.LotteryType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class ConsultDataSourceImpl(
    private val retrofit: AppRetrofit,
    private val token: String = Keys.apiKey(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ConsultDataSource {

    override suspend fun consultContest(type: LotteryType): Response<String> =
        withContext(dispatcher) {
            retrofit
                .getService()
                .consultContest(token, type.url)
        }

    override suspend fun consultContestByNumber(type: LotteryType, contestNumber: Int): Response<String> =
        withContext(dispatcher) {
            retrofit
                .getService()
                .consultContestByNumber(token, type.url, contestNumber.toString())
        }

}
