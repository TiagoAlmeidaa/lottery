package com.tiagoalmeida.lottery.ui.detail

import androidx.lifecycle.LiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.RangedLottery
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.data.repository.ConsultRepository
import com.tiagoalmeida.lottery.domain.ConsultRangedResultsUseCase
import com.tiagoalmeida.lottery.util.SingleLiveEvent
import com.tiagoalmeida.lottery.ui.BaseViewModel

class DetailGameViewModel(
    crashlytics: FirebaseCrashlytics,
    private val userGame: UserGame,
    private val consultRepository: ConsultRepository,
    private val consultRangedResultsUseCase: ConsultRangedResultsUseCase
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
                val contest = if (isValidForFutureContests()) {
                    consultRepository.consultLatestContest(type)
                } else {
                    consultRepository.consultContestByNumber(type, getEndContestInt())
                }

                val contestNumber = contest.contestNumber.toInt()

                if (!isNotValidContestForThisGame(contestNumber)) {
                    results.add(contest)

                    val difference = contestNumber - getStartContestInt()
                    if (difference > 0) {
                        val result = calculateRange(contestNumber, difference)

                        results.addAll(
                            consultRangedResultsUseCase(
                                type,
                                result.startContestNumber,
                                result.endContestNumber
                            )
                        )
                    }
                }

                val state = if (results.isEmpty()) {
                    DetailGameState.NoResultsYet
                } else {
                    DetailGameState.ContestsReceived(results)
                }

                _viewState.postValue(state)
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
                    val result = calculateRange(lastDownloadedContestNumber, difference)

                    results.addAll(
                        consultRangedResultsUseCase(
                            type,
                            result.startContestNumber,
                            result.endContestNumber
                        )
                    )
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
            val result = consultRepository.consultContestByNumber(userGame.type, contestNumber)
            val state = if (contestNumber == result.contestNumber.toInt()) {
                DetailGameState.ContestFiltered(result)
            } else {
                DetailGameState.ContestNotFound(contestNumber.toString())
            }

            _viewState.postValue(state)
        },
        doWhenErrorOccurs = { exception ->
            logError(exception, "consultContest()")

            val contestString = contestNumber.toString()

            _viewState.postValue(DetailGameState.ContestNotFound(contestString))
        }
    )

    fun getContestNumber(): String = with(userGame) {
        if (isSingleGame()) {
            startContestNumber
        } else {
            val endNumber = if (endContestNumber.isEmpty()) "∞" else endContestNumber
            "$startContestNumber - $endNumber"
        }
    }

    fun calculateRange(contestNumber: Int, difference: Int): RangedLottery {
        val end = contestNumber - 1
        val start = if (difference > 10) {
            end - 9
        } else {
            contestNumber - difference
        }

        lastDownloadedContestNumber = start

        return RangedLottery(start, end)
    }

    fun getColorId(): Int = userGame.type.primaryColor

    fun getNumbers(): List<Int> = userGame.numbers

    fun getUserGame(): UserGame = userGame

    fun isLoading(): Boolean = loading.value ?: false

}
