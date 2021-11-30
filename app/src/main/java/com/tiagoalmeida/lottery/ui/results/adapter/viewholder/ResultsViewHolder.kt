package com.tiagoalmeida.lottery.ui.results.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.AdapterResultsBinding
import com.tiagoalmeida.lottery.model.vo.LotteryAward
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.ui.adapter.NumberAdapter
import com.tiagoalmeida.lottery.util.enums.LotteryType
import com.tiagoalmeida.lottery.util.extensions.gone
import com.tiagoalmeida.lottery.util.extensions.toAppDateString
import com.tiagoalmeida.lottery.util.extensions.toCurrency
import com.tiagoalmeida.lottery.util.extensions.visible
import java.util.*

class ResultsViewHolder(
    private val binding: AdapterResultsBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var chevronUp: Int = R.drawable.icon_chevron_up
    private var chevronDown: Int = R.drawable.icon_chevron_down

    fun bind(result: LotteryResult, onHeaderClick: (Int) -> Unit) {
        val type = result.getLotteryType()
        val numbers = result.numbersDrawn.map { it.toInt() }
        val numberAdapter: NumberAdapter

        with(binding) {
            backColorId = type.color

            if (type == LotteryType.TIMEMANIA) {
                numberAdapter = NumberAdapter(type.color, numbers, textColor = R.color.colorTimemaniaText)
                primaryColorId = R.color.colorTimemaniaText
                secondaryColorId = R.color.colorTimemaniaText
                chevronUp = R.drawable.icon_chevron_up_timemania
                chevronDown = R.drawable.icon_chevron_down_timemania
                imageViewClover.setImageResource(R.drawable.icon_clover_timemania)
            } else {
                numberAdapter = NumberAdapter(type.color, numbers)
                primaryColorId = android.R.color.white
                secondaryColorId = type.color
                chevronUp = R.drawable.icon_chevron_up
                chevronDown = R.drawable.icon_chevron_down
                imageViewClover.setImageResource(R.drawable.icon_clover)
            }

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

            recyclerViewNumbers.adapter = numberAdapter
            recyclerViewNumbers.layoutManager = getFlexBoxLayoutManager()

            layoutHeader.setOnClickListener {
                onHeaderClick(adapterPosition)
            }
        }
    }

    fun showOrHideExpandableLayout(show: Boolean) {
        with(binding) {
            if (show) {
                imageViewChevron.setImageResource(chevronUp)
                expandableLayout.visible()
            } else {
                imageViewChevron.setImageResource(chevronDown)
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