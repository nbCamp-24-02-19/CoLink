package com.seven.colink.domain.repository

import android.net.Uri

interface ImageRepository {

    suspend fun uploadImage(imageUri: Uri): Result<Uri>
    suspend fun uploadChatImage(imageUri: Uri): Result<Uri>
}
