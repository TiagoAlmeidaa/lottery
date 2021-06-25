package com.tiagoalmeida.lottery.model.vo

import com.google.gson.Gson
import com.tiagoalmeida.lottery.util.enums.LotteryType

data class UserGame(
    var startContestNumber: String = "",
    var endContestNumber: String = "",
    var type: LotteryType = LotteryType.MEGASENA,
    var numbers: MutableList<Int> = mutableListOf()
) {
    fun getMinimum() = type.minimum.toInt()

    fun getMaximum() = type.maximum.toInt()

    fun addNumber(number: Int) = numbers.add(number)

    fun removeNumber(number: Int) = numbers.remove(number)

    fun containsNumber(number: Int) = numbers.contains(number)

    fun numbersCount() = numbers.size

    fun clearNumbers() = numbers.clear()

    fun toJson(): String = Gson().toJson(this)

    fun getStartContestInt() = if (startContestNumber.isEmpty()) 0 else startContestNumber.toInt()

    fun getEndContestInt() = if (endContestNumber.isEmpty()) 0 else endContestNumber.toInt()
}
