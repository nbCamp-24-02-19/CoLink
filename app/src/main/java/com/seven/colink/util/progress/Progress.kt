package com.seven.colink.util.progress

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seven.colink.R

fun Activity.showProgressOverlay() {
    val extra = findViewById<View>(R.id.progress_bar)
    if (extra != null) return

    val layout = LayoutInflater.from(this).inflate(R.layout.progress_bar, null).apply {
        id = R.id.progress_bar
    }
    val container = findViewById<ViewGroup>(android.R.id.content)
    container.addView(layout)
}

fun Activity.hideProgressOverlay() {
    val container = findViewById<ViewGroup>(android.R.id.content)
    val layout = container.findViewById<View>(R.id.progress_bar)
    if (layout != null) {
        container.removeView(layout)
    }
}

fun Fragment.showProgressOverlay() {
    val context = context ?: return
    val activity = activity ?: return
    val extra = activity.findViewById<View>(R.id.progress_bar)
    if (extra != null) return

    val layout = LayoutInflater.from(context).inflate(R.layout.progress_bar, null).apply {
        id = R.id.progress_bar
    }
    val container = activity.findViewById<ViewGroup>(android.R.id.content)
    container.addView(layout)
}

fun Fragment.hideProgressOverlay() {
    val activity = activity ?: return
    val container = activity.findViewById<ViewGroup>(android.R.id.content)
    val layout = container.findViewById<View>(R.id.progress_bar)
    if (layout != null) {
        container.removeView(layout)
    }
}