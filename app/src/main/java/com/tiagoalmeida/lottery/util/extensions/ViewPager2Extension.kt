package com.tiagoalmeida.lottery.util.extensions

import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.onPageChanged(method: (Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            method(position)
        }
    })
}