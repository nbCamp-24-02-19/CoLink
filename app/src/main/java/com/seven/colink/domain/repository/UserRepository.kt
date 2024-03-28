package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.util.status.DataResultStatus

interface UserRepository {
    suspend fun registerUser(user: UserEntity): DataResultStatus
    suspend fun getUserDetails(uid: String): Result<UserEntity?>
    suspend fun deleteUser(uid: String): DataResultStatus
    suspend fun checkUserEmail(email: String): Boolean
    suspend fun getUserBySpecialty(specialty: String): Result<List<UserEntity>>
    suspend fun updateUserInfo(user: UserEntity): DataResultStatus
    suspend fun registerToken(): DataResultStatus
}