package com.tiagoalmeida.lottery.ui.register

import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.model.UserGame

sealed class GameRegisterState {
    data object LotteryTypeInvalid : GameRegisterState()
    data object LotteryTypeOk : GameRegisterState()
    data class ContestWithError(val messageId: Int = R.string.game_register_error_internal) : GameRegisterState()
    data object ContestOk : GameRegisterState()
    data class NumbersWithError(val messageId: Int = R.string.game_register_error_internal) : GameRegisterState()
    data class OnNumberPicked(val messageId: Int, val messageColorId: Int, val isSaveButtonEnabled: Boolean) : GameRegisterState()
    data object GameUpdated : GameRegisterState()
}
