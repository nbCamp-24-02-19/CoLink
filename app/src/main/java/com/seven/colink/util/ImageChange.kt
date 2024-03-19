package com.seven.colink.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.seven.colink.R

suspend fun Context.loadImageBitmap(imageUrl: String) = try {
    val request = ImageRequest.Builder(this)
        .data(imageUrl)
        .build()
    request.context.imageLoader.execute(request).drawable?.toBitmap()
} catch (e: Exception) {
    Log.e("loadImageBitmap", "Image loading failed: ${e.message}")
    (ContextCompat.getDrawable(this, R.drawable.ic_profile) as? BitmapDrawable)?.bitmap
}