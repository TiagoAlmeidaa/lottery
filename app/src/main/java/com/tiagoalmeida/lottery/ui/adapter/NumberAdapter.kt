package com.tiagoalmeida.lottery.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.databinding.AdapterNumbersBinding
import com.tiagoalmeida.lottery.util.enums.LotteryType
import com.tiagoalmeida.lottery.util.extensions.toStringNumber

class NumberAdapter(
    private val numbers: List<Int>,
    private val lotteryType: LotteryType,
    private val invertColors: Boolean = false
) : RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterNumbersBinding.inflate(inflater, parent, false)
        return NumberViewHolder(binding)
    }

    override fun getItemCount(): Int = numbers.size

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.bind(numbers[position])
    }

    inner class NumberViewHolder(val binding: AdapterNumbersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(numberReceived: Int) = with(binding) {
            primaryColorId = if (invertColors) {
                lotteryType.secondaryColor
            } else {
                lotteryType.primaryColor
            }
            secondaryColorId = if (invertColors) {
                lotteryType.primaryColor
            } else {
                lotteryType.secondaryColor
            }
            lotteryNumber = numberReceived.toStringNumber()
        }
    }

}
