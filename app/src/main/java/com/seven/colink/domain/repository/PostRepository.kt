package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.util.status.DataResultStatus

interface PostRepository {
    suspend fun registerPost(post: PostEntity): DataResultStatus?
    suspend fun getPost(key: String): Result<PostEntity?>
    suspend fun getPostByAuthId(authId: String): Result<List<PostEntity>>
    suspend fun getPostByContainUserId(userId: String): Result<List<PostEntity>>
    suspend fun deletePost(key: String): DataResultStatus
}
