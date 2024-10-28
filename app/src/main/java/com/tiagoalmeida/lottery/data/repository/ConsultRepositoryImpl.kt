package com.tiagoalmeida.lottery.data.repository

import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.data.source.ConsultDataSource

internal class ConsultRepositoryImpl(
    private val dataSource: ConsultDataSource
) : ConsultRepository {

    override suspend fun consultLatestContest(type: LotteryType): LotteryResult {
        return dataSource.consultContest(type)
    }

    override suspend fun consultContestByNumber(type: LotteryType, contestNumber: Int): LotteryResult {
        return dataSource.consultContestByNumber(type, contestNumber)
    }
}
