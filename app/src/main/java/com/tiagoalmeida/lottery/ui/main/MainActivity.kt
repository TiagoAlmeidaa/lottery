package com.tiagoalmeida.lottery.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tiagoalmeida.lottery.BuildConfig
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.ActivityMainBinding
import com.tiagoalmeida.lottery.extensions.onPageChanged
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTheme(R.style.AppTheme)

        initializeEvents()
        initializeUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
            textViewVersion.text = BuildConfig.VERSION_NAME
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
