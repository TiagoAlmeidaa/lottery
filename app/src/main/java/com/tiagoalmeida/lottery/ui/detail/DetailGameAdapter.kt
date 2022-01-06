package com.tiagoalmeida.lottery.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.databinding.AdapterDetailGameBinding
import com.tiagoalmeida.lottery.databinding.AdapterDetailGameGhostBinding
import com.tiagoalmeida.lottery.databinding.AdapterLoadingBinding

class DetailGameAdapter(
    private val userGame: UserGame
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val results: MutableList<BaseDetailGame> = mutableListOf()

    private var expandedItemsMapper = resetExpanded()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LotteryResult.LAYOUT_ID -> {
                val binding = AdapterDetailGameBinding.inflate(inflater, parent, false)
                DetailGameViewHolder(binding, userGame)
            }
            DetailGameLoading.LAYOUT_ID -> {
                val binding = AdapterLoadingBinding.inflate(inflater, parent, false)
                DetailGameLoadingViewHolder(binding)
            }
            DetailGameEmpty.LAYOUT_ID -> {
                val binding = AdapterDetailGameGhostBinding.inflate(inflater, parent, false)
                DetailGameGhostViewHolder(binding)
            }
            else -> throw Exception("viewType not supported")
        }
    }

    override fun getItemCount(): Int = results.size

    override fun getItemViewType(position: Int): Int = results[position].getLayoutId()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailGameViewHolder -> {
                val lotteryResult = results[position] as LotteryResult
                holder.bind(lotteryResult)
                holder.showOrHideExpandableLayout(expandedItemsMapper[position])
                holder.binding.layoutHeader.setOnClickListener {
                    closeOtherItems(position)
                    changeItemClickedVisibility(position)
                }
            }
            is DetailGameLoadingViewHolder -> holder.bind(userGame.type.primaryColor)
            is DetailGameGhostViewHolder -> holder.bind(position)
        }
    }

    fun clear() {
        results.clear()
        update()
    }

    fun add(contest: LotteryResult) {
        results.add(contest)
        update()
    }

    fun addAll(contests: List<LotteryResult>) {
        results.removeAll { it is DetailGameEmpty }
        results.removeAll { it is DetailGameLoading }
        results.addAll(contests)
        update()
    }

    fun showLoading() {
        results.add(DetailGameLoading())
        update()
    }

    fun showEmpty() {
        results.clear()
        results.add(DetailGameEmpty())
        results.add(DetailGameEmpty())
        results.add(DetailGameEmpty())
        update()
    }

    private fun update() {
        expandedItemsMapper = resetExpanded()
        notifyDataSetChanged()
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

}
