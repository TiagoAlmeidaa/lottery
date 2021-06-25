package com.tiagoalmeida.lottery.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.util.view.ButtonCardView

fun buildRemoveGameDialog(
    context: Context,
    parent: ViewGroup?,
    onClick: () -> Unit
): AlertDialog {
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.dialog_remove_game, parent, false)

    val dialog = AlertDialog
        .Builder(context, R.style.AlertDialog)
        .setView(view)
        .create()

    with(view) {
        findViewById<ButtonCardView>(R.id.button_remove)?.let { button ->
            button.setOnClickListener {
                onClick()
                dialog.dismiss()
            }
        }

        findViewById<ButtonCardView>(R.id.button_cancel)?.let { button ->
            button.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    return dialog
}

fun buildFilterGameDialog(
    context: Context,
    parent: ViewGroup?,
    onClick: (String) -> Unit
): AlertDialog {
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.dialog_detail_game_search, parent, false)

    val dialog = AlertDialog
        .Builder(context, R.style.AlertDialog)
        .setView(view)
        .create()

    with(view) {
        val editText = findViewById<TextInputEditText>(R.id.edit_text_email)

        findViewById<ButtonCardView>(R.id.button_compare)?.let { button ->
            button.setOnClickListener {
                onClick(editText.text?.toString() ?: "")
                dialog.dismiss()
            }
        }

        findViewById<ButtonCardView>(R.id.button_cancel)?.let { button ->
            button.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    return dialog
}