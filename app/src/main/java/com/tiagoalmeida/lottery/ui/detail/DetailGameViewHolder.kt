package com.tiagoalmeida.lottery.ui.detail

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.model.LotteryAward
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.databinding.AdapterDetailGameBinding
import com.tiagoalmeida.lottery.extensions.gone
import com.tiagoalmeida.lottery.extensions.toCurrency
import com.tiagoalmeida.lottery.extensions.visible

class DetailGameViewHolder(
    val binding: AdapterDetailGameBinding,
    val userGame: UserGame
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(result: LotteryResult) {
        val type = result.getLotteryType()
        val numbers = result.numbersDrawn.map { it.toInt() }
        val hits = countHits(numbers)

        with(binding) {
            colorId = type.primaryColor

            textViewContest.text = type.toString()
            textViewContestNumber.text = formatContestNumber(result.contestNumber)
            textViewHits.text = formatHits(hits)
            textViewEarnings.text = formatEarnings(result.awards, hits)

            if (isGameForTheContest(result.contestNumber)) {
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

    private fun isGameForTheContest(contestNumberString: String): Boolean {
        val contestNumber = if (contestNumberString.isNotEmpty()) contestNumberString.toInt() else -1
        val startContest = if (userGame.startContestNumber.isNotEmpty()) userGame.startContestNumber.toInt() else -1
        val endContest = if (userGame.endContestNumber.isNotEmpty()) userGame.endContestNumber.toInt() else 0

        return if (startContest == -1 || contestNumber == -1) {
            true
        } else {
            startContest <= contestNumber && (endContest == 0 || endContest >= contestNumber)
        }
    }

    private fun getFlexBoxLayoutManager() = FlexboxLayoutManager(binding.root.context).apply {
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.CENTER
    }
}
