package com.seven.colink.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {
    override suspend fun registerUser(user: UserEntity) = suspendCoroutine { continuation ->
        firebaseAuth.currentUser?.uid?.let {
            firestore.collection(DataBaseType.USER.title).document(it).set(user)
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    continuation.resume(DataResultStatus.FAIL.apply { this.message = e.message?: "Unknown Error" })
                }
        }
    }

    override suspend fun getUserDetails(uid: String) = runCatching {
        firestore.collection(DataBaseType.USER.title).document(uid).get().await()
            .toObject(UserEntity::class.java)
    }.onFailure {
        DataResultStatus.FAIL.apply { message = it.message?: "Unknown error" }
    }

    override suspend fun deleteUser(uid: String) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.USER.title).document(uid).delete()
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener {
                continuation.resume(DataResultStatus.FAIL.apply { message = it.message?: "Unknown error" })
            }
    }
}