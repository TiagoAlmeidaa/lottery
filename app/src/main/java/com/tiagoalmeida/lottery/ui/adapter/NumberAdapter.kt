package com.tiagoalmeida.lottery.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.databinding.AdapterNumbersBinding
import com.tiagoalmeida.lottery.util.extensions.toStringNumber

class NumberAdapter(
    private val colorId: Int,
    private val numbers: List<Int>,
    private val invertColors: Boolean = false,
    private val textColor: Int = android.R.color.white
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

        fun bind(numberReceived: Int) {
            with(binding) {
                val textColor = ContextCompat.getColor(root.context, getTextColor())
                val backgroundColor = ContextCompat.getColor(root.context, getBackgroundColor())

                root.background.setTint(backgroundColor)
                textViewNumber.setTextColor(textColor)
                number = numberReceived.toStringNumber()
            }
        }

        private fun getTextColor(): Int {
            return if (invertColors) {
                colorId
            } else {
                textColor
            }
        }

        private fun getBackgroundColor(): Int {
            return if (invertColors) {
                textColor
            } else {
                colorId
            }
        }
    }

}
