package com.tiagoalmeida.lottery.network.repository

import com.tiagoalmeida.lottery.model.mapper.LotteryResultParser
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.network.datasource.ConsultDataSource
import com.tiagoalmeida.lottery.util.enums.LotteryType
import retrofit2.Response

internal class ConsultRepositoryImpl(
    private val parser: LotteryResultParser,
    private val dataSource: ConsultDataSource
) : ConsultRepository {

    override suspend fun consultAll(): List<LotteryResult> {
        val results = mutableListOf<LotteryResult>()

        executeParse(dataSource.consultContest(LotteryType.MEGASENA)) { response ->
            results.add(response)
        }
        executeParse(dataSource.consultContest(LotteryType.LOTOFACIL)) { response ->
            results.add(response)
        }
        executeParse(dataSource.consultContest(LotteryType.LOTOMANIA)) { response ->
            results.add(response)
        }
        executeParse(dataSource.consultContest(LotteryType.QUINA)) { response ->
            results.add(response)
        }

        return results
    }

    override suspend fun consultLatestContest(type: LotteryType): LotteryResult? {
        var contest: LotteryResult? = null

        executeParse(dataSource.consultContest(type)) {
                response -> contest = response
        }

        return contest
    }

    override suspend fun consultContest(type: LotteryType, contestNumber: Int): LotteryResult? {
        var contest: LotteryResult? = null

        executeParse(dataSource.consultContestByNumber(type, contestNumber)) { response ->
            contest = response
        }

        return contest
    }

    override suspend fun consultContests(type: LotteryType, startContest: Int, endContest: Int): List<LotteryResult> {
        val results = mutableListOf<LotteryResult>()

        for (contestNumber in endContest downTo startContest) {
            executeParse(dataSource.consultContestByNumber(type, contestNumber)) { response ->
                if (response.contestNumber.toInt() == contestNumber) {
                    results.add(response)
                }
            }
        }

        return results
    }

    fun executeParse(request: Response<String>, response: (LotteryResult) -> Unit) {
        parser.from(request)?.let(response)
    }

}
