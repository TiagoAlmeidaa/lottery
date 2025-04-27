package com.tiagoalmeida.lottery.ui.register

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.FragmentRegisterNumbersBinding
import com.tiagoalmeida.lottery.extensions.gone
import com.tiagoalmeida.lottery.extensions.toStringNumber
import com.tiagoalmeida.lottery.extensions.visible
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class GameRegisterNumbersFragment : Fragment(), GridNumberPickedEvents {

    private var _binding: FragmentRegisterNumbersBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GameRegisterViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterNumbersBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@GameRegisterNumbersFragment
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

    override fun onNumberPicked(number: Int) {
        sharedViewModel.onNumberPicked(number)
        updateSelectedCounter()
    }

    override fun hasNumber(number: Int): Boolean {
        return sharedViewModel.containsNumber(number)
    }

    private fun initializeEvents() {
        with(binding) {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonSave.setOnClickListener {
                sharedViewModel.saveGame()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            getOnBackPressedCallback()
        )
    }

    private fun initializeObservers() {
        with(sharedViewModel) {
            viewState.observe(viewLifecycleOwner, observeViewState())
            loading.observe(viewLifecycleOwner, observeLoading())
        }
    }

    private fun initializeUI() {
        with(binding) {
            minimum = sharedViewModel.getLotteryType().minimum
            maximum = sharedViewModel.getLotteryType().maximum

            gridViewNumbers.adapter = GridNumbersAdapter(
                sharedViewModel.getLotteryType(),
                this@GameRegisterNumbersFragment
            )

            buttonSave.isEnabled = false
        }

        sharedViewModel.clearNumbers()
        updateSelectedCounter()
    }

    private fun getOnBackPressedCallback(): OnBackPressedCallback {
        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                _binding?.let {
                    if (!it.loadingOrErrorView.isVisible) {
                        findNavController().popBackStack()
                    }
                } ?: kotlin.run {
                    val exception = Throwable("GameRegisterNumbersFragment onBackPressed failed because binding is null")
                    FirebaseCrashlytics.getInstance().recordException(exception)
                }
            }
        }
    }

    private fun observeViewState() = Observer<GameRegisterState> { state ->
        when (state) {
            is GameRegisterState.GameUpdated -> finishProcess()
            is GameRegisterState.OnNumberPicked -> updateUiOnNumberPicked(state)
            else -> {}
        }
    }

    private fun observeLoading() = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                loadingOrErrorView.visible()
                loadingOrErrorView.setLoading()
            } else {
                loadingOrErrorView.gone()
            }
        }
    }

    private fun finishProcess() {
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
        activity?.overridePendingTransition(R.anim.stay, R.anim.slide_top_to_bottom)
    }

    private fun updateSelectedCounter() {
        binding.textViewSelected.text = sharedViewModel.getNumbersCount().toStringNumber()
    }

    private fun updateUiOnNumberPicked(state: GameRegisterState.OnNumberPicked) = with(binding) {
        textViewMessage.text = getString(state.messageId)
        cardViewMessage.setCardBackgroundColor(ContextCompat.getColor(requireContext(), state.messageColorId))
        buttonSave.isEnabled = state.isSaveButtonEnabled
    }
}
