package com.tiagoalmeida.lottery.ui.common

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class LotteryTypeAdapter<String>(
    context: Context,
    layout: Int,
    var items: Array<String>
) : ArrayAdapter<String>(context, layout, items) {

    private val filterThatDoesNothing = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = items
                count = items.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = filterThatDoesNothing
}