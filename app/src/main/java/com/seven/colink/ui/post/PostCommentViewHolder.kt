package com.seven.colink.ui.post

import android.content.Context
import com.seven.colink.databinding.ItemPostCommentBinding
import com.seven.colink.databinding.ItemPostCommentSendBinding
import com.seven.colink.databinding.ItemPostCommentTitleBinding
import com.seven.colink.ui.post.content.adapter.PostContentListAdapter
import com.seven.colink.ui.post.content.model.PostContentItem

class PostCommentCountViewHolder(
    private val context: Context,
    private val binding: ItemPostCommentTitleBinding
): PostContentListAdapter.PostViewHolder(binding.root) {

    override fun onBind(item: PostContentItem) {
        val commentCount = binding.tvPostCommentCount
    }

}

class PostCommentViewHolder(
    private val context: Context,
    private val binding: ItemPostCommentBinding
): PostContentListAdapter.PostViewHolder(binding.root) {

    override fun onBind(item: PostContentItem) {
        val commentName = binding.tvPostCommentName
        val commentTime = binding.tvPostCommentTime
        val comment = binding.tvPostComment
    }

}

class PostCommentSendViewHolder(
    private val context: Context,
    private val binding: ItemPostCommentSendBinding
): PostContentListAdapter.PostViewHolder(binding.root) {

    override fun onBind(item: PostContentItem) {
        val sendEdit = binding.etPostCommentSend
        val sendClick = binding.btnPostCommentSend
    }

}