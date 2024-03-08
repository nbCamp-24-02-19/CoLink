package com.seven.colink.ui.post.content.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemGroupBoardTitleBinding
import com.seven.colink.databinding.ItemPostContentBinding
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemPostMessageBinding
import com.seven.colink.databinding.ItemPostRecruitBinding
import com.seven.colink.databinding.ItemPostSelectionTypeBinding
import com.seven.colink.databinding.ItemPostSubTitleBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.group.board.board.GroupBoardItem
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.ui.post.content.model.ContentOwnerButtonUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostContentViewTypeItem

class PostContentListAdapter(
    private val context: Context,
    private val onClickItem: (Int, PostContentItem) -> Unit,
    private val onClickButton: (Int, PostContentItem, ContentOwnerButtonUiState) -> Unit,
    private val onClickView: (PostContentItem, View) -> Unit,

    ) : ListAdapter<PostContentItem, PostContentListAdapter.PostViewHolder>(
    object : DiffUtil.ItemCallback<PostContentItem>() {

        override fun areItemsTheSame(oldItem: PostContentItem, newItem: PostContentItem): Boolean =
            when {
                oldItem is PostContentItem.Item && newItem is PostContentItem.Item -> {
                    oldItem.key == newItem.key
                }

                oldItem is PostContentItem.RecruitItem && newItem is PostContentItem.RecruitItem -> {
                    oldItem.recruit.type == newItem.recruit.type
                }

                oldItem is PostContentItem.MemberItem && newItem is PostContentItem.MemberItem -> {
                    oldItem.userInfo.uid == newItem.userInfo.uid
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
        is PostContentItem.Item -> PostContentViewTypeItem.ITEM
        is PostContentItem.RecruitItem -> PostContentViewTypeItem.RECRUIT
        is PostContentItem.MemberItem -> PostContentViewTypeItem.MEMBER
        is PostContentItem.TitleItem -> PostContentViewTypeItem.TITLE
        is PostContentItem.SubTitleItem -> PostContentViewTypeItem.SUB_TITLE
        is PostContentItem.MessageItem -> PostContentViewTypeItem.MESSAGE
        is PostContentItem.AdditionalInfo -> PostContentViewTypeItem.ADDITIONAL_INFO
        else -> PostContentViewTypeItem.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        when (PostContentViewTypeItem.from(viewType)) {
            PostContentViewTypeItem.ITEM -> PostItemViewHolder(
                context,
                ItemPostContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            PostContentViewTypeItem.RECRUIT -> PostRecruitItemViewHolder(
                context,
                ItemPostRecruitBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickButton,
            )

            PostContentViewTypeItem.MEMBER -> PostMemberInfoViewHolder(
                ItemPostMemberInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem,
            )

            PostContentViewTypeItem.TITLE -> PostTitleViewHolder(
                context,
                ItemGroupBoardTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                this,
                onClickView
            )

            PostContentViewTypeItem.SUB_TITLE -> PostSubTitleViewHolder(
                context,
                ItemPostSubTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            PostContentViewTypeItem.MESSAGE -> PostMessageViewHolder(
                ItemPostMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            PostContentViewTypeItem.ADDITIONAL_INFO -> PostAdditionalInfoViewHolder(
                ItemPostSelectionTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
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
        private val binding: ItemPostMemberInfoBinding,
        onClickItem: (Int, PostContentItem) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.MemberItem) {
                binding.tvUserName.text = item.userInfo.name
                binding.tvUserGrade.text = item.userInfo.grade.toString()
                item.userInfo.level?.let { binding.ivLevelDiaIcon.setLevelIcon(it) }
                binding.tvLevelDiaIcon.text = item.userInfo.level.toString()
                binding.tvUserIntroduction.text = item.userInfo.info
            }
        }
    }

    class PostRecruitItemViewHolder(
        private val context: Context,
        private val binding: ItemPostRecruitBinding,
        private val onClickButton: (Int, PostContentItem, ContentOwnerButtonUiState) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.RecruitItem) {
                binding.tvRecruitType.text = item.recruit.type
                binding.tvRecruitType.isVisible = item.recruit.type?.isNotEmpty() ?: false

                binding.tvNowPersonnel.text = "${item.recruit.nowPersonnel}"
                binding.tvMaxPersonnel.text = "${item.recruit.maxPersonnel}"

                if (item.buttonUiState == ContentOwnerButtonUiState.User) {
                    binding.btRecruit.isEnabled =
                        item.recruit.nowPersonnel < (item.recruit.maxPersonnel ?: -1)
                    binding.btRecruit.alpha = if (binding.btRecruit.isEnabled) 1.0f else 0.5f
                }

                binding.btRecruit.visibility = if (item.buttonUiState == ContentOwnerButtonUiState.Owner) View.GONE
                else View.VISIBLE

                binding.btRecruit.setOnClickListener {
                    onClickButton(adapterPosition, item, item.buttonUiState)
                }
            }
        }
    }

    class PostItemViewHolder(
        private val context: Context,
        private val binding: ItemPostContentBinding
    ) : PostViewHolder(binding.root) {
        private val tagAdapter = TagListAdapter(onClickItem = { item -> })

        init {
            binding.recyclerViewTag.adapter = tagAdapter
        }

        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.Item) {
                binding.tvTitle.text = item.title
                binding.tvRegisterDatetime.text = item.registeredDate
                binding.tvHits.text = item.views.toString()
                binding.tvContent.text = item.description
                binding.ivAddImage.load(item.imageUrl)
                binding.ivImageBackground.load(item.imageUrl)

                val textColorResId = when (item.groupType) {
                    GroupType.PROJECT -> {
                        binding.tvGroupType.setText(R.string.bt_project)
                        R.color.forth_color
                    }

                    else -> {
                        binding.tvGroupType.setText(R.string.bt_study)
                        R.color.study_color
                    }
                }

                binding.tvGroupType.backgroundTintList =
                    ContextCompat.getColorStateList(context, textColorResId)

                val tagListItems = mutableListOf<TagListItem>()
                tagListItems.addAll(item.tags?.map { TagListItem.ContentItem(name = it) }
                    ?: emptyList())
                tagAdapter.submitList(tagListItems)
            }
        }
    }

    class PostTitleViewHolder(
        private val context: Context,
        private val binding: ItemGroupBoardTitleBinding,
        private val adapter: PostContentListAdapter,
        private val onClickView: (PostContentItem, View) -> Unit,
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.TitleItem) {
                binding.tvTitle.text = context.getString(item.titleRes)

                when (item.viewType) {
                    GroupContentViewType.POST_ITEM -> {
                        binding.ivApplyRequest.visibility = View.INVISIBLE
                        binding.tvApplyRequest.visibility = View.INVISIBLE
                    }

                    GroupContentViewType.MEMBER_ITEM -> {
                        binding.ivApplyRequest.visibility = View.VISIBLE
                        binding.tvApplyRequest.visibility = View.VISIBLE

                        binding.ivNotify.isVisible = adapter.countPostApplyRequester() > 0

                        binding.tvApplyRequest.setOnClickListener {
                            onClickView(item, it)
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    class PostSubTitleViewHolder(
        private val context: Context,
        private val binding: ItemPostSubTitleBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.SubTitleItem) {
                binding.tvSubTitle.text = context.getString(item.titleRes)
            }
        }
    }

    class PostMessageViewHolder(
        private val binding: ItemPostMessageBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.MessageItem) {
                binding.tvMessage.text = item.message
            }
        }
    }

    class PostAdditionalInfoViewHolder(
        private val binding: ItemPostSelectionTypeBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.AdditionalInfo) {
                binding.tvPrecautions.text = item.precautions
                binding.tvDescription.text = item.recruitInfo
            }
        }
    }





    private fun countPostApplyRequester(): Int {
        val postItem = currentList.filterIsInstance<GroupBoardItem.PostItem>()

        return postItem.sumOf { postItem ->
            postItem.post.recruit?.sumOf { recruitInfo ->
                recruitInfo.applicationInfos?.count { it.applicationStatus == ApplicationStatus.PENDING }
                    ?: 0
            } ?: 0
        }
    }

}