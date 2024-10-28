package com.tiagoalmeida.lottery.ui.results

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.databinding.AdapterResultsBinding

class ResultsAdapter : RecyclerView.Adapter<ResultsViewHolder>() {

    private val results: MutableList<LotteryResult> = mutableListOf()
    private var expandedItemsMapper = resetExpanded()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterResultsBinding.inflate(inflater, parent, false)

        return ResultsViewHolder(binding)
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        with(holder) {
            bind(results[position]) { adapterPosition ->
                closeOtherItems(adapterPosition)
                changeItemClickedVisibility(adapterPosition)
            }
            showOrHideExpandableLayout(expandedItemsMapper[position])
        }
    }

    private fun changeItemClickedVisibility(position: Int) {
        expandedItemsMapper[position] = !expandedItemsMapper[position]
        notifyItemChanged(position)
    }

    private fun closeOtherItems(position: Int) {
        val index = expandedItemsMapper.indexOfFirst { it }
        if (index >= 0 && index != position) {
            expandedItemsMapper = resetExpanded()
            notifyItemChanged(index)
        }
    }

    private fun resetExpanded(): MutableList<Boolean> {
        return mutableListOf<Boolean>().apply {
            repeat(itemCount) {
                add(false)
            }
        }
    }

    fun addResults(list: List<LotteryResult>) {
        results.clear()
        results.addAll(list)

        notifyDataSetChanged()

        expandedItemsMapper = resetExpanded()
    }

}
