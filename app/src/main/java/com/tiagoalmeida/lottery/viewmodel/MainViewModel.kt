package com.tiagoalmeida.lottery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.network.repository.ConsultRepository
import com.tiagoalmeida.lottery.network.repository.PreferencesRepository
import com.tiagoalmeida.lottery.viewmodel.base.BaseViewModel

class MainViewModel(
    crashlytics: FirebaseCrashlytics,
    private val consultRepository: ConsultRepository,
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel(crashlytics) {

    private val _results = MutableLiveData<List<LotteryResult>>()
    val results = _results as LiveData<List<LotteryResult>>

    fun consultLastResults() = runWithCoroutines(
        handleLoadingAutomatically = true,
        doInBackground = {
            _results.postValue(consultRepository.consultAll())
        },
        doWhenErrorOccurs = { exception ->
            logError(exception, "consultLastResults()")

            _results.postValue(listOf())
        }
    )

    fun updateLastGamesContestNumbers(games: List<LotteryResult>) {
        games.forEach { game ->
            val lastContestNumber = preferencesRepository.getLastSavedContestNumber(game.getLotteryType())
            if (game.contestNumber.toInt() > lastContestNumber) {
                preferencesRepository.saveLastContestNumber(
                    game.getLotteryType(),
                    game.contestNumber.toInt()
                )
            }
        }
    }

}
