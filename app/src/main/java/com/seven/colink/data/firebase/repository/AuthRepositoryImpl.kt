package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.seven.colink.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override suspend fun register(email: String, password: String): String? {
        var message: String? = null
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    message = "success"
                    Log.d("signUp", "success")
                }
            }
            .addOnFailureListener { e ->
                message = e.message
                Log.e("signUp", "회원가입 실패: ${e.message}")
            }
        return message
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser? = suspendCancellableCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(FirebaseAuth.getInstance().currentUser)
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Unknown authentication error"))
                }
            }
    }

    override suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}