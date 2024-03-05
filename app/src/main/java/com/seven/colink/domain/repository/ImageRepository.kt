package com.seven.colink.domain.repository

import android.net.Uri

interface ImageRepository {

    suspend fun uploadImage(imageUri: Uri): Result<Uri>
}
