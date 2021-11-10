package com.tiagoalmeida.lottery.ui.register.game

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.FragmentRegisterContestBinding
import com.tiagoalmeida.lottery.ui.adapter.LotteryTypeAdapter
import com.tiagoalmeida.lottery.util.enums.LotteryType
import com.tiagoalmeida.lottery.util.extensions.*
import com.tiagoalmeida.lottery.viewmodel.register.game.GameRegisterState
import com.tiagoalmeida.lottery.viewmodel.register.game.GameRegisterViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GameRegisterContestFragment : Fragment(R.layout.fragment_register_contest) {

    private val binding by viewBinding(FragmentRegisterContestBinding::bind)

    private val viewModel: GameRegisterViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeEvents()
        initializeObservers()
        initializeUI()
    }

    private fun initializeObservers() = with(viewModel) {
        viewState.observe(viewLifecycleOwner, observeViewState())
        validForAllFutureContests.observe(viewLifecycleOwner, observeValidForAllFutureContests())
        singleGame.observe(viewLifecycleOwner, observeSingleGame())
    }

    private fun initializeEvents() {
        with(binding) {
            buttonBack.setOnClickListener {
                activity?.finish()
                activity?.overridePendingTransition(R.anim.stay, R.anim.slide_top_to_bottom)
            }
            buttonNext.setOnClickListener {
                clearErrors()
                hideKeyboard()
                viewModel.validateLotteryType(binding.spinnerLotteryType.getLotteryType())
            }
            checkBoxUnlimitedContests.setOnClickListener {
                hideKeyboard()
            }
        }
    }

    private fun initializeUI() = with(binding) {
        gameRegisterViewModel = viewModel
        spinnerLotteryType.setAdapter(getSpinnerAdapter())
    }

    private fun getSpinnerAdapter(): ArrayAdapter<LotteryType> {
        return LotteryTypeAdapter(
            requireContext(),
            R.layout.spinner_item,
            LotteryType.values()
        )
    }

    private fun observeViewState() = Observer<GameRegisterState> { state ->
        when (state) {
            is GameRegisterState.LotteryTypeInvalid -> setErrorInSpinnerContest()
            is GameRegisterState.LotteryTypeOk -> viewModel.validateContest()
            is GameRegisterState.ContestWithError -> handleError(state.messageId)
            is GameRegisterState.ContestOk -> navigate(R.id.contest_to_numbers_fragment)
        }
    }

    private fun observeValidForAllFutureContests() = Observer<Boolean> { hide ->
        with(binding) {
            if (hide) {
                checkBoxSingleGame.gone()
                inputLayoutEndContestNumber.gone()
                editTextEndContestNumber.text?.clear()
            } else {
                checkBoxSingleGame.visible()
                inputLayoutEndContestNumber.visible()
            }
        }
    }

    private fun observeSingleGame() = Observer<Boolean> { hide ->
        with(binding) {
            if (hide) {
                checkBoxUnlimitedContests.gone()
                inputLayoutEndContestNumber.gone()
                editTextEndContestNumber.text?.clear()
            } else {
                checkBoxUnlimitedContests.visible()
                inputLayoutEndContestNumber.visible()
            }
        }
    }

    private fun setErrorInSpinnerContest() = with(binding) {
        inputLayoutSpinner.isErrorEnabled = true
        inputLayoutSpinner.error = getString(R.string.game_register_error_contest_not_selected)
    }

    private fun handleError(messageId: Int) {
        when {
            isMessageFromStartContest(messageId) -> {
                binding.inputLayoutStartContestNumber.isErrorEnabled = true
                binding.inputLayoutStartContestNumber.error = getString(messageId)
            }
            isMessageFromEndContest(messageId) -> {
                binding.inputLayoutEndContestNumber.isErrorEnabled = true
                binding.inputLayoutEndContestNumber.error = getString(messageId)
            }
            else -> showToast(messageId)
        }
    }

    private fun isMessageFromStartContest(messageId: Int): Boolean {
        return messageId == R.string.game_register_error_start_contest_empty ||
                messageId == R.string.game_register_error_start_bigger_than_end
    }

    private fun isMessageFromEndContest(messageId: Int): Boolean {
        return messageId == R.string.game_register_error_end_contest_empty
    }

    private fun clearErrors() = with(binding) {
        inputLayoutSpinner.isErrorEnabled = false
        inputLayoutStartContestNumber.isErrorEnabled = false
        inputLayoutEndContestNumber.isErrorEnabled = false
    }
}
