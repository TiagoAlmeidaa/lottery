package com.tiagoalmeida.lottery.ui.custom

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.util.Constants

class CustomLoadingOrErrorView(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progress_bar)
    }

    private val imageView: AppCompatImageView by lazy {
        findViewById(R.id.image_view_error)
    }

    private val title: AppCompatTextView by lazy {
        findViewById(R.id.text_view_error)
    }

    private val subtitle: AppCompatTextView by lazy {
        findViewById(R.id.text_view_timer)
    }

    private var errorCountDownTimer: CountDownTimer? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_loading_and_error, this, true)
    }

    fun setLoading() {
        loadingViewVisibility(VISIBLE)
        errorViewVisibility(GONE)
    }

    fun setError(time: Long, message: String, onFinish: () -> Unit) {
        loadingViewVisibility(GONE)
        errorViewVisibility(VISIBLE)

        title.text = message

        prepareCountDown(time, onFinish)
    }

    private fun loadingViewVisibility(visibility: Int) {
        progressBar.visibility = visibility
    }

    private fun errorViewVisibility(visibility: Int) {
        imageView.visibility = visibility
        title.visibility = visibility
        subtitle.visibility = visibility
    }

    private fun prepareCountDown(time: Long, onFinish: () -> Unit) {
        errorCountDownTimer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val extraText = if (time == Constants.THREE_SECONDS) {
                    context.getString(R.string.game_register_back_in)
                } else {
                    context.getString(R.string.game_register_finishing_in)
                }

                val millis = (millisUntilFinished + Constants.ONE_SECOND) / Constants.ONE_SECOND

                subtitle.text = String.format(
                    context.getString(R.string.game_register_error_timer_format),
                    extraText,
                    millis
                )
            }

            override fun onFinish() {
                errorCountDownTimer = null
                onFinish()
            }
        }
        errorCountDownTimer?.start()
    }

}