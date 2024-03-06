package com.seven.colink.util

import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.view.View

class CustomTypefaceSpan(private val newType: Typeface) : TypefaceSpan("") {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        paint.typeface = tf
    }
}

fun String.setFontType(
    text: String? = null,
    fontType: Typeface,
) = SpannableString(plus(text)).apply {
    setSpan(
        CustomTypefaceSpan(fontType),
        0,
        this@setFontType.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun View.interBold(): Typeface = Typeface.createFromAsset(context.assets,"fonts/inter_bold.ttf")