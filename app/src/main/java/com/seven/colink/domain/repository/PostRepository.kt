package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

interface PostRepository {
    suspend fun registerPost(post: PostEntity): DataResultStatus?
    suspend fun getPost(key: String): Result<PostEntity?>
    suspend fun getPostByAuthId(authId: String): Result<List<PostEntity>>
    suspend fun getPostByContainUserId(userId: String): Result<List<PostEntity>>
    suspend fun deletePost(key: String): DataResultStatus
    suspend fun searchQuery(
        query: String,
        groupType: GroupType? = null,
        projectStatus: ProjectStatus? = null
    ): List<PostEntity>

    suspend fun updatePost(key: String, updatedPost: PostEntity): DataResultStatus
    suspend fun incrementPostViews(key: String): DataResultStatus
    suspend fun getRecentPost(count: Int, groupType: GroupType?= null): List<PostEntity>
    suspend fun synchronizeFirestoreToAlgolia()
}
