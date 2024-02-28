package com.seven.colink.ui.post.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.ItemPostContentBinding
import com.seven.colink.databinding.ItemPostImageBinding
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemPostRecruitBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.post.TagListItem
import com.seven.colink.ui.post.content.PostContentItem
import com.seven.colink.ui.post.content.PostContentViewType
import com.seven.colink.util.status.GroupType

class PostContentListAdapter(
    private val onClickItem: (Int, PostContentItem) -> Unit
) : ListAdapter<PostContentItem, PostContentListAdapter.PostViewHolder>(
    object : DiffUtil.ItemCallback<PostContentItem>() {

        override fun areItemsTheSame(oldItem: PostContentItem, newItem: PostContentItem): Boolean =
            when {
                oldItem is PostContentItem.Item && newItem is PostContentItem.Item -> {
                    oldItem.id == newItem.id
                }

                oldItem is PostContentItem.RecruitItem && newItem is PostContentItem.RecruitItem -> {
                    oldItem.recruit == newItem.recruit
                }

                oldItem is PostContentItem.MemberItem && newItem is PostContentItem.MemberItem -> {
                    oldItem.userInfo == newItem.userInfo
                }

                oldItem is PostContentItem.ImageItem && newItem is PostContentItem.ImageItem -> {
                    oldItem.imageUrl == newItem.imageUrl
                }

                else -> oldItem == newItem
            }


        override fun areContentsTheSame(
            oldItem: PostContentItem,
            newItem: PostContentItem
        ): Boolean = oldItem == newItem

    }
) {

    abstract class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: PostContentItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PostContentItem.Item -> PostContentViewType.ITEM
        is PostContentItem.RecruitItem -> PostContentViewType.RECRUIT
        is PostContentItem.MemberItem -> PostContentViewType.MEMBER
        is PostContentItem.ImageItem -> PostContentViewType.IMAGE
        else -> PostContentViewType.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        when (PostContentViewType.from(viewType)) {
            PostContentViewType.ITEM -> PostItemViewHolder(
                ItemPostContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            PostContentViewType.RECRUIT -> PostRecruitItemViewHolder(
                ItemPostRecruitBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem,
            )

            PostContentViewType.IMAGE -> PostImageViewHolder(
                ItemPostImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            PostContentViewType.MEMBER -> PostMemberInfoViewHolder(
                ItemPostMemberInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem,
            )

            else -> PostUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )

        }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class PostUnknownViewHolder(binding: ItemUnknownBinding) :
        PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) = Unit
    }

    class PostMemberInfoViewHolder(
        binding: ItemPostMemberInfoBinding,
        onClickItem: (Int, PostContentItem) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.MemberItem) {

            }
        }
    }

    class PostRecruitItemViewHolder(
        private val binding: ItemPostRecruitBinding,
        onClickItem: (Int, PostContentItem) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.RecruitItem) {
                binding.tvRecruitType.text = item.recruit.type
                binding.tvCurrentPersonnel.text =
                    "${item.recruit.nowPersonnel}/${item.recruit.maxPersonnel}"
            }
        }
    }

    class PostItemViewHolder(
        private val binding: ItemPostContentBinding
    ) : PostViewHolder(binding.root) {
        private val tagAdapter = TagListAdapter(onClickItem = { position, item -> })

        init {
            binding.recyclerViewTag.adapter = tagAdapter
        }

        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.Item) {
                binding.tvGroupType.text =
                    if (item.groupType == GroupType.PROJECT) "Project" else "Study"
                binding.tvTitle.text = item.title
                binding.tvRegisterDatetime.text = item.datetime
                binding.tvContent.text = item.content

                val tagListItems = mutableListOf<TagListItem>()
                tagListItems.addAll(item.tags?.map { TagListItem.ContentItem(tagName = it) }
                    ?: emptyList())
                tagAdapter.submitList(tagListItems)
            }
        }
    }

    class PostImageViewHolder(
        private val binding: ItemPostImageBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.ImageItem) {
                binding.ivAddImage.load(item.imageUrl)
                binding.ivImageBackground.load(item.imageUrl)
            }
        }
    }


}