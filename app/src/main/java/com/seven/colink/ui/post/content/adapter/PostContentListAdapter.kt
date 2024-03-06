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
import com.seven.colink.databinding.ItemPostContentBinding
import com.seven.colink.databinding.ItemPostGroupTypeBinding
import com.seven.colink.databinding.ItemPostImageBinding
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemPostRecruitBinding
import com.seven.colink.databinding.ItemPostSubTitleBinding
import com.seven.colink.databinding.ItemPostTitleBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.ui.post.content.model.PostContentButtonUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostContentViewType

class PostContentListAdapter(
    private val context: Context,
    private val onClickItem: (Int, PostContentItem) -> Unit,
    private val onClickButton: (Int, PostContentItem, PostContentButtonUiState) -> Unit
) : ListAdapter<PostContentItem, PostContentListAdapter.PostViewHolder>(
    object : DiffUtil.ItemCallback<PostContentItem>() {

        override fun areItemsTheSame(oldItem: PostContentItem, newItem: PostContentItem): Boolean =
            when {
                oldItem is PostContentItem.Item && newItem is PostContentItem.Item -> {
                    oldItem.key == newItem.key
                }

                oldItem is PostContentItem.GroupTypeItem && newItem is PostContentItem.GroupTypeItem -> {
                    oldItem.groupType == newItem.groupType
                }

                oldItem is PostContentItem.RecruitItem && newItem is PostContentItem.RecruitItem -> {
                    oldItem.recruit.type == newItem.recruit.type
                }

                oldItem is PostContentItem.MemberItem && newItem is PostContentItem.MemberItem -> {
                    oldItem.userInfo.uid == newItem.userInfo.uid
                }

                oldItem is PostContentItem.ImageItem && newItem is PostContentItem.ImageItem -> {
                    oldItem.imageUrl == newItem.imageUrl
                }

                oldItem is PostContentItem.TitleItem && newItem is PostContentItem.TitleItem -> {
                    oldItem.titleRes == newItem.titleRes
                }

                oldItem is PostContentItem.SubTitleItem && newItem is PostContentItem.SubTitleItem -> {
                    oldItem.titleRes == newItem.titleRes
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
        is PostContentItem.GroupTypeItem -> PostContentViewType.GROUP_TYPE
        is PostContentItem.RecruitItem -> PostContentViewType.RECRUIT
        is PostContentItem.MemberItem -> PostContentViewType.MEMBER
        is PostContentItem.ImageItem -> PostContentViewType.IMAGE
        is PostContentItem.TitleItem -> PostContentViewType.TITLE
        is PostContentItem.SubTitleItem -> PostContentViewType.SUB_TITLE
        else -> PostContentViewType.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        when (PostContentViewType.from(viewType)) {
            PostContentViewType.ITEM -> PostItemViewHolder(
                context,
                ItemPostContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            PostContentViewType.GROUP_TYPE -> PostGroupTypeItemViewHolder(
                context,
                ItemPostGroupTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            PostContentViewType.RECRUIT -> PostRecruitItemViewHolder(
                context,
                ItemPostRecruitBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickButton,
            )

            PostContentViewType.IMAGE -> PostImageViewHolder(
                context,
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

            PostContentViewType.TITLE -> PostTitleViewHolder(
                context,
                ItemPostTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            PostContentViewType.SUB_TITLE -> PostSubTitleViewHolder(
                context,
                ItemPostSubTitleBinding.inflate(
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
        private val onClickButton: (Int, PostContentItem, PostContentButtonUiState) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.RecruitItem) {
                binding.tvRecruitType.text = item.recruit.type
                binding.tvRecruitType.isVisible = item.recruit.type?.isNotEmpty() ?: false

                binding.tvNowPersonnel.text = "${item.recruit.nowPersonnel}"
                binding.tvMaxPersonnel.text = "${item.recruit.maxPersonnel}"

                if (item.buttonUiState == PostContentButtonUiState.Supporter) {
                    binding.btRecruit.isEnabled =
                        item.recruit.nowPersonnel < (item.recruit.maxPersonnel ?: -1)
                    binding.btRecruit.alpha = if (binding.btRecruit.isEnabled) 1.0f else 0.5f
                }

                binding.btRecruit.text = context.getString(
                    if (item.buttonUiState == PostContentButtonUiState.Writer) R.string.util_dialog_button_confirm
                    else R.string.project_support
                )
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
        private val tagAdapter = TagListAdapter(onClickItem = { position, item -> })

        init {
            binding.recyclerViewTag.adapter = tagAdapter
        }

        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.Item) {
                binding.tvTitle.text = item.title
                binding.tvRegisterDatetime.text = item.registeredDate
                binding.tvHits.text = item.views.toString()
                binding.tvContent.text = item.description

                val tagListItems = mutableListOf<TagListItem>()
                tagListItems.addAll(item.tags?.map { TagListItem.ContentItem(tagName = it) }
                    ?: emptyList())
                tagAdapter.submitList(tagListItems)
            }
        }
    }

    class PostGroupTypeItemViewHolder(
        private val context: Context,
        private val binding: ItemPostGroupTypeBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.GroupTypeItem) {
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
            }
        }
    }

    class PostImageViewHolder(
        context: Context,
        private val binding: ItemPostImageBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.ImageItem) {
                binding.ivAddImage.load(item.imageUrl)
                binding.ivImageBackground.load(item.imageUrl)
            }
        }
    }

    class PostTitleViewHolder(
        private val context: Context,
        private val binding: ItemPostTitleBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.TitleItem) {
                binding.tvTitle.text = context.getString(item.titleRes)
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

}