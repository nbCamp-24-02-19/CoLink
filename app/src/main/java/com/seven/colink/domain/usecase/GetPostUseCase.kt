package com.seven.colink.domain.usecase

import android.util.Log
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.ApplicationInfoEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.RecruitEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.ui.post.register.post.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetPostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val recruitRepository: RecruitRepository,
) {
    suspend operator fun invoke(key: String) =
        postRepository.getPost(key).getOrNull()?.let {
            it.convert().copy(recruit = convertRecruit(it.recruit))
        }


    private suspend fun convertRecruit(recruits: List<String>?) = coroutineScope {
        recruits?.map {
            async {
                recruitRepository.getRecruit(it)?.let {
                    it.convert().copy(applicationInfos = convertAppInfo(it.applicationInfo))
                }
            }
        }?.awaitAll()?.filterNotNull() ?: emptyList()
    }

    private suspend fun convertAppInfo(applicationInfo: List<String>?) = coroutineScope {
        applicationInfo?.map {
            async { recruitRepository.getApplicationInfo(it)?.convert().apply {
            } }
        }?.awaitAll()?.filterNotNull() ?: emptyList()
    }

    private fun PostEntity.convert() = Post(
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

    private fun RecruitEntity.convert() = RecruitInfo(
        key = key,
        type = type,
        maxPersonnel = maxPersonnel,
    )

    private fun ApplicationInfoEntity.convert() = recruitId?.let {
        ApplicationInfo(
        key = key,
        recruitId = it,
        userId = userId,
        applicationStatus = applicationStatus,
        applicationDate = applicationDate,
    )
    }
}