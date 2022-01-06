package com.tiagoalmeida.lottery.data.model

import com.google.gson.annotations.SerializedName

data class LotteryAward(
    @SerializedName("nome") val name: String,
    @SerializedName("quantidade_ganhadores") val winnersCount: String,
    @SerializedName("valor_total") val value: String,
    @SerializedName("acertos") val hits: Int
)
