package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.seven.colink.data.firebase.type.DataResult
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
                        continuation.resume(DataResultStatus.SUCCESS.apply {
                            message = firebaseAuth.currentUser!!.uid
                        })
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

    override suspend fun signIn(email: String, password: String) =
        suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS.apply {
                        message = firebaseAuth.currentUser!!.uid
                    })
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

    override suspend fun registerUserByGoogle(token: String) = try {
        val result = firebaseAuth.signInWithCredential(
            GoogleAuthProvider.getCredential(token, null)
        ).await()
        result.user ?: DataResultStatus.FAIL.apply { message = "유저를 찾을 수 없습니다." }
    } catch (e: Exception) {
        DataResultStatus.FAIL.apply { message = e.message ?: "알수없는 에러" }
    }

    override suspend fun getCustomToken(token: String):DataResult<FirebaseUser> = suspendCoroutine { continuation ->
        val function = Firebase.functions("asia-northeast3")
        val data = hashMapOf(
            "token" to token
        )

        function.getHttpsCallable("kakaoCustomAuth")
            .call(data)
            .addOnSuccessListener { functionResult ->
                val result = functionResult.data as HashMap<*, *>
                val mKey = result.keys.firstOrNull()?.toString()
                if (mKey != null) {
                    firebaseAuth.signInWithCustomToken(result[mKey]!!.toString())
                        .addOnSuccessListener { authResult ->
                            authResult.user?.let { user ->
                                continuation.resume(DataResult.Success(data = user))
                            } ?: continuation.resume(DataResult.Error(Exception("Authentication succeeded but no user found")))
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(DataResult.Error(error = exception))
                        }
                } else {
                    continuation.resume(DataResult.Error(Exception("No data returned from function")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(DataResult.Error(error = exception))
            }
    }
}