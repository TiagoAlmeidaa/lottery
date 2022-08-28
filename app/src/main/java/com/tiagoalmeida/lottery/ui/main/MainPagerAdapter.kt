package com.tiagoalmeida.lottery.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tiagoalmeida.lottery.ui.games.GamesFragment
import com.tiagoalmeida.lottery.ui.results.ResultsFragment

class MainPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when(position) {
        0 -> ResultsFragment()
        else -> GamesFragment()
    }
}