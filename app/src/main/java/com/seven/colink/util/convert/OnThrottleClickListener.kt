package com.seven.colink.util.convert

import android.util.Log
import android.view.View
import android.view.View.OnClickListener

class OnThrottleClickListener(
    private val clickListener: OnClickListener,
    private val interval: Long = 1500
) : View.OnClickListener {

    private var clickable = true

    override fun onClick(view: View?) {
        if (clickable) {
            clickable = false
            view?.run {
                postDelayed({
                    clickable = true
                }, interval)
                clickListener.onClick(view)
            }
        } else {
            Log.e("Error", "Too Fast Click")
        }
    }
}

fun View.onThrottleClick(action: (view: View) -> Unit) {
    val listener = View.OnClickListener { action(it) }
    setOnClickListener(OnThrottleClickListener(listener))
}

fun View.onThrottleClick(action: (view: View) -> Unit, interval: Long) {
    val listener = View.OnClickListener { action(it) }
    setOnClickListener(OnThrottleClickListener(listener, interval))
}