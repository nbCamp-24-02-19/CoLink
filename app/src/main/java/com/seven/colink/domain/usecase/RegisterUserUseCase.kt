package com.seven.colink.domain.usecase

import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: UserEntity): String? {
        val result = authRepository.register(user.email, user.password)
        if (result == "success") {
            userRepository.userRegistration(user)
        }else {
            return result
        }
        return null
    }
}