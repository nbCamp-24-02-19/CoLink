package com.seven.colink.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.seven.colink.util.status.DataResultStatus

interface AuthRepository {
    suspend fun register(email: String, password: String): DataResultStatus
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun signOut()
}