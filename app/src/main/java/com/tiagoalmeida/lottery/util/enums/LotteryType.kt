package com.tiagoalmeida.lottery.util.enums

import com.tiagoalmeida.lottery.R

enum class LotteryType(
    val lotteryName: String,
    val url: String,
    val total: Int,
    val maximum: String,
    val minimum: String,
    val color: Int
) {

    MEGASENA("Megasena", "megasena",60, "15", "06", R.color.colorMegasena),
    LOTOFACIL("Lotofácil", "lotofacil",25, "18", "15", R.color.colorLotofacil),
    LOTOMANIA("Lotomania", "lotomania",100, "50", "50", R.color.colorLotomania),
    QUINA("Quina", "quina",80, "15", "05", R.color.colorQuina),
    TIMEMANIA("Timemania", "timemania", 80, "10", "10", R.color.colorTimemania);

    companion object {

        fun getByName(name: String): LotteryType? = when (name) {
            MEGASENA.lotteryName -> MEGASENA
            LOTOFACIL.lotteryName -> LOTOFACIL
            LOTOMANIA.lotteryName -> LOTOMANIA
            QUINA.lotteryName -> QUINA
            TIMEMANIA.lotteryName -> TIMEMANIA
            else -> null
        }

    }

    override fun toString(): String = lotteryName
}
