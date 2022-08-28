package com.tiagoalmeida.lottery.ui.games

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.extensions.dp

class GamesItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (isOnTop(view, parent)) {
                top = 5.dp
            }
            if (!isEven(view, parent)) {
                left = 10.dp
            }
            if (isEven(view, parent)) {
                right = 10.dp
            }
            bottom = 5.dp
        }
    }

    private fun isOnTop(view: View, parent: RecyclerView): Boolean =
        parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1

    private fun isEven(view: View, parent: RecyclerView): Boolean =
        parent.getChildAdapterPosition(view) % 2 == 0

}