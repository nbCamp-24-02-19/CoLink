package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PostRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : PostRepository {
    override suspend fun registerPost(post: PostEntity) = suspendCoroutine { continuation ->
        firebaseFirestore.collection(DataBaseType.POST.title).document(post.key).set(post)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply {
                    message = e.message ?: "Unknown error"
                })
            }
    }

    override suspend fun getPost(key: String) = runCatching {
        firebaseFirestore.collection(DataBaseType.POST.title).document(key)
            .get().await()
            .toObject(PostEntity::class.java)
    }

    override suspend fun getPostByAuthId(authId: String) = runCatching {
        firebaseFirestore.collection(DataBaseType.POST.title).whereEqualTo("authId", authId).get()
            .await()
            .documents.mapNotNull {
                it.toObject(PostEntity::class.java)
            }
    }

    override suspend fun getPostByContainUserId(userId: String) = runCatching {
        firebaseFirestore.collection(DataBaseType.POST.title).whereArrayContains("userId", userId)
            .get().await()
            .documents.mapNotNull {
                it.toObject(PostEntity::class.java)
            }
    }

    override suspend fun deletePost(key: String) = suspendCoroutine { continuation ->
        firebaseFirestore.collection(DataBaseType.POST.title).document(key).delete()
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