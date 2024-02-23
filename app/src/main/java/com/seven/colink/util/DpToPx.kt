package com.seven.colink.util

import android.content.Context

fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

fun String.getUrlFromSrc(): String {
    val urlRegex = """src="//([^"]*)"""".toRegex()

    val matchResult = urlRegex.find(this)

    return matchResult?.groups?.get(1)?.value.toString()
}