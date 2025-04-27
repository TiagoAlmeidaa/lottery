package com.tiagoalmeida.lottery.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.domain.ConsultLatestResultsUseCase
import com.tiagoalmeida.lottery.ui.BaseViewModel

class MainViewModel(
    crashlytics: FirebaseCrashlytics,
    private val analytics: FirebaseAnalytics,
    private val preferencesRepository: PreferencesRepository,
    private val consultLatestResultsUseCase: ConsultLatestResultsUseCase
) : BaseViewModel(crashlytics, analytics) {

    private val _results = MutableLiveData<List<LotteryResult>>()
    val results = _results as LiveData<List<LotteryResult>>

    fun consultLastResults() = runWithCoroutines(
        analyticsName = "consult_last_results",
        doInBackground = {
            val latestResults = consultLatestResultsUseCase()

            analytics.logEvent("api_consult_last_results") {
                param("types", latestResults.joinToString { it.name })
            }

            _results.postValue(latestResults)
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
