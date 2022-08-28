package com.tiagoalmeida.lottery.ui.detail

import com.tiagoalmeida.lottery.R

class DetailGameLoading : BaseDetailGame {
    companion object {
        const val LAYOUT_ID = R.layout.adapter_loading
    }
    override fun getLayoutId(): Int = LAYOUT_ID
}