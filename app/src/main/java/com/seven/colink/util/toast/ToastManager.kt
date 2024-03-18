package com.seven.colink.util.toast

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import coil.load
import com.seven.colink.R
import com.seven.colink.util.dpToPx

fun Context.setToast(
    img: Any?,
    message: String,
    @ColorRes backgroundColor: Int = R.color.white,
    @ColorRes fontColor: Int = R.color.black,
    toastDuration: Int = Toast.LENGTH_SHORT,
) {
    val layout = LayoutInflater.from(this).inflate(R.layout.util_custom_snackbar, null)

    val image = layout.findViewById<ImageView>(R.id.iv_snack_state)
    val text = layout.findViewById<TextView>(R.id.tv_snack_message)

    layout.elevation = 4.0F
    layout.background.setTint(ContextCompat.getColor(this, backgroundColor))
    image.load(img)
    text.text = message
    text.setTextColor(ContextCompat.getColor(this, fontColor))

    Toast(this).apply {
        duration = toastDuration
        view = layout
    }.show()
}
