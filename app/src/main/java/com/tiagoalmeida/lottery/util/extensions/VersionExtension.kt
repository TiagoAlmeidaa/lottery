package com.tiagoalmeida.lottery.util.extensions

import android.os.Build

fun isSDKVersionBiggerThanO(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}