package com.tiagoalmeida.lottery.ui.register

interface GridNumberPickedEvents {
    fun onNumberPicked(number: Int)
    fun hasNumber(number: Int): Boolean
}