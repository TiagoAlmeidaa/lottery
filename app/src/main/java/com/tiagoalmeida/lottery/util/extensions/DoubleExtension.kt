package com.tiagoalmeida.lottery.util.extensions

import com.tiagoalmeida.lottery.util.Brazil
import java.text.NumberFormat

fun Double.toCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Brazil.locale)
    return format.format(this)
}
