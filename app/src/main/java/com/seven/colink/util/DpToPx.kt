package com.seven.colink.util

import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher

fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

fun String.getUrlFromSrc(): String {
    val urlRegex = """src="//([^"]*)"""".toRegex()

    val matchResult = urlRegex.find(this)

    return matchResult?.groups?.get(1)?.value.toString()
}

fun Context.showToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun openGallery(galleryResultLauncher: ActivityResultLauncher<Intent>) {
    val intent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    galleryResultLauncher.launch(intent)
}

fun ImageView.applyDarkFilter() {
    val paint = Paint()
    val matrix = ColorMatrix()
    matrix.set(floatArrayOf(
        0.4f, 0f, 0f, 0f, 0f, // Red
        0f, 0.4f, 0f, 0f, 0f, // Green
        0f, 0f, 0.4f, 0f, 0f, // Blue
        0f, 0f, 0f, 1f, 0f    // Alpha
    ))
    paint.colorFilter = ColorMatrixColorFilter(matrix)
    this.setLayerType(ImageView.LAYER_TYPE_HARDWARE, paint)
    this.scaleType = ImageView.ScaleType.CENTER_CROP
}

fun highlightNumbers(text: String, color: Int): Spannable {
    val regex = "\\d+"
    val spannable = SpannableStringBuilder(text)

    regex.toRegex().findAll(text).forEach { matchResult ->
        val start = matchResult.range.first
        val end = matchResult.range.last + 1

        spannable.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return spannable
}