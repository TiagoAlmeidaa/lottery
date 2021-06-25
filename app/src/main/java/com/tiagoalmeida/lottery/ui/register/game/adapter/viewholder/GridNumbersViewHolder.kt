package com.tiagoalmeida.lottery.ui.register.game.adapter.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.AdapterGridNumberBinding
import com.tiagoalmeida.lottery.ui.register.game.adapter.events.GridNumberPickedEvents
import com.tiagoalmeida.lottery.util.extensions.toStringNumber

class GridNumbersViewHolder(
    private val binding: AdapterGridNumberBinding,
    private val listener: GridNumberPickedEvents
) : RecyclerView.ViewHolder(binding.root) {

    private val defaultTextColor = binding.textViewGridNumber.textColors

    fun bind(number: Int) {
        checkBackgroundColor(number)

        binding.root.setOnClickListener {
            listener.onNumberPicked(number)
            checkBackgroundColor(number)
        }

        binding.number = number.toStringNumber()
    }

    private fun checkBackgroundColor(number: Int) {
        if (listener.hasNumber(number))
            setSelectedColorInView()
        else
            setDefaultColorsInViews()
    }

    private fun setDefaultColorsInViews() {
        with(binding) {
            defaultTextColor?.let { color ->
                textViewGridNumber.setTextColor(color)
                root.background.setTint(getColor(android.R.color.white))
            }
        }
    }

    private fun setSelectedColorInView() {
        with(binding) {
            root.background.setTint(getColor(R.color.colorAccent))
            textViewGridNumber.setTextColor(getColor(android.R.color.white))
        }
    }

    private fun getColor(colorId: Int): Int = ContextCompat.getColor(
        binding.textViewGridNumber.context,
        colorId
    )

}