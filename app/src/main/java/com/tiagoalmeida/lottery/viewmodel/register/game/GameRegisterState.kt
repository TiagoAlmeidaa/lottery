package com.tiagoalmeida.lottery.viewmodel.register.game

import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.model.vo.UserGame

sealed class GameRegisterState {
    object LotteryTypeInvalid : GameRegisterState()
    object LotteryTypeOk : GameRegisterState()
    data class ContestWithError(val messageId: Int = R.string.game_register_error_internal) : GameRegisterState()
    object ContestOk : GameRegisterState()
    data class NumbersWithError(val messageId: Int = R.string.game_register_error_internal) : GameRegisterState()
    data class ProceedToSaveNumbers(val game: UserGame) : GameRegisterState()
    object GameUpdated : GameRegisterState()
    data class Timeout(val messageId: Int = R.string.game_register_error_timeout) : GameRegisterState()
}
