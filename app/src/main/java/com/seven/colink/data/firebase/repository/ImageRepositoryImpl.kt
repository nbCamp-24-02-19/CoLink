package com.seven.colink.data.firebase.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
class ImageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
): ImageRepository {
    override suspend fun uploadImage(imageUri: Uri) = runCatching {
        firebaseStorage.reference.child("img/${UUID.randomUUID()}.jpg").putFile(imageUri)
            .await().storage.downloadUrl.await()

    }
}