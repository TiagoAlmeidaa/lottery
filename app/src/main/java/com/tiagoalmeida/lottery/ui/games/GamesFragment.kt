package com.tiagoalmeida.lottery.ui.games

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.FragmentGamesBinding
import com.tiagoalmeida.lottery.model.vo.GamesFilter
import com.tiagoalmeida.lottery.model.vo.UserGame
import com.tiagoalmeida.lottery.ui.detail.DetailGameActivity
import com.tiagoalmeida.lottery.ui.games.adapter.GamesAdapter
import com.tiagoalmeida.lottery.ui.games.adapter.listener.GamesAdapterEvents
import com.tiagoalmeida.lottery.ui.register.game.GameRegisterActivity
import com.tiagoalmeida.lottery.util.Constants
import com.tiagoalmeida.lottery.util.buildRemoveGameDialog
import com.tiagoalmeida.lottery.util.decoration.GamesItemDecoration
import com.tiagoalmeida.lottery.util.extensions.gone
import com.tiagoalmeida.lottery.util.extensions.showToast
import com.tiagoalmeida.lottery.util.extensions.visible
import com.tiagoalmeida.lottery.viewmodel.games.GamesState
import com.tiagoalmeida.lottery.viewmodel.games.GamesViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GamesFragment : Fragment(), GamesAdapterEvents {

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!

    private val gamesViewModel: GamesViewModel by sharedViewModel()

    private val gamesAdapter: GamesAdapter by lazy { GamesAdapter(this) }

    private var bottomSheet: BottomSheetDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamesBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_NEW_GAME) {
                gamesViewModel.findGames()
            } else if (requestCode == Constants.REQUEST_CODE_DETAIL_GAME) {
                data?.extras?.getString(Constants.BUNDLE_GAME_REMOVED_JSON)?.let { json ->
                    gamesViewModel.removeGame(
                        Gson().fromJson(json, UserGame::class.java)
                    )
                }
            }
        }
    }

    override fun onGameClicked(userGame: UserGame, view: View) {
        val intent = Intent(context, DetailGameActivity::class.java).apply {
            putExtra(Constants.BUNDLE_GAME_JSON, userGame.toJson())
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair.create(view, view.transitionName)
        )

        startActivityForResult(
            intent,
            Constants.REQUEST_CODE_DETAIL_GAME,
            options.toBundle()
        )
    }

    override fun onGameLongClicked(userGame: UserGame): Boolean {
        buildRemoveGameDialog(requireContext(), view?.findViewById(android.R.id.content)) {
            gamesViewModel.removeGame(userGame)
        }.show()
        return true
    }

    private fun initializeEvents() {
        with(binding.layoutAddGame) {
            cardAddGame.setOnClickListener {
                requestNewGame()
            }
            cardFilter.setOnClickListener {
                bottomSheet = GamesFilterBottomSheet(gamesViewModel)
                bottomSheet?.show(parentFragmentManager, Constants.BOTTOM_SHEET_FILTER_ID)
            }
        }
    }

    private fun initializeObservers() {
        with(gamesViewModel) {
            loading.observe(viewLifecycleOwner, observeLoading())
            viewState.observe(viewLifecycleOwner, observeViewState())
            gamesFilter.observe(viewLifecycleOwner, observeFilter())
        }
    }

    private fun initializeUI() = with(binding) {
        recyclerViewGames.addItemDecoration(GamesItemDecoration())
        recyclerViewGames.adapter = gamesAdapter

        gamesViewModel.findGames()
    }

    private fun observeLoading() = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                coordinatorLayout.gone()
                progressBar.visible()
            } else {
                coordinatorLayout.visible()
                progressBar.gone()
            }
        }
    }

    private fun observeFilter() = Observer<GamesFilter> { filter ->
        bottomSheet?.dismissAllowingStateLoss()
        bottomSheet = null

        with(binding) {
            gamesViewModel.createFilterMessage(filter).let { filterMessage ->
                if (filterMessage.first.isEmpty() && filterMessage.second.isEmpty()) {
                    textViewFilter.gone()
                } else {
                    textViewFilter.visible()
                    textViewFilter.text =
                        if (filterMessage.first.isNotEmpty() && filterMessage.second.isNotEmpty()) {
                            val formattedString = filterMessage.first + "(${filterMessage.second})"
                            String.format(getString(R.string.filter_text), formattedString)
                        } else if (filterMessage.first.isNotEmpty()) {
                            String.format(getString(R.string.filter_text), filterMessage.first)
                        } else if (filterMessage.second.isNotEmpty()) {
                            String.format(getString(R.string.filter_text), filterMessage.second)
                        } else {
                            ""
                        }
                }

            }
        }

        gamesViewModel.findGames()
    }

    private fun observeViewState() = Observer<GamesState> { state ->
        when (state) {
            is GamesState.GamesReceived -> gamesReceived(state.games)
            is GamesState.GameRemoved -> gamesViewModel.findGames()
            is GamesState.Timeout -> showToast(R.string.games_removing_timeout)
            is GamesState.InternalError -> showToast(R.string.games_removing_internal_error)
        }
    }

    private fun gamesReceived(games: List<UserGame>) {
        with(binding) {
            if (games.isEmpty())
                recyclerViewGames.gone()
            else
                recyclerViewGames.visible()
        }

        gamesAdapter.addGames(games)
    }

    private fun requestNewGame() {
        val intent = Intent(requireContext(), GameRegisterActivity::class.java)

        startActivityForResult(intent, Constants.REQUEST_CODE_NEW_GAME)
        activity?.overridePendingTransition(R.anim.slide_bottom_to_top, R.anim.stay)
    }

}