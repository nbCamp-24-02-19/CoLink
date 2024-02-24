package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.UserEntity

interface UserRepository {
    suspend fun userRegistration(user: UserEntity)
    suspend fun getUserDetails(id: String): UserEntity?
}