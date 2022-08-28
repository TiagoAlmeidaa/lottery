package com.tiagoalmeida.lottery.ui.detail

import com.tiagoalmeida.lottery.data.model.LotteryResult

sealed class DetailGameState {
    data class ContestsReceived(val results: List<LotteryResult>) : DetailGameState()
    data class ContestFiltered(val lotteryResult: LotteryResult) : DetailGameState()
    data class ContestNotFound(val contestNumber: String) : DetailGameState()
    object NoResultsYet : DetailGameState()
}