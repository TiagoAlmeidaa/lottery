package com.tiagoalmeida.lottery.util.extensions

import android.content.res.Resources

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun Int.toStringNumber(): String = if (this < 10) {
    "0$this"
} else {
    this.toString()
}
