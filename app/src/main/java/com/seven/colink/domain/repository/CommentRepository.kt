package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.CommentEntity
import com.seven.colink.util.status.DataResultStatus

interface CommentRepository {

    suspend fun registerComment(comment: CommentEntity): DataResultStatus
    suspend fun getComment(postId: String): Result<List<CommentEntity>>
    suspend fun deleteComment(key: String): Any
    suspend fun editComment(key: String, comment: String): Any
}
