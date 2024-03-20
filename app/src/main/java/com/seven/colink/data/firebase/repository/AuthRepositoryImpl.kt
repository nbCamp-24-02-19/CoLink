package com.seven.colink.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override suspend fun register(email: String, password: String) =
        suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(DataResultStatus.SUCCESS.apply { message = firebaseAuth.currentUser!!.uid })
                    }
                }
                .addOnFailureListener { e ->
                    if (e is FirebaseAuthException) {
                        val errorCode = e.errorCode
                        continuation.resume(DataResultStatus.FAIL.apply {
                            this.message = errorCode
                        })
                    } else {
                        continuation.resume(DataResultStatus.FAIL.apply {
                            this.message = e.message ?: "Unknown error"
                        })
                    }
                }
        }

    override suspend fun signIn(email: String, password: String) = suspendCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS.apply { message = firebaseAuth.currentUser!!.uid })
            }
            .addOnFailureListener { e ->
                if (e is FirebaseAuthException) {
                    val errorCode = e.errorCode
                    continuation.resume(DataResultStatus.FAIL.apply {
                        this.message = errorCode
                    })
                } else {
                    continuation.resume(DataResultStatus.FAIL.apply {
                        this.message = e.message ?: "Unknown error"
                    })
                }
            }
    }


    override suspend fun getCurrentUser(): DataResultStatus {
        return if (firebaseAuth.currentUser != null) {
            DataResultStatus.SUCCESS.apply { message = firebaseAuth.currentUser!!.uid }
        } else {
            DataResultStatus.FAIL.apply {
                this.message = "No user"
            }
        }
    }

    override suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    override suspend fun deleteUser() = suspendCoroutine { continuation ->
        firebaseAuth.currentUser?.delete()
            ?.addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            ?.addOnFailureListener { e ->
                if (e is FirebaseAuthException) {
                    val errorCode = e.errorCode
                    continuation.resume(DataResultStatus.FAIL.apply {
                        this.message = errorCode
                    })
                } else {
                    continuation.resume(DataResultStatus.FAIL.apply {
                        this.message = e.message ?: "Unknown error"
                    })
                }
            }
    }
}