package com.tiagoalmeida.lottery.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.ui.BaseViewModel
import com.tiagoalmeida.lottery.util.SingleLiveEvent

class GameRegisterViewModel(
    crashlytics: FirebaseCrashlytics,
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel(crashlytics) {

    private val _viewState = SingleLiveEvent<GameRegisterState>()
    val viewState = _viewState as LiveData<GameRegisterState>

    val userGame = MutableLiveData<UserGame>().apply { value = UserGame() }

    val validForAllFutureContests = MutableLiveData<Boolean>().apply { value = false }

    val singleGame = MutableLiveData<Boolean>().apply { value = false }

    fun validateLotteryType(type: LotteryType?) {
        val state = if (type == null) {
            GameRegisterState.LotteryTypeInvalid
        } else {
            userGame.value!!.type = type
            GameRegisterState.LotteryTypeOk
        }

        _viewState.postValue(state)
    }

    fun validateContest() {
        with(userGame.value) {
            val state = when {
                this == null -> GameRegisterState.ContestWithError()
                startContestNumber.isEmpty() -> GameRegisterState.ContestWithError(R.string.game_register_error_start_contest_empty)
                shouldValidateEndContest() && endContestNumber.isEmpty() -> GameRegisterState.ContestWithError(R.string.game_register_error_end_contest_empty)
                shouldValidateEndContest() && startContestNumber.toInt() > endContestNumber.toInt() -> GameRegisterState.ContestWithError(R.string.game_register_error_start_bigger_than_end)
                else -> GameRegisterState.ContestOk
            }

            _viewState.postValue(state)
        }
    }

    fun shouldValidateEndContest(): Boolean {
        return validForAllFutureContests.value == false && singleGame.value == false
    }

    fun clearNumbers() {
        userGame.value!!.clearNumbers()
    }

    fun containsNumber(number: Int): Boolean {
        return userGame.value!!.containsNumber(number)
    }

    fun getLotteryType(): LotteryType {
        return userGame.value!!.type
    }

    fun getNumbersCount(): Int {
        return userGame.value!!.numbersCount()
    }

    fun onNumberPicked(number: Int) {
        with(userGame.value!!) {
            if (!containsNumber(number)) {
                if (numbersCount() < getMaximum()) {
                    addNumber(number)
                } else {
                    _viewState.postValue(GameRegisterState.NumbersWithError(R.string.game_register_error_maximum))
                }
            } else {
                removeNumber(number)
            }
        }
    }

    fun validateNumbers() {
        with(userGame.value!!) {
            val state = if (numbersCount() >= getMinimum()) {
                numbers = numbers.sorted().toMutableList()

                GameRegisterState.ProceedToSaveNumbers(this)
            } else {
                GameRegisterState.NumbersWithError(R.string.game_register_error_minimum)
            }

            _viewState.postValue(state)
        }
    }

    fun saveNumbers(game: UserGame) {
        startLoading()
        try {
            if (singleGame.value == true) {
                game.endContestNumber = game.startContestNumber
            }

            preferencesRepository.saveGame(game)

            _viewState.postValue(GameRegisterState.GameUpdated)
        } catch (exception: Exception) {
            finishLoading()

            _viewState.postValue(GameRegisterState.NumbersWithError())
        }
    }

}
