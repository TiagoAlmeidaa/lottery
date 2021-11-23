package com.tiagoalmeida.lottery.model.vo

import com.tiagoalmeida.lottery.util.enums.LotteryType

data class GamesFilter(
    val lotteryType: LotteryType? = null,
    val contestNumber: String = "",
    val hideOldNumbers: Boolean = false
) {
    fun hasAnyFilterApplied() = lotteryType != null || contestNumber.isNotEmpty() || hideOldNumbers
}