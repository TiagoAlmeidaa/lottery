package com.tiagoalmeida.lottery.model.mapper

import com.google.gson.Gson
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import retrofit2.Response

class LotteryResultParser {

    fun from(result: Response<String>): LotteryResult? {
        return if (result.isSuccessful) {
            val json = result.body()
            return try {
                Gson().fromJson(json, LotteryResult::class.java)
            } catch (exception: Exception) {
                null
            }
        } else {
            null
        }
    }
}
