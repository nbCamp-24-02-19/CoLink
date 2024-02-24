package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.mapper.toFirestore
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
    private val firebaseFirestore: FirebaseFirestore
) : UserRepository {
    override suspend fun registerUser(user: UserEntity) = suspendCoroutine { continuation ->

        firebaseAuth.currentUser?.uid?.let {
            firebaseFirestore.collection(DataBaseType.USER.title).document(it).set(user.toFirestore())
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    continuation.resume(DataResultStatus.FAIL.apply { this.message = e.message?: "Unknown Error" })
                }
        }
    }

    override suspend fun getUserDetails(id: String): UserEntity? {
        var userEntity: UserEntity? = null

        try {
            val documentSnapshot = firebaseFirestore.collection(DataBaseType.USER.title).document(id).get().await()
            if (documentSnapshot.exists()) {
                userEntity = documentSnapshot.toObject(UserEntity::class.java)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user: ${e.message}")
        }

        return userEntity
    }
}