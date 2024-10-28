package com.tiagoalmeida.lottery.extensions

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tiagoalmeida.lottery.data.model.LotteryType
import com.tiagoalmeida.lottery.util.Brazil
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun AutoCompleteTextView.getLotteryType(): LotteryType? =
    LotteryType
        .values()
        .find { type -> type.lotteryName == text.toString() }

fun Date.toAppDateString(): String {
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return simpleDateFormat.format(this)
}

fun Double.toCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Brazil.locale)
    return format.format(this)
}

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

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun Int.toStringNumber(): String = if (this < 10) {
    "0$this"
} else {
    this.toString()
}

fun RecyclerView.onBottomReached(bottomReached: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                bottomReached()
            }
        }
    })
}

fun SharedPreferences.getString(key: String): String {
    return getString(key, "") ?: ""
}

fun SharedPreferences.putString(key: String, value: String) {
    edit().putString(key, value).apply()
}

fun SharedPreferences.getInt(key: String): Int {
    return getInt(key, 0)
}

fun SharedPreferences.putInt(key: String, value: Int) {
    edit().putInt(key, value).apply()
}

fun isSDKVersionBiggerThanO(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}

fun isSDKVersionBiggerThanM(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun ViewPager2.onPageChanged(method: (Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            method(position)
        }
    })
}
