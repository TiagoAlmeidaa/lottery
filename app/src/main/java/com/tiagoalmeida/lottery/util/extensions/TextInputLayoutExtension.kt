package com.tiagoalmeida.lottery.util.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.putError(message: String) {
    isErrorEnabled = true
    error = message
}

fun TextInputLayout.removeError() {
    isErrorEnabled = false
    error = ""
}

fun TextInputLayout.hasNoErrors(): Boolean {
    return !isErrorEnabled
}
