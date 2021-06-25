package com.tiagoalmeida.lottery.util

import com.tiagoalmeida.lottery.util.Constants.KEYS_FILE_NAME

object Keys {

    init {
        System.loadLibrary(KEYS_FILE_NAME)
    }

    external fun apiKey(): String

    external fun googleKey(): String
}