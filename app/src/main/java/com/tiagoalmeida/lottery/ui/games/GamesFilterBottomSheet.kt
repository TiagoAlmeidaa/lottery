package com.tiagoalmeida.lottery.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.BottomSheetGamesFilterBinding
import com.tiagoalmeida.lottery.ui.common.LotteryTypeAdapter
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.extensions.getLotteryType
import com.tiagoalmeida.lottery.extensions.hideKeyboard

class GamesFilterBottomSheet(
    private val sharedViewModel: GamesViewModel // TODO porque não tá funcionando com Koin? sharedViewModel
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetGamesFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetGamesFilterBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeEvents()
        initializeUI()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initializeEvents() {
        with(binding) {
            buttonFilter.setOnClickListener {
                hideKeyboard()
                setFilter()
            }
            buttonClearFilter.setOnClickListener {
                hideKeyboard()
                clearFilter()
            }
        }
    }

    private fun initializeUI() {
        expandBottomSheet()
        populateFields()
    }

    private fun expandBottomSheet() {
        val bottom = dialog as BottomSheetDialog
        bottom.setOnShowListener {
            bottom.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.apply {
                BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
    private fun populateFields() = with(binding) {
        spinnerLotteryType.setAdapter(getSpinnerAdapter())

        with(sharedViewModel.getFilter()) {
            if (lotteryType != null) {
                spinnerLotteryType.setText(lotteryType.toString())
            }
            if (contestNumber.isNotEmpty()) {
                editTextContestNumber.setText(contestNumber)
            }
            checkBoxHideOldGames.isChecked = hideOldNumbers
        }
    }

    private fun setFilter() = with(binding) {
        val type = spinnerLotteryType.getLotteryType()
        val number = editTextContestNumber.text?.toString() ?: ""
        val hideOldNumbers = checkBoxHideOldGames.isChecked

        sharedViewModel.setFilter(GamesFilter(type, number, hideOldNumbers))
    }

    private fun clearFilter() {
        sharedViewModel.setFilter(GamesFilter())
    }

    private fun getSpinnerAdapter(): ArrayAdapter<LotteryType> {
        return LotteryTypeAdapter(
            requireContext(),
            R.layout.spinner_item,
            LotteryType.values()
        )
    }

}