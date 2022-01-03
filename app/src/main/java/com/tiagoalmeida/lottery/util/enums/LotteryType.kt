package com.tiagoalmeida.lottery.util.enums

import com.tiagoalmeida.lottery.R

enum class LotteryType(
    val lotteryName: String,
    val url: String,
    val total: Int,
    val maximum: String,
    val minimum: String,
    val primaryColor: Int,
    val secondaryColor: Int
) {
    MEGASENA(
        lotteryName = "Megasena",
        url = "megasena",
        total = 60,
        maximum = "15",
        minimum = "06",
        primaryColor = R.color.colorMegasena,
        secondaryColor = android.R.color.white
    ),
    LOTOFACIL(
        lotteryName = "Lotofácil",
        url = "lotofacil",
        total = 25,
        maximum = "18",
        minimum = "15",
        primaryColor = R.color.colorLotofacil,
        secondaryColor = android.R.color.white
    ),
    LOTOMANIA(
        lotteryName = "Lotomania",
        url = "lotomania",
        total = 100,
        maximum = "50",
        minimum = "50",
        R.color.colorLotomania,
        secondaryColor = android.R.color.white
    ),
    QUINA(
        lotteryName = "Quina",
        url = "quina",
        total = 80,
        maximum = "15",
        minimum = "05",
        primaryColor = R.color.colorQuina,
        secondaryColor =android.R.color.white
    ),
    TIMEMANIA(
        lotteryName = "Timemania",
        url = "timemania",
        total = 80,
        maximum = "10",
        minimum = "10",
        primaryColor = R.color.colorTimemania,
        secondaryColor = R.color.colorTimemaniaSecondary
    );

    companion object {
        fun getByName(name: String): LotteryType? = values().find { it.lotteryName == name }
    }

    override fun toString(): String = lotteryName
}
