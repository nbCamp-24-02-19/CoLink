package com.seven.colink.domain.usecase

import com.seven.colink.domain.model.PostWithUser
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import javax.inject.Inject

class GetPostWithUserUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(postId: String) =
        PostWithUser(
            post = postRepository.getPost(postId).getOrNull(),
            users = postRepository.getPost(postId).getOrNull()?.memberIds?.map { uid ->
                userRepository.getUserDetails(uid).getOrNull()
            }
        )

}