package com.tiagoalmeida.lottery.data.model

import com.google.gson.annotations.SerializedName
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.ui.detail.BaseDetailGame

data class LotteryResult(
    @SerializedName("nome") val name: String,
    @SerializedName("numero_concurso") val contestNumber: String,
    @SerializedName("data_concurso_milliseconds") val contestDate: Long,
    @SerializedName("dezenas") val numbersDrawn: List<String>,
    @SerializedName("premiacao") val awards: List<LotteryAward>,
    @SerializedName("data_proximo_concurso_milliseconds") val nextContestDate: Long,
    @SerializedName("valor_estimado_proximo_concurso") val nextContestPrize: Double
) : BaseDetailGame {

    companion object {
        const val LAYOUT_ID = R.layout.adapter_detail_game
    }

    override fun getLayoutId(): Int = LAYOUT_ID

    fun getLotteryType(): LotteryType {
        return when (name) {
            "MEGA-SENA" -> LotteryType.MEGASENA
            "LOTOFÁCIL" -> LotteryType.LOTOFACIL
            "QUINA" -> LotteryType.QUINA
            else -> LotteryType.LOTOMANIA
        }
    }
}
