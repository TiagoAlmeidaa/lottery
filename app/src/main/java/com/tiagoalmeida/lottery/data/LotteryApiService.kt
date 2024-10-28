package com.tiagoalmeida.lottery.data

import com.tiagoalmeida.lottery.data.model.LotteryResult
import retrofit2.http.GET
import retrofit2.http.Query

internal interface LotteryApiService {

    @GET("resultado?")
    suspend fun consultContest(
        @Query("token") token: String,
        @Query("loteria") contest: String
    ): LotteryResult

    @GET("resultado?")
    suspend fun consultContestByNumber(
        @Query("token") token: String,
        @Query("loteria") contest: String,
        @Query("concurso") contestNumber: String
    ): LotteryResult

}
