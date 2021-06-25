package com.tiagoalmeida.lottery.viewmodel.detail

import androidx.lifecycle.LiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.network.repository.ConsultRepository
import com.tiagoalmeida.lottery.util.SingleLiveEvent
import com.tiagoalmeida.lottery.viewmodel.base.BaseViewModel

class DetailGameViewModel(
    crashlytics: FirebaseCrashlytics,
    private val userGame: UserGame,
    private val consultRepository: ConsultRepository
) : BaseViewModel(crashlytics) {

    private val _viewState = SingleLiveEvent<DetailGameState>()
    val viewState = _viewState as LiveData<DetailGameState>

    private var lastDownloadedContestNumber = 0

    fun resetLastDownloadedContestNumber() {
        lastDownloadedContestNumber = 0
    }

    fun consultContestsForGame() = runWithCoroutines(
        handleLoadingAutomatically = true,
        doInBackground = {
            val results = mutableListOf<LotteryResult>()

            with(userGame) {
                val lastContest = if (getEndContestInt() == 0) {
                    consultRepository.consultLatestContest(type)
                } else {
                    consultRepository.consultContest(type, getEndContestInt())
                }

                lastContest?.let { safeLastContest ->
                    results.add(safeLastContest)

                    val lastContestNumber = safeLastContest.contestNumber.toInt()
                    val difference = lastContestNumber - getStartContestInt()

                    if (difference > 0) {
                        val end = lastContestNumber - 1
                        lastDownloadedContestNumber = if (difference > 10) {
                            end - 9
                        } else {
                            lastContestNumber - difference
                        }

                        consultRepository
                            .consultContests(type, lastDownloadedContestNumber, end)
                            .let { result -> results.addAll(result) }
                    }
                }

                _viewState.postValue(DetailGameState.ContestsReceived(results))
            }
        },
        doWhenErrorOccurs = { exception ->
            logError(exception, "consultContestsForGame()")

            _viewState.postValue(DetailGameState.ContestsReceived(mutableListOf()))
        }
    )

    fun consultNextPage() = runWithCoroutines(
        handleLoadingAutomatically = true,
        doInBackground = {
            val results = mutableListOf<LotteryResult>()

            with(userGame) {
                val difference = lastDownloadedContestNumber - getStartContestInt()
                if (difference > 0) {
                    val end = lastDownloadedContestNumber - 1
                    lastDownloadedContestNumber = if (difference > 10) {
                        end - 9
                    } else {
                        lastDownloadedContestNumber - difference
                    }

                    consultRepository
                        .consultContests(type, lastDownloadedContestNumber, end)
                        .let { result -> results.addAll(result) }
                }
            }

            if (results.isNotEmpty()) {
                _viewState.postValue(DetailGameState.ContestsReceived(results))
            }
        },
        doWhenErrorOccurs = { exception ->
            logError(exception, "consultNextPage()")
        }
    )

    fun consultContest(contestNumber: Int) = runWithCoroutines(
        handleLoadingAutomatically = true,
        doInBackground = {
            consultRepository.consultContest(userGame.type, contestNumber)?.let { result ->
                val state = if (contestNumber == result.contestNumber.toInt()) {
                    DetailGameState.ContestFiltered(result)
                } else {
                    DetailGameState.ContestNotFound(contestNumber.toString())
                }

                _viewState.postValue(state)
            }
        },
        doWhenErrorOccurs = { exception ->
            logError(exception, "consultContest()")

            val contestString = contestNumber.toString()

            _viewState.postValue(DetailGameState.ContestNotFound(contestString))
        }
    )

    fun calculateContestRange(
        startNumber: Int,
        endNumber: Int,
        lastNumber: Int
    ): Triple<Int, Int, Boolean> {
        var newStart = startNumber
        var newEnd = endNumber

        val useLastGame = endNumber == 0 || endNumber >= lastNumber

        if (startNumber <= lastNumber - 1 && useLastGame) {
            newEnd = lastNumber - 1
        }

        if (startNumber <= lastNumber - 2 && useLastGame) {
            newStart = lastNumber - 2
        } else if (startNumber <= endNumber - 2) {
            newStart = endNumber - 2
        }

        return Triple(newStart, newEnd, useLastGame)
    }

    fun getContestNumber(): String = with(userGame) {
        val endNumber = if (endContestNumber.isEmpty()) "∞" else endContestNumber
        "$startContestNumber - $endNumber"
    }

    fun getColorId(): Int = userGame.type.color

    fun getNumbers(): List<Int> = userGame.numbers

    fun getUserGame(): UserGame = userGame

    fun isLoading(): Boolean = loading.value ?: false

}
