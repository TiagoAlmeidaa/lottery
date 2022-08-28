package com.tiagoalmeida.lottery.ui.games

import com.tiagoalmeida.lottery.data.model.LotteryType

data class GamesFilter(
    val lotteryType: LotteryType? = null,
    val contestNumber: String = "",
    val hideOldNumbers: Boolean = false
) {
    fun hasAnyFilterApplied() = lotteryType != null || contestNumber.isNotEmpty() || hideOldNumbers
}