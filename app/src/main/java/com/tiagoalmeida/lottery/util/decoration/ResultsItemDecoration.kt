package com.tiagoalmeida.lottery.util.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.util.extensions.dp

class ResultsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (isOnTop(view,parent)) {
                top = 5.dp
            }
            left = 10.dp
            right = 10.dp
            bottom = 5.dp
        }
    }

    private fun isOnTop(view: View, parent: RecyclerView): Boolean =
        parent.getChildAdapterPosition(view) == 0
}