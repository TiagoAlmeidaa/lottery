package com.tiagoalmeida.lottery.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.databinding.ActivityDetailGameBinding
import com.tiagoalmeida.lottery.extensions.invisible
import com.tiagoalmeida.lottery.extensions.onBottomReached
import com.tiagoalmeida.lottery.extensions.visible
import com.tiagoalmeida.lottery.ui.common.NumberAdapter
import com.tiagoalmeida.lottery.util.Constants
import com.tiagoalmeida.lottery.util.applyWindowInsets
import com.tiagoalmeida.lottery.util.buildFilterGameDialog
import com.tiagoalmeida.lottery.util.buildRemoveGameDialog
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailGameActivity : AppCompatActivity() {

    private var _binding: ActivityDetailGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailGameViewModel by viewModel()

    private val adapter: DetailGameAdapter by lazy {
        DetailGameAdapter(viewModel.getUserGame())
    }

    private val crashlytics: FirebaseCrashlytics by lazy { get() }
    private val analytics: FirebaseAnalytics by lazy { get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailGameBinding.inflate(layoutInflater)
        binding.root.applyWindowInsets()
        setContentView(binding.root)

        val json = getUserGameJson()
        try {
            val userGame = Gson().fromJson(json, UserGame::class.java)
            viewModel.start(userGame)

            initializeEvents()
            initializeObservers()
            initializeUI()
        } catch (e: Exception) {
            crashlytics.log("User game json: $json")
            crashlytics.recordException(e)
            Toast.makeText(this, getString(R.string.detail_game_user_game_not_found), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getUserGameJson(): String = intent.getStringExtra(Constants.BUNDLE_GAME_JSON) ?: ""

    private fun initializeEvents() {
        with(binding) {
            buttonCompare.setOnClickListener {
                analytics.logEvent("clicked_details_compare", null)
                showFilterDialog()
            }
            imageViewBack.setOnClickListener {
                onBackPressed()
            }
            imageViewDelete.setOnClickListener {
                analytics.logEvent("clicked_details_remove", null)
                showDeleteDialog()
            }
            buttonClearFilter.setOnClickListener {
                analytics.logEvent("clicked_details_clear_filter", null)
                buttonClearFilter.invisible()
                adapter.clear()
                viewModel.resetLastDownloadedContestNumber()
                viewModel.consultContestsForGame()
            }
            recyclerViewContests.onBottomReached {
                if (!viewModel.isLoading()) {
                    analytics.logEvent("details_loading_more_items", null)
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
        window.statusBarColor = ContextCompat.getColor(this, viewModel.getColorId())
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        with(binding) {
            userGame = viewModel.getUserGame()
            textViewContestNumber.text = viewModel.getContestNumber()

            recyclerViewNumbers.adapter = NumberAdapter(
                viewModel.getColorId(),
                viewModel.getNumbers(),
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
            analytics.logEvent("details_comparing_with") {
                param("contest", contest)
            }
            if (contest.isNotEmpty()) {
                val contestNumber = contest.toInt()
                if (contestNumber > 0) {
                    viewModel.consultContest(contestNumber)
                }
            }
        }.show()
    }

}
