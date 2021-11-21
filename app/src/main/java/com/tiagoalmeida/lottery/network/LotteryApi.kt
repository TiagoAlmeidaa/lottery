package com.tiagoalmeida.lottery.network

import org.koin.core.component.KoinComponent
import retrofit2.Retrofit

internal class LotteryApi(private val retrofit: Retrofit) : KoinComponent {
    fun getService(): LotteryApiService = retrofit.create(LotteryApiService::class.java)
}
