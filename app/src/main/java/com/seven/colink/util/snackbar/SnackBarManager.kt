package com.seven.colink.util.snackbar

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.seven.colink.R
import com.seven.colink.util.status.SnackType

fun View.setSnackBar(
    state: SnackType,
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
): Snackbar {
    val snackBar = Snackbar.make(this,"",duration)
    val layout = snackBar.view as Snackbar.SnackbarLayout
    val view = LayoutInflater.from(context).inflate(R.layout.util_custom_snackbar, null)

    layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))

    val img = view.findViewById<ImageView>(R.id.iv_snack_state)
    val text = view.findViewById<TextView>(R.id.tv_snack_message)

    when(state) {
        is SnackType.Success -> img.load(R.drawable.widget_success)
        is SnackType.Error -> img.load(R.drawable.widget_error)
        is SnackType.Notice -> img.load(R.drawable.widget_notice)
    }
    text.text = message

    layout.addView(view)

    return snackBar
}



fun View.setSnackBar(
    image: Any?,
    message: String,
    @ColorRes backgroundColor: Int = R.color.black,
    @ColorRes fontColor: Int = R.color.white,
    duration: Int = Snackbar.LENGTH_SHORT,
): Snackbar {
    val snackBar = Snackbar.make(this,"",duration)
    val layout = snackBar.view as Snackbar.SnackbarLayout
    val view = LayoutInflater.from(context).inflate(R.layout.util_custom_snackbar, null)

    layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    val img = view.findViewById<ImageView>(R.id.iv_snack_state)
    val text = view.findViewById<TextView>(R.id.tv_snack_message)

    view.findViewById<LinearLayoutCompat>(R.id.ll_snack_bar).apply {
        background.setTint(ContextCompat.getColor(context, backgroundColor))
    }
    img.load(image)
    text.text = message
    text.setTextColor(ContextCompat.getColor(context, fontColor))

    layout.addView(view)

    return snackBar
}