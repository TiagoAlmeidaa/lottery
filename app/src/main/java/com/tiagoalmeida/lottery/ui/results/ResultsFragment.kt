package com.tiagoalmeida.lottery.ui.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tiagoalmeida.lottery.data.model.LotteryResult
import com.tiagoalmeida.lottery.databinding.FragmentResultsBinding
import com.tiagoalmeida.lottery.extensions.gone
import com.tiagoalmeida.lottery.extensions.visible
import com.tiagoalmeida.lottery.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModel()

    private val resultsAdapter: ResultsAdapter by lazy { ResultsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ResultsFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeEvents()
        initializeObservers()
        initializeUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
