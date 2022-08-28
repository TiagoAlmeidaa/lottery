package com.tiagoalmeida.lottery.ui.detail

import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.databinding.AdapterLoadingBinding

class DetailGameLoadingViewHolder(
    val binding: AdapterLoadingBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(colorId: Int) = with(binding) {
        val color = ContextCompat.getColor(root.context, colorId)
        progressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }
}