package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.mapper.toFirestore
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : UserRepository {
    override suspend fun userRegistration(user: UserEntity) {

        firebaseAuth.currentUser?.uid?.let {
            firebaseFirestore.collection("users").document(it).set(user.toFirestore())
                .addOnSuccessListener {
                    Log.d("saveUser", "success")
                }
                .addOnFailureListener { e ->
                    Log.e("saveUser", "Error: ${e.message}")
                }
        }
    }

    override suspend fun getUserDetails(id: String): UserEntity? {
        var userEntity: UserEntity? = null

        try {
            val documentSnapshot = firebaseFirestore.collection("users").document(id).get().await()
            if (documentSnapshot.exists()) {
                userEntity = documentSnapshot.toObject(UserEntity::class.java)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user: ${e.message}")
        }

        return userEntity
    }
}