package com.tiagoalmeida.lottery.ui.detail

import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.databinding.AdapterDetailGameGhostBinding
import com.tiagoalmeida.lottery.extensions.gone

class DetailGameGhostViewHolder(
    val binding: AdapterDetailGameGhostBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int) {
        if (position > 0) {
            binding.textViewGhostTitle.gone()
        }
    }
}