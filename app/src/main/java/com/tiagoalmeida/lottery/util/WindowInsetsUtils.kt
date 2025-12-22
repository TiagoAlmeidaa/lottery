package com.tiagoalmeida.lottery.util

import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.tiagoalmeida.lottery.R

internal fun View.applyWindowInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            insets.getInsets(WindowInsetsCompat.Type.systemBars())
        } else {
            @Suppress("DEPRECATION")
            androidx.core.graphics.Insets.of(
                insets.systemWindowInsetLeft,
                insets.systemWindowInsetTop,
                insets.systemWindowInsetRight,
                insets.systemWindowInsetBottom
            )
        }
        view.setPadding(
            systemBars.left,
            systemBars.top,
            systemBars.right,
            systemBars.bottom
        )
        insets
    }
}

internal fun Activity.adjustS() {
    val systemBars = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false // true for dark icons
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackgroundDefault)
    } else {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackgroundDefault)
    }
}