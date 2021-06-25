package com.tiagoalmeida.lottery.util.extensions

import android.content.SharedPreferences

fun SharedPreferences.getString(key: String): String {
    return getString(key, "") ?: ""
}

fun SharedPreferences.putString(key: String, value: String) {
    edit().putString(key, value).apply()
}

fun SharedPreferences.getInt(key: String): Int {
    return getInt(key, 0)
}

fun SharedPreferences.putInt(key: String, value: Int) {
    edit().putInt(key, value).apply()
}