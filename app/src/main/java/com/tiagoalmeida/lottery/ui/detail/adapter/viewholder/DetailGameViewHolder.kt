package com.tiagoalmeida.lottery.ui.detail.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.AdapterDetailGameBinding
import com.tiagoalmeida.lottery.model.vo.LotteryAward
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.ui.adapter.NumberMatchedAdapter
import com.tiagoalmeida.lottery.util.extensions.gone
import com.tiagoalmeida.lottery.util.extensions.toCurrency
import com.tiagoalmeida.lottery.util.extensions.visible

class DetailGameViewHolder(
    val binding: AdapterDetailGameBinding,
    val userGame: UserGame
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(result: LotteryResult) {
        val type = result.getLotteryType()
        val numbers = result.numbersDrawn.map { it.toInt() }
        val hits = countHits(numbers)

        with(binding) {
            lotteryType = type

            textViewContest.text = type.toString()
            textViewContestNumber.text = formatContestNumber(result.contestNumber)
            textViewHits.text = formatHits(hits)
            textViewEarnings.text = formatEarnings(result.awards, hits)

            if (isGameForTheContest(result.contestNumber.toInt())) {
                textViewWithoutParticipation.gone()
            } else {
                textViewWithoutParticipation.visible()
            }

            recyclerViewNumbers.adapter = NumberMatchedAdapter(userGame, numbers)
            recyclerViewNumbers.layoutManager = getFlexBoxLayoutManager()
        }
    }

    fun showOrHideExpandableLayout(show: Boolean) {
        with(binding) {
            if (show) {
                imageViewChevron.setImageResource(R.drawable.icon_chevron_up)
                expandableLayout.visible()
            } else {
                imageViewChevron.setImageResource(R.drawable.icon_chevron_down)
                expandableLayout.gone()
            }
        }
    }

    private fun countHits(numbers: List<Int>): Int =
        userGame.numbers.filter { numbers.contains(it) }.size

    private fun formatContestNumber(contestNumber: String): String {
        val context = binding.root.context
        return String.format(context.getString(R.string.game_title), contestNumber)
    }

    private fun formatEarnings(awards: List<LotteryAward>, hits: Int): String {
        val award = awards.find { it.hits == hits }
        return award?.value?.toDouble()?.toCurrency() ?: 0.toDouble().toCurrency()
    }

    private fun formatHits(hits: Int): String {
        val context = binding.root.context
        return when (hits) {
            0 -> context.getString(R.string.detail_game_adapter_no_hits)
            1 -> context.getString(R.string.detail_game_adapter_one_hit)
            else -> String.format(context.getString(R.string.detail_game_adapter_hits), hits)
        }
    }

    private fun isGameForTheContest(contestNumber: Int): Boolean {
        val startContest = userGame.startContestNumber.toInt()
        val endContest =
            if (userGame.endContestNumber.isNotEmpty()) userGame.endContestNumber.toInt() else 0

        return startContest <= contestNumber && (endContest == 0 || endContest >= contestNumber)
    }

    private fun getFlexBoxLayoutManager() = FlexboxLayoutManager(binding.root.context).apply {
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.CENTER
    }
}