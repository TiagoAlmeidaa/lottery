package com.tiagoalmeida.lottery.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface LotteryApiService {

    @GET("resultado?")
    suspend fun consultContest(
        @Query("token") token: String,
        @Query("loteria") contest: String
    ): Response<String>

    @GET("resultado?")
    suspend fun consultContestByNumber(
        @Query("token") token: String,
        @Query("loteria") contest: String,
        @Query("concurso") contestNumber: String
    ): Response<String>

}
