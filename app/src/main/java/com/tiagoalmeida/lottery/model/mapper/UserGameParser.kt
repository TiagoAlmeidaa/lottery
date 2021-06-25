package com.tiagoalmeida.lottery.model.mapper

import com.google.gson.Gson
import com.tiagoalmeida.lottery.model.vo.UserGame

object UserGameParser {

    fun from(json: String): UserGame {
        return Gson().fromJson(json, UserGame::class.java)
    }
}
