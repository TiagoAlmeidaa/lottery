package com.tiagoalmeida.lottery.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tiagoalmeida.lottery.util.Constants
import com.tiagoalmeida.lottery.util.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

abstract class BaseViewModel(
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _loading = SingleLiveEvent<Boolean>().apply { value = false }
    val loading = _loading as LiveData<Boolean>

    protected fun startLoading() = _loading.postValue(true)
    protected fun finishLoading() = _loading.postValue(false)

    protected fun runWithCoroutines(
        handleLoadingAutomatically: Boolean = false,
        doInBackground: suspend () -> Unit,
        doWhenErrorOccurs: (Exception) -> Unit
    ) = viewModelScope.launch {
        if (handleLoadingAutomatically)
            startLoading()

        try {
            withTimeout(Constants.THIRTY_SECONDS) {
                doInBackground()
            }
        } catch (exception: Exception) {
            crashlytics.recordException(exception)

            doWhenErrorOccurs(exception)
        }

        if (handleLoadingAutomatically)
            finishLoading()
    }

    protected fun logError(exception: Exception, methodName: String) {
        Log.d(Constants.TAG_LOG, "[1] $methodName - ${exception.javaClass}")
        Log.d(Constants.TAG_LOG, "[2] $methodName - ${exception.message}")
    }
}
