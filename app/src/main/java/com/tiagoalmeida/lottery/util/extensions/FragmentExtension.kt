package com.tiagoalmeida.lottery.util.extensions

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

fun Fragment.hideKeyboard() {
    val auxContext = context
    val auxView = view

    if (auxContext != null && auxView != null) {
        val manager = auxContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(auxView.windowToken, 0)
    }
}

fun Fragment.navigate(actionId: Int) {
    view?.let { theView ->
        try {
            Navigation.findNavController(theView).navigate(actionId)
        } catch (exception: Exception) {
            Log.e("FragmentExtension", "Unable to navigate, reason: ${exception.message}")
        }
    }
}

fun Fragment.showToast(messageId: Int) {
    Toast.makeText(context, getString(messageId), Toast.LENGTH_SHORT).show()
}
