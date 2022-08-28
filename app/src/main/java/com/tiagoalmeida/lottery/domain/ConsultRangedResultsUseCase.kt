package com.tiagoalmeida.lottery.domain

import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.repository.ConsultRepository

class ConsultRangedResultsUseCase(private val repository: ConsultRepository) {

    suspend operator fun invoke(
        type: LotteryType,
        startContest: Int,
        endContest: Int
    ): List<LotteryResult> = mutableListOf<LotteryResult>().apply {
        for (contestNumber in endContest downTo startContest) {
            add(repository.consultContestByNumber(type, contestNumber))
        }
    }
}