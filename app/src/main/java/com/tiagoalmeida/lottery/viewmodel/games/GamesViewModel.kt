package com.tiagoalmeida.lottery.viewmodel.games

import androidx.lifecycle.LiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.model.vo.GamesFilter
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.network.repository.PreferencesRepository
import com.tiagoalmeida.lottery.util.SingleLiveEvent
import com.tiagoalmeida.lottery.viewmodel.base.BaseViewModel
import kotlinx.coroutines.TimeoutCancellationException

class GamesViewModel(
    crashlytics: FirebaseCrashlytics,
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel(crashlytics) {

    private val _viewState = SingleLiveEvent<GamesState>()
    val viewState = _viewState as LiveData<GamesState>

    private val _gamesFilter = SingleLiveEvent<GamesFilter>()
    val gamesFilter = _gamesFilter as LiveData<GamesFilter>

    fun findGames() = runWithCoroutines(
        handleLoadingAutomatically = true,
        doInBackground = {
            var games = preferencesRepository.getGames()

            gamesFilter.value?.let { filter ->
                if (filter.lotteryType != null) {
                    games = games.filter { userGame ->
                        userGame.type == filter.lotteryType
                    }
                }
                if (filter.contestNumber.isNotEmpty()) {
                    games = games.filter { userGame ->
                        filter.contestNumber >= userGame.startContestNumber &&
                                (userGame.endContestNumber.isEmpty() || filter.contestNumber <= userGame.endContestNumber)
                    }
                }
            }

            games = games.sortedByDescending { game -> game.getStartContestInt() }

            _viewState.postValue(GamesState.GamesReceived(games))
        },
        doWhenErrorOccurs = { exception ->
            logError(exception, "findGames()")
        }
    )

    fun removeGame(userGame: UserGame) = runWithCoroutines(
        handleLoadingAutomatically = true,
        doInBackground = {
            preferencesRepository.removeGame(userGame)

            _viewState.postValue(GamesState.GameRemoved)
        },
        doWhenErrorOccurs = { exception ->
            logError(exception, "removeGame()")

            val state = if (exception is TimeoutCancellationException) {
                GamesState.Timeout
            } else {
                GamesState.InternalError
            }

            _viewState.postValue(state)
        }
    )

    fun createFilterMessage(filter: GamesFilter): Pair<String, String> = with(filter) {
        if (lotteryType != null && contestNumber.isNotEmpty()) {
            Pair(lotteryType.lotteryName, contestNumber)
        } else if (lotteryType != null) {
            Pair(lotteryType.lotteryName, "")
        } else if (contestNumber.isNotEmpty()) {
            Pair("", contestNumber)
        } else {
            Pair("", "")
        }
    }

    fun setFilter(filter: GamesFilter) = _gamesFilter.postValue(filter)

    fun getFilter() = _gamesFilter.value ?: GamesFilter()

    fun hasGamesInPreferences() = preferencesRepository.getGames().isNotEmpty()

    fun deleteAllGamesInPreferences() = preferencesRepository.deleteAllGames()

}