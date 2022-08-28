package com.tiagoalmeida.lottery.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.getColorOrThrow
import com.tiagoalmeida.lottery.R

class CustomButtonCardView(
    context: Context,
    attributeSet: AttributeSet
) : CardView(context, attributeSet) {

    private val icon: AppCompatImageView by lazy {
        findViewById(R.id.image_view_icon)
    }

    private val title: AppCompatTextView by lazy {
        findViewById(R.id.text_view_contest_number)
    }

    private val content: LinearLayout by lazy {
        findViewById(R.id.layout_content)
    }

    init {
        background = null

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.button_card_view, this, true)

        val sets = intArrayOf(
            R.attr.backColor,
            R.attr.icon,
            R.attr.setCenter,
            R.attr.setHorizontal,
            R.attr.title,
            R.attr.titleColor
        )
        val attrs = context.obtainStyledAttributes(attributeSet, sets)

        val drawable = attrs.getDrawable(sets.indexOf(R.attr.icon))
        if (drawable == null) {
            icon.visibility = View.GONE
        } else {
            icon.visibility = View.VISIBLE
            icon.setImageDrawable(drawable)
        }

        val center = attrs.getBoolean(sets.indexOf(R.attr.setCenter), false)
        if (center)
            content.gravity = Gravity.CENTER

        val horizontal = attrs.getBoolean(sets.indexOf(R.attr.setHorizontal), false)
        setOrientation(horizontal)

        val stringTitle = attrs.getString(sets.indexOf(R.attr.title))
        title.text = stringTitle

        val defaultTitleColor = ContextCompat.getColor(context, android.R.color.white)
        val titleColor = attrs.getColor(sets.indexOf(R.attr.titleColor), defaultTitleColor)
        title.setTextColor(titleColor)

        try {
            val backColor = attrs.getColorOrThrow(sets.indexOf(R.attr.backColor))
            content.setBackgroundColor(backColor)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        attrs.recycle()
    }

    private fun setMarginOnText(left: Int = 0, top: Int = 0) {
        LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(left, top, 0, 0)
            title.layoutParams = this
        }
    }

    fun setOrientation(horizontal: Boolean) {
        content.orientation = if (horizontal) {
            setMarginOnText(left = 15)
            LinearLayout.HORIZONTAL
        } else {
            setMarginOnText(top = 15)
            LinearLayout.VERTICAL
        }
    }

    fun setTitle(text: String) {
        title.text = text
    }

}