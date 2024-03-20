package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.domain.repository.NotificationStoreRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NotificationStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : NotificationStoreRepository {
    override suspend fun registerNotification(notification: NotificationEntity) {
        firestore.collection(DataBaseType.NOTIFICATION.title).document(notification.key)
            .set(notification)
    }

    override suspend fun getNotificationByUid(uid: String) = withContext(Dispatchers.IO) {
        try {
            firestore.collection(DataBaseType.NOTIFICATION.title)
                .whereEqualTo("toUserId", uid)
                .get().await()
                .documents
                .mapNotNull {
                    it.toObject(NotificationEntity::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun deleteNotification(key: String) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.NOTIFICATION.title).document(key).delete()
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply {
                    message = e.message ?: "Unknown error"
                })
            }
    }
}