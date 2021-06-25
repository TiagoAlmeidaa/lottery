package com.tiagoalmeida.lottery.util.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.toAppDateString(): String {
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return simpleDateFormat.format(this)
}
