package com.tiagoalmeida.lottery.ui.results

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.AdapterResultsBinding
import com.tiagoalmeida.lottery.data.model.LotteryAward
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.ui.common.NumberAdapter
import com.tiagoalmeida.lottery.extensions.gone
import com.tiagoalmeida.lottery.extensions.toAppDateString
import com.tiagoalmeida.lottery.extensions.toCurrency
import com.tiagoalmeida.lottery.extensions.visible
import java.util.*

class ResultsViewHolder(
    private val binding: AdapterResultsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(result: LotteryResult, onHeaderClick: (Int) -> Unit) {
        val type = result.getLotteryType()
        val numbers = result.numbersDrawn.map { it.toInt() }

        with(binding) {
            colorId = type.primaryColor

            textViewContest.text = type.toString()
            textViewContestNumber.text = formatContestNumber(result.contestNumber)
            textViewContestWinners.text = formatContestWinners(result.awards)
            textViewContestDate.text = Date(result.contestDate).toAppDateString()
            textViewNextContestDate.text = Date(result.nextContestDate).toAppDateString()
            textViewNextContestPrize.text = if (result.nextContestPrize == 0.0) {
                binding.root.context.getString(R.string.results_adapter_without_next_prize)
            } else {
                result.nextContestPrize.toCurrency()
            }

            recyclerViewNumbers.adapter = NumberAdapter(type.primaryColor, numbers)
            recyclerViewNumbers.layoutManager = getFlexBoxLayoutManager()

            layoutHeader.setOnClickListener {
                onHeaderClick(adapterPosition)
            }
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

    private fun formatContestNumber(contestNumber: String): String {
        val context = binding.root.context
        return String.format(context.getString(R.string.game_title), contestNumber)
    }

    private fun formatContestWinners(awards: List<LotteryAward>): String {
        val biggerPrize = awards.maxByOrNull { it.hits }
        return String.format(
            binding.root.context.getString(R.string.winners),
            biggerPrize?.winnersCount?.toInt() ?: 0
        )
    }

    private fun getFlexBoxLayoutManager() = FlexboxLayoutManager(binding.root.context).apply {
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.CENTER
    }
}