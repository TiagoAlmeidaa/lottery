package com.tiagoalmeida.lottery.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.ui.BaseViewModel
import com.tiagoalmeida.lottery.util.SingleLiveEvent

class GameRegisterViewModel(
    crashlytics: FirebaseCrashlytics,
    private val analytics: FirebaseAnalytics,
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel(crashlytics, analytics) {

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
            if (!containsNumber(number) && numbersCount() < getMaximum()) {
                addNumber(number)
            } else {
                removeNumber(number)
            }

            val state = if (numbersCount() < getMinimum()) {
                GameRegisterState.OnNumberPicked(
                    messageId = R.string.game_register_select_minimum_numbers,
                    messageColorId = R.color.colorWarning,
                    isSaveButtonEnabled = false
                )
            } else if (numbersCount() >= getMinimum() && numbersCount() < getMaximum()) {
                GameRegisterState.OnNumberPicked(
                    messageId = R.string.game_register_minimum_numbers_selected,
                    messageColorId = R.color.colorSuccess,
                    isSaveButtonEnabled = true
                )
            } else {
                GameRegisterState.OnNumberPicked(
                    messageId = R.string.game_register_maximum_numbers_selected,
                    messageColorId = R.color.colorWarning,
                    isSaveButtonEnabled = true
                )
            }

            _viewState.postValue(state)
        }
    }

    fun saveGame() {
        try {
            startLoading()

            with(userGame.value!!) {
                val sortedNumbers = numbers.sorted().toMutableList()

                if (singleGame.value == true) {
                    endContestNumber = startContestNumber
                }

                preferencesRepository.saveGame(copy(numbers = sortedNumbers))

                analytics.logEvent("register_game_saved") {
                    param("type", type.name)
                    param("start_contest", startContestNumber)
                    param("end_contest", endContestNumber)
                    param("numbers_selected", sortedNumbers.size.toString())
                    param("single_game", (singleGame.value == true).toString())
                    param("participate_all_future_contests", (validForAllFutureContests.value == true).toString())
                }

                _viewState.postValue(GameRegisterState.GameUpdated)
            }
        } catch (exception: Exception) {
            finishLoading()

            _viewState.postValue(GameRegisterState.NumbersWithError())
        }
    }
}
