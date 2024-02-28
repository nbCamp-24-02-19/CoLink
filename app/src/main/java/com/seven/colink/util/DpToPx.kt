package com.seven.colink.util

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
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