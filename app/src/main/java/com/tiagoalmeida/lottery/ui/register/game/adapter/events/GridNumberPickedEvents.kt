package com.tiagoalmeida.lottery.ui.register.game.adapter.events

interface GridNumberPickedEvents {
    fun onNumberPicked(number: Int)
    fun hasNumber(number: Int): Boolean
}