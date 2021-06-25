package com.tiagoalmeida.lottery.util.extensions

fun String.isInEmailFormat(): Boolean {
    val regex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"

    val x = regex.toRegex()

    return x.matches(this)
}
