package com.tiagoalmeida.lottery.ui.detail.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.databinding.AdapterDetailGameGhostBinding
import com.tiagoalmeida.lottery.util.extensions.gone

class DetailGameGhostViewHolder(
    val binding: AdapterDetailGameGhostBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int) {
        if (position > 0) {
            binding.textViewGhostTitle.gone()
        }
    }
}