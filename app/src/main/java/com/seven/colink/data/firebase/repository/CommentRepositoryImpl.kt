package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.CommentEntity
import com.seven.colink.domain.repository.CommentRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): CommentRepository {
    override suspend fun registerComment(comment: CommentEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.COMMENT.title).document(comment.key).set(comment)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener {
                continuation.resume(DataResultStatus.FAIL.apply { message = it.message?: "Unknown error" })
            }
    }

    override suspend fun getComment(postId: String) = runCatching {
        firestore.collection(DataBaseType.COMMENT.title).whereEqualTo("postId", postId).get().await()
            .documents.mapNotNull {
                it.toObject(CommentEntity::class.java)
            }
    }.onFailure {
        DataResultStatus.FAIL.apply { message = it.message?: "Unknown error" }
    }

    override suspend fun deleteComment(key: String) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.COMMENT.title).document(key).delete()
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener {
                continuation.resume(DataResultStatus.FAIL.message.apply { it.message })
            }
    }
}