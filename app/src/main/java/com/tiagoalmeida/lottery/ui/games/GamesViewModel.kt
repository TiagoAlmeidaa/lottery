package com.tiagoalmeida.lottery.ui.games

import androidx.lifecycle.LiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.data.repository.PreferencesRepository
import com.tiagoalmeida.lottery.ui.BaseViewModel
import com.tiagoalmeida.lottery.util.SingleLiveEvent
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
                with(filter) {
                    if (lotteryType != null) {
                        games = games.filter { userGame ->
                            userGame.type == lotteryType
                        }
                    }
                    if (contestNumber.isNotEmpty()) {
                        games = games.filter { userGame ->
                            contestNumber >= userGame.startContestNumber &&
                                    (userGame.endContestNumber.isEmpty() || contestNumber <= userGame.endContestNumber)
                        }
                    }
                    if (filter.hideOldNumbers) {
                        games = games.filter { userGame ->
                            val lastContestNumber = preferencesRepository.getLastSavedContestNumber(userGame.type)

                            lastContestNumber == 0
                                    || userGame.isValidForFutureContests()
                                    || userGame.getEndContestInt() >= lastContestNumber
                        }
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

    fun setFilter(filter: GamesFilter) = _gamesFilter.postValue(filter)

    fun getFilter() = _gamesFilter.value ?: GamesFilter()

    fun hasGamesInPreferences() = preferencesRepository.getGames().isNotEmpty()

    fun deleteAllGamesInPreferences() = preferencesRepository.deleteAllGames()

}
