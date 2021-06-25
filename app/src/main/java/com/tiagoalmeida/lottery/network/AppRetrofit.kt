package com.tiagoalmeida.lottery.network

import org.koin.core.component.KoinComponent
import retrofit2.Retrofit

class AppRetrofit(private val retrofit: Retrofit) : KoinComponent {
    fun getService(): AppRetrofitService = retrofit.create(AppRetrofitService::class.java)
}
