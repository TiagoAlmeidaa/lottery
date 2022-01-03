package com.tiagoalmeida.lottery.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tiagoalmeida.lottery.databinding.ActivityDetailGameBinding
import com.tiagoalmeida.lottery.model.mapper.UserGameParser
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.ui.adapter.NumberAdapter
import com.tiagoalmeida.lottery.ui.detail.adapter.DetailGameAdapter
import com.tiagoalmeida.lottery.util.Constants
import com.tiagoalmeida.lottery.util.buildFilterGameDialog
import com.tiagoalmeida.lottery.util.buildRemoveGameDialog
import com.tiagoalmeida.lottery.util.extensions.invisible
import com.tiagoalmeida.lottery.util.extensions.onBottomReached
import com.tiagoalmeida.lottery.util.extensions.visible
import com.tiagoalmeida.lottery.viewmodel.detail.DetailGameState
import com.tiagoalmeida.lottery.viewmodel.detail.DetailGameViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailGameActivity : AppCompatActivity() {

    private var _binding: ActivityDetailGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailGameViewModel by viewModel(parameters = {
        parametersOf(UserGameParser.from(getUserGameJson()))
    })

    private val adapter: DetailGameAdapter by lazy {
        DetailGameAdapter(viewModel.getUserGame())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeEvents()
        initializeObservers()
        initializeUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getUserGameJson(): String = intent.getStringExtra(Constants.BUNDLE_GAME_JSON) ?: ""

    private fun initializeEvents() {
        with(binding) {
            buttonCompare.setOnClickListener {
                showFilterDialog()
            }
            imageViewBack.setOnClickListener {
                onBackPressed()
            }
            imageViewDelete.setOnClickListener {
                showDeleteDialog()
            }
            buttonClearFilter.setOnClickListener {
                buttonClearFilter.invisible()
                adapter.clear()
                viewModel.resetLastDownloadedContestNumber()
                viewModel.consultContestsForGame()
            }
            recyclerViewContests.onBottomReached {
                if (!viewModel.isLoading()) {
                    viewModel.consultNextPage()
                }
            }
        }
    }

    private fun initializeObservers() {
        with(viewModel) {
            loading.observe(this@DetailGameActivity, observeLoading())
            viewState.observe(this@DetailGameActivity, observeViewState())
        }
    }

    private fun initializeUI() {
        val type = viewModel.getLotteryType()
        window.statusBarColor = ContextCompat.getColor(this, type.primaryColor)

        with(binding) {
            userGame = viewModel.getUserGame()
            textViewContestNumber.text = viewModel.getContestNumber()

            recyclerViewNumbers.adapter = NumberAdapter(
                viewModel.getNumbers(),
                type,
                true
            )
            recyclerViewNumbers.layoutManager = getFlexBoxLayoutManager()

            recyclerViewContests.adapter = adapter
        }

        viewModel.consultContestsForGame()
    }

    private fun observeLoading() = Observer<Boolean> { isLoading ->
        if (isLoading) {
            adapter.showLoading()
        } else if (!binding.recyclerViewContests.isVisible) {
            binding.recyclerViewContests.visible()
        }
    }

    private fun observeViewState() = Observer<DetailGameState> { state ->
        when (state) {
            is DetailGameState.ContestsReceived -> adapter.addAll(state.results)
            is DetailGameState.ContestFiltered -> checkFilter(result = state.lotteryResult)
            is DetailGameState.ContestNotFound -> checkFilterNotFound()
            is DetailGameState.NoResultsYet -> adapter.showEmpty()
        }
    }

    private fun checkFilter(result: LotteryResult) {
        binding.buttonClearFilter.visible()
        adapter.clear()
        adapter.add(result)
    }

    private fun checkFilterNotFound() {
        binding.buttonClearFilter.visible()
        adapter.showEmpty()
    }

    private fun getFlexBoxLayoutManager() = FlexboxLayoutManager(this).apply {
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.CENTER
    }

    private fun showDeleteDialog() {
        buildRemoveGameDialog(this, findViewById(android.R.id.content)) {
            val intent = Intent().apply {
                putExtra(Constants.BUNDLE_GAME_REMOVED_JSON, viewModel.getUserGame().toJson())
            }
            setResult(RESULT_OK, intent)
            finish()
        }.show()
    }

    private fun showFilterDialog() {
        buildFilterGameDialog(this, findViewById(android.R.id.content)) { contest ->
            if (contest.isNotEmpty()) {
                val contestNumber = contest.toInt()
                if (contestNumber > 0) {
                    viewModel.consultContest(contestNumber)
                }
            }
        }.show()
    }

}
