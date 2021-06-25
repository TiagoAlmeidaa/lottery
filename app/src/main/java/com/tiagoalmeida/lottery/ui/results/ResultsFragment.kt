package com.tiagoalmeida.lottery.ui.results

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.FragmentResultsBinding
import com.tiagoalmeida.lottery.model.vo.LotteryResult
import com.tiagoalmeida.lottery.ui.results.adapter.ResultsAdapter
import com.tiagoalmeida.lottery.util.decoration.ResultsItemDecoration
import com.tiagoalmeida.lottery.util.extensions.gone
import com.tiagoalmeida.lottery.util.extensions.visible
import com.tiagoalmeida.lottery.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ResultsFragment : Fragment(R.layout.fragment_results) {

    private val binding by viewBinding(FragmentResultsBinding::bind)

    private val mainViewModel: MainViewModel by sharedViewModel()

    private val resultsAdapter: ResultsAdapter by lazy { ResultsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeEvents()
        initializeObservers()
        initializeUI()
    }

    private fun initializeEvents() {
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                mainViewModel.consultLastResults()
            }
        }
    }

    private fun initializeObservers() {
        with(mainViewModel) {
            results.observe(viewLifecycleOwner, observeResults())
            loading.observe(viewLifecycleOwner, observeLoading())
        }
    }

    private fun observeLoading() = Observer<Boolean> { isLoading ->
        with(binding) {
            swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun initializeUI() {
        with(binding) {
            recyclerViewResults.adapter = resultsAdapter
            recyclerViewResults.addItemDecoration(ResultsItemDecoration())
        }
    }

    private fun observeResults() = Observer<List<LotteryResult>> { results ->
        resultsAdapter.addResults(results)
        mainViewModel.updateLastGamesContestNumbers(results)
        showOrHideEmpty(results.isEmpty())
    }

    private fun showOrHideEmpty(show: Boolean) {
        with(binding) {
            if (show) {
                imageViewEmpty.visible()
                textViewEmpty.visible()
            } else {
                imageViewEmpty.gone()
                textViewEmpty.gone()
            }
        }
    }
}
