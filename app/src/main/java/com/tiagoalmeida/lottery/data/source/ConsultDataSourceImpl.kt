package com.tiagoalmeida.lottery.data.source

import com.tiagoalmeida.lottery.data.LotteryApi
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.util.Keys
import com.tiagoalmeida.lottery.data.model.LotteryType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class ConsultDataSourceImpl(
    private val retrofit: LotteryApi,
    private val token: String = Keys.apiKey(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ConsultDataSource {

    override suspend fun consultContest(type: LotteryType): LotteryResult =
        withContext(dispatcher) {
            retrofit
                .getService()
                .consultContest(token, type.url)
        }

    override suspend fun consultContestByNumber(type: LotteryType, contestNumber: Int): LotteryResult =
        withContext(dispatcher) {
            retrofit
                .getService()
                .consultContestByNumber(token, type.url, contestNumber.toString())
        }

}
