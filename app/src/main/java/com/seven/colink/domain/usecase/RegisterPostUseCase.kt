package com.seven.colink.domain.usecase

import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.RecruitEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RegisterPostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val recruitRepository: RecruitRepository,
) {
    suspend operator fun invoke(post: Post) =
        try {
            coroutineScope {
                val result =
                    post.recruit?.map {
                        async {
                            recruitRepository.registerRecruit(it.convertRecruit(post.key)).message
                        }
                    }?.awaitAll() ?: listOf()
                postRepository.registerPost(post.convertPostEntity().copy(recruit = result))
            }
            DataResultStatus.SUCCESS
        } catch (e: Exception) {
            DataResultStatus.FAIL.apply { message = e.message ?: "Unknown error" }
        }

    private fun Post.convertPostEntity() = PostEntity(
        key = key,
        authId = authId,
        title = title,
        imageUrl = imageUrl,
        status = status,
        groupType = groupType,
        description = description,
        tags = tags,
        precautions = precautions,
        recruitInfo = recruitInfo,
        registeredDate = registeredDate,
        editDate = editDate,
        views = views,
        startDate = startDate,
        endDate = endDate,
        memberIds = memberIds,
    )

    private fun RecruitInfo.convertRecruit(key: String) = RecruitEntity(
        postId = key,
        groupId = key,
        type = type,
        maxPersonnel = maxPersonnel,
    )
}