package com.tiagoalmeida.lottery.ui.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.databinding.AdapterGridNumberBinding
import com.tiagoalmeida.lottery.data.model.LotteryType

class GridNumbersAdapter(
    private val lotteryType: LotteryType,
    private val listener: GridNumberPickedEvents
) : RecyclerView.Adapter<GridNumbersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridNumbersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterGridNumberBinding.inflate(inflater, parent, false)

        return GridNumbersViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: GridNumbersViewHolder, position: Int) {
        val number = if (lotteryType == LotteryType.LOTOMANIA) position else position + 1
        holder.bind(number)
    }

    override fun getItemCount(): Int = lotteryType.total

}