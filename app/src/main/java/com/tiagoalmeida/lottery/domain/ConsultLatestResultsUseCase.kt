package com.tiagoalmeida.lottery.domain

import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.repository.ConsultRepository

class ConsultLatestResultsUseCase(private val repository: ConsultRepository) {

    suspend operator fun invoke(): List<LotteryResult> = mutableListOf<LotteryResult>().apply {
        LotteryType.values().forEach { type ->
            add(repository.consultLatestContest(type))
        }
    }
}