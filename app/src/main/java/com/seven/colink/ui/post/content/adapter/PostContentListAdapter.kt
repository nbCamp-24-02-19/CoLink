package com.seven.colink.ui.post.content.adapter

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemGroupBoardTitleBinding
import com.seven.colink.databinding.ItemPostCommentBinding
import com.seven.colink.databinding.ItemPostCommentSendBinding
import com.seven.colink.databinding.ItemPostCommentTitleBinding
import com.seven.colink.databinding.ItemPostContentBinding
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemPostMessageBinding
import com.seven.colink.databinding.ItemPostRecruitBinding
import com.seven.colink.databinding.ItemPostSelectionTypeBinding
import com.seven.colink.databinding.ItemPostSubTitleBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostContentViewTypeItem

class PostContentListAdapter(
    private val onClickItem: (PostContentItem) -> Unit,
    private val onClickButton: (PostContentItem, ContentButtonUiState) -> Unit,
    private val onClickView: (View) -> Unit,
    private val onClickCommentButton: (String) -> Unit,
    private val onClickCommentDeleteButton: (String) -> Unit,
    private val onClickCommentEditButton: (String, String) -> Unit,
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
                    oldItem.userInfo?.uid == newItem.userInfo?.uid
                }

                oldItem is PostContentItem.AdditionalInfo && newItem is PostContentItem.AdditionalInfo -> {
                    oldItem.key == newItem.key
                }

                oldItem is PostContentItem.CommentTitle && newItem is PostContentItem.CommentTitle -> {
                    oldItem.count == newItem.count
                }

                oldItem is PostContentItem.CommentItem && newItem is PostContentItem.CommentItem -> {
                    oldItem.key == newItem.key
                }

                oldItem is PostContentItem.CommentSendItem && newItem is PostContentItem.CommentSendItem -> {
                    oldItem == newItem
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
        is PostContentItem.CommentTitle -> PostContentViewTypeItem.COMMENTTITEL
        is PostContentItem.CommentItem -> PostContentViewTypeItem.COMMENT
        is PostContentItem.CommentSendItem -> PostContentViewTypeItem.COMMENTSEND
        else -> PostContentViewTypeItem.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        when (PostContentViewTypeItem.from(viewType)) {
            PostContentViewTypeItem.ITEM -> PostItemViewHolder(
                ItemPostContentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem
            )

            PostContentViewTypeItem.RECRUIT -> PostRecruitItemViewHolder(
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
                ItemGroupBoardTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                this,
                onClickView
            )

            PostContentViewTypeItem.SUB_TITLE -> PostSubTitleViewHolder(
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

            PostContentViewTypeItem.COMMENTTITEL -> PostCommentTitleViewHolder(
                ItemPostCommentTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            PostContentViewTypeItem.COMMENT -> PostCommentViewHolder(
                ItemPostCommentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickCommentDeleteButton,
                onClickCommentEditButton
            )

            PostContentViewTypeItem.COMMENTSEND -> PostCommentSendViewHolder(
                ItemPostCommentSendBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickCommentButton
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
        private val onClickItem: (PostContentItem) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.MemberItem) {
                binding.ivUser.load(item.userInfo?.photoUrl)
                binding.ivUser.clipToOutline = true
                binding.tvUserName.text = item.userInfo?.name
                binding.tvUserGrade.text = item.userInfo?.grade.toString()
                item.userInfo?.level?.let { binding.ivLevelDiaIcon.setLevelIcon(it) }
                binding.tvLevelDiaIcon.text = item.userInfo?.level.toString()
                binding.tvUserIntroduction.text = item.userInfo?.info

                binding.root.setOnClickListener {
                    onClickItem(item)
                }
            }
        }
    }

    class PostRecruitItemViewHolder(
        private val binding: ItemPostRecruitBinding,
        private val onClickButton: (PostContentItem, ContentButtonUiState) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.RecruitItem) {
                with(binding) {
                    tvRecruitType.apply {
                        text = item.recruit.type
                        isVisible = item.recruit.type?.isNotEmpty() == true
                    }

                    tvNowPersonnel.text = "${item.recruit.nowPersonnel}"
                    tvMaxPersonnel.text = "${item.recruit.maxPersonnel}"

                    if (item.buttonUiState == ContentButtonUiState.User) {
                        btRecruit.isEnabled = item.recruit.nowPersonnel < (item.recruit.maxPersonnel ?: -1)
                        btRecruit.alpha = if (btRecruit.isEnabled) 1.0f else 0.5f
                    }

                    btRecruit.visibility = if (item.buttonUiState == ContentButtonUiState.Manager) View.GONE else View.VISIBLE

                    btRecruit.setOnClickListener { onClickButton(item, item.buttonUiState) }
                }

            }
        }
    }

    class PostItemViewHolder(
        private val binding: ItemPostContentBinding,
        private val onClickItem: (PostContentItem) -> Unit,
    ) : PostViewHolder(binding.root) {
        private val tagAdapter = TagListAdapter(onClickItem = { _ -> })

        init {
            binding.recyclerViewTag.adapter = tagAdapter
        }

        override fun onBind(item: PostContentItem) {
            val context = binding.root.context
            if (item is PostContentItem.Item) {
                with(binding) {
                    ivLike.setOnClickListener{
                        onClickItem(item)
                        if (!item.isLike) {
                            ivLike.setImageResource(R.drawable.ic_heart)
                        } else {
                            ivLike.setImageResource(R.drawable.ic_heart_clicked)
                        }
                    }
                    tvTitle.text = item.title
                    tvRegisterDatetime.text = item.registeredDate
                    tvHits.text = item.views.toString()
                    tvContent.text = item.description
                    ivAddImage.load(item.imageUrl)
                    ivImageBackground.load(item.imageUrl)

                    val (groupTypeTextRes, textColorResId) = when (item.groupType) {
                        GroupType.PROJECT -> R.string.bt_project to R.color.forth_color
                        else -> R.string.bt_study to R.color.study_color
                    }

                    tvGroupType.apply {
                        setText(groupTypeTextRes)
                        backgroundTintList = ContextCompat.getColorStateList(context, textColorResId)
                    }

                    val tagListItems = item.tags?.map { TagListItem.ContentItem(name = it) } ?: emptyList()
                    tagAdapter.submitList(tagListItems)
                }

            }
        }
    }

    class PostTitleViewHolder(
        private val binding: ItemGroupBoardTitleBinding,
        private val adapter: PostContentListAdapter,
        private val onClickView: (View) -> Unit,
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            val context = binding.root.context
            if (item is PostContentItem.TitleItem) {
                binding.tvTitle.text = context.getString(item.titleRes)

                when (item.viewType) {
                    GroupContentViewType.POST_ITEM -> {
                        binding.ivApplyRequest.isVisible = false
                        binding.tvApplyRequest.isVisible = false
                    }

                    GroupContentViewType.MEMBER_ITEM -> {
                        val buttonState = adapter.getButtonUiState()
                        val isManager = buttonState == ContentButtonUiState.Manager

                        binding.ivApplyRequest.isVisible = isManager
                        binding.tvApplyRequest.isVisible = isManager

                        binding.ivNotify.isVisible = isManager && adapter.countPostApplyRequester() > 0
                        binding.tvApplyRequest.setOnClickListener {
                            if (isManager) {
                                onClickView(it)
                            }
                        }
                    }

                    else -> Unit
                }

            }
        }
    }

    class PostSubTitleViewHolder(
        private val binding: ItemPostSubTitleBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            val context = binding.root.context
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
                binding.etPrecautions.setText(item.precautions)
                binding.etRecruitInfo.setText(item.recruitInfo)
                binding.etPrecautions.inputType = InputType.TYPE_NULL
                binding.etRecruitInfo.inputType = InputType.TYPE_NULL
                val startDateText = item.startDate
                val endDateText = item.endDate
                if (!startDateText.isNullOrBlank() && !endDateText.isNullOrBlank()) {
                    binding.etEstimatedSchedule.setText("$startDateText~$endDateText")
                }
            }
        }
    }

    class PostCommentTitleViewHolder(
        private val binding: ItemPostCommentTitleBinding
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.CommentTitle){
                val context = binding.root.context
                binding.tvPostCommentCount.text = context.getString(item.count)
            }
        }
    }

    class PostCommentViewHolder(
        private val binding: ItemPostCommentBinding,
        private val onClickCommentDeleteButton: (String) -> Unit,
        private val onClickCommentEditButton: (String, String) -> Unit,
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            val context = binding.root.context
            if (item is PostContentItem.CommentItem){
                binding.tvPostCommentName.text = item.name
                binding.tvPostComment.text = item.description
                binding.tvPostCommentTime.text = item.registeredDate
                binding.ivPostCommentProfile.load(item.profile)
                binding.ivPostCommentProfile.clipToOutline = true
                binding.tvPostCommentDelete.isVisible = item.buttonUiState
                binding.tvPostCommentDelete.setOnClickListener {
                    val popupMenu = PopupMenu(context, it)
                    popupMenu.menuInflater.inflate(R.menu.option, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener {
                        when(it.itemId){
                            R.id.delete_option -> {
                                onClickCommentDeleteButton(item.key)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.edit_option -> {
                                binding.etPostComment.text.clear()
                                binding.etPostComment.visibility = View.VISIBLE
                                binding.btnPostCommentEdit.visibility = View.VISIBLE
                                binding.tvPostComment.visibility = View.GONE
                                binding.btnPostCommentEdit.setOnClickListener {
                                    onClickCommentEditButton(item.key, binding.etPostComment.text.toString())
                                    binding.tvPostComment.visibility = View.VISIBLE
                                    binding.etPostComment.visibility = View.GONE
                                    binding.btnPostCommentEdit.visibility = View.GONE
                                    val imm:InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(binding.etPostComment.windowToken, 0)
                                }
                                return@setOnMenuItemClickListener true
                            }
                            else ->{
                                return@setOnMenuItemClickListener  false
                            }
                        }
                    }
                    popupMenu.show()
                }

            }
        }
    }

    class PostCommentSendViewHolder(
        private val binding: ItemPostCommentSendBinding,
        private val onClickCommentButton: (String) -> Unit
    ) : PostViewHolder(binding.root) {
        override fun onBind(item: PostContentItem) {
            if (item is PostContentItem.CommentSendItem){
                binding.btnPostCommentSend.setOnClickListener {
                    onClickCommentButton(binding.etPostCommentSend.text.toString())
                    binding.etPostCommentSend.text.clear()
                }
            }
        }
    }


    private fun getButtonUiState(): ContentButtonUiState? {
        val recruitItems = currentList.filterIsInstance<PostContentItem.RecruitItem>()
        val specificRecruitItem: PostContentItem.RecruitItem? = recruitItems.firstOrNull()

        return specificRecruitItem?.buttonUiState
    }

    private fun countPostApplyRequester(): Int {
        val recruitItems = currentList.filterIsInstance<PostContentItem.RecruitItem>()

        return recruitItems.sumOf { recruitItem ->
            recruitItem.recruit.applicationInfos?.count { it.applicationStatus == ApplicationStatus.PENDING }
                ?: 0
        }
    }
}