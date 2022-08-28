package com.tiagoalmeida.lottery.ui.detail

import com.tiagoalmeida.lottery.R

class DetailGameEmpty : BaseDetailGame {
    companion object {
        const val LAYOUT_ID = R.layout.adapter_detail_game_ghost
    }
    override fun getLayoutId(): Int = LAYOUT_ID
}