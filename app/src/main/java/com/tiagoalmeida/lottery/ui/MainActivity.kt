package com.tiagoalmeida.lottery.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tiagoalmeida.lottery.BuildConfig
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.ActivityMainBinding
import com.tiagoalmeida.lottery.ui.adapter.MainPagerAdapter
import com.tiagoalmeida.lottery.util.extensions.onPageChanged
import com.tiagoalmeida.lottery.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        initializeEvents()
        initializeUI()
    }

    private fun initializeEvents() {
        with(binding) {
            pager.onPageChanged { position ->
                when (position) {
                    0 -> setSelectedButton(results = true)
                    1 -> setSelectedButton(games = true)
                }
            }
            buttonResults.setOnClickListener {
                if (binding.buttonGames.isSelected)
                    binding.pager.setCurrentItem(0, true)
            }
            buttonGames.setOnClickListener {
                if (binding.buttonResults.isSelected)
                    binding.pager.setCurrentItem(1, true)
            }
            textViewVersion.text = String.format(
                getString(R.string.version),
                BuildConfig.VERSION_NAME
            )
        }
    }

    private fun initializeUI() {
        binding.pager.adapter = MainPagerAdapter(this@MainActivity)

        with(mainViewModel) {
            consultLastResults()
        }
    }

    private fun setSelectedButton(results: Boolean = false, games: Boolean = false) {
        with(binding) {
            buttonResults.isSelected = results
            buttonGames.isSelected = games
        }
    }
}
