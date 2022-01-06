package com.tiagoalmeida.lottery.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.databinding.AdapterNumbersBinding
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.extensions.toStringNumber

class NumberMatchedAdapter(
    private val userGame: UserGame,
    private val numbers: List<Int>
) : RecyclerView.Adapter<NumberMatchedAdapter.NumberViewHolder>() {

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

        fun bind(number: Int) {
            val colorId = if (userGame.numbers.contains(number)) {
                userGame.type.primaryColor
            } else {
                android.R.color.darker_gray
            }
            val color = ContextCompat.getColor(binding.root.context, colorId)

            binding.root.background.setTint(color)
            binding.number = number.toStringNumber()
        }
    }
}
