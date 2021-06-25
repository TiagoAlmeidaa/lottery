package com.tiagoalmeida.lottery.util.extensions

import android.widget.AutoCompleteTextView
import com.tiagoalmeida.lottery.util.enums.LotteryType

fun AutoCompleteTextView.getLotteryType(): LotteryType? = LotteryType.getByName(text.toString())