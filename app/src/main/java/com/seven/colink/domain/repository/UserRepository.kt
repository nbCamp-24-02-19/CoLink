package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.util.status.DataResultStatus

interface UserRepository {
    suspend fun registerUser(user: UserEntity): DataResultStatus
    suspend fun getUserDetails(uid: String): Result<UserEntity?>
    suspend fun deleteUser(uid: String): DataResultStatus
}