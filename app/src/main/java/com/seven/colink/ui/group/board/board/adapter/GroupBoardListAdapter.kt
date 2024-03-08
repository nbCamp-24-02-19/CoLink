package com.seven.colink.ui.group.board.board.adapter

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
import com.seven.colink.databinding.ItemGroupBoardContentBinding
import com.seven.colink.databinding.ItemGroupBoardTitleBinding
import com.seven.colink.databinding.ItemPostMessageBinding
import com.seven.colink.databinding.ItemPostPaddingBinding
import com.seven.colink.databinding.ItemPostSubTitleBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.databinding.UtilMemberInfoDialogItemBinding
import com.seven.colink.ui.group.board.board.GroupBoardItem
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.convert.convertCalculateDays
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

class GroupBoardListAdapter(
    private val context: Context,
    private val onClickItem: (Int, GroupBoardItem) -> Unit,
    private val onClickView: (GroupBoardItem, View) -> Unit,
) : ListAdapter<GroupBoardItem, GroupBoardListAdapter.GroupViewHolder>(
    object : DiffUtil.ItemCallback<GroupBoardItem>() {
        override fun areItemsTheSame(
            oldItem: GroupBoardItem,
            newItem: GroupBoardItem
        ): Boolean =
            when {
                oldItem is GroupBoardItem.GroupItem && newItem is GroupBoardItem.GroupItem -> {
                    oldItem.key == newItem.key
                }

                oldItem is GroupBoardItem.PostItem && newItem is GroupBoardItem.PostItem -> {
                    oldItem.post.key == newItem.post.key
                }

                oldItem is GroupBoardItem.MemberItem && newItem is GroupBoardItem.MemberItem -> {
                    oldItem.userInfo.uid == newItem.userInfo.uid
                }

                else -> oldItem == newItem
            }

        override fun areContentsTheSame(
            oldItem: GroupBoardItem,
            newItem: GroupBoardItem
        ): Boolean = oldItem == newItem
    }
) {

    abstract class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: GroupBoardItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder =
        when (GroupContentViewType.from(viewType)) {
            GroupContentViewType.GROUP_ITEM -> GroupItemViewHolder(
                context,
                ItemGroupBoardContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem
            )

            GroupContentViewType.MEMBER_ITEM -> MemberItemViewHolder(
                UtilMemberInfoDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem,
            )

            GroupContentViewType.POST_ITEM -> PostItemViewHolder(
                ItemPostPaddingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem
            )

            GroupContentViewType.TITLE -> GroupTitleViewHolder(
                context,
                ItemGroupBoardTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                this,
                onClickView
            )

            GroupContentViewType.SUB_TITLE -> GroupSubTitleViewHolder(
                ItemPostSubTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            GroupContentViewType.MESSAGE -> GroupMessageViewHolder(
                ItemPostMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> GroupUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
        }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroupBoardItem.GroupItem -> GroupContentViewType.GROUP_ITEM
        is GroupBoardItem.PostItem -> GroupContentViewType.POST_ITEM
        is GroupBoardItem.MemberItem -> GroupContentViewType.MEMBER_ITEM
        is GroupBoardItem.TitleItem -> GroupContentViewType.TITLE
        is GroupBoardItem.SubTitleItem -> GroupContentViewType.SUB_TITLE
        is GroupBoardItem.MessageItem -> GroupContentViewType.MESSAGE
        else -> GroupContentViewType.UNKNOWN
    }.ordinal


    class GroupItemViewHolder(
        private val context: Context,
        private val binding: ItemGroupBoardContentBinding,
        private val onClickItem: (Int, GroupBoardItem) -> Unit
    ) : GroupViewHolder(binding.root) {
        private val tagAdapter = TagListAdapter {_ -> }
        init {
            binding.recyclerViewTags.adapter = tagAdapter
        }
        override fun onBind(item: GroupBoardItem) {
            if (item !is GroupBoardItem.GroupItem) return

            with(binding) {
                ivGroupImage.load(item.imageUrl)
                tagAdapter.submitList(item.tags?.map { TagListItem.ContentItem(name = it) }
                    ?: emptyList())

                btStatus.visibility =
                    if (item.status != ProjectStatus.END) View.GONE else View.VISIBLE
                btStatus.isEnabled = item.isOwner ?: false
                btStatus.setOnClickListener { onClickItem(adapterPosition, item) }

                tvTeamName.text = item.teamName
                tvDescription.text = item.description

                var days = ""
                if (item.status == ProjectStatus.START) {
                    days = item.startDate?.convertCalculateDays() ?: ""
                }

                val (backgroundTint, groupTypeString) = when (item.groupType) {
                    GroupType.PROJECT -> R.color.forth_color to context.getString(R.string.bt_project)
                    GroupType.STUDY -> R.color.study_color to context.getString(R.string.bt_study)
                    else -> R.color.enable_stroke to ""
                }

                tvGroupType.backgroundTintList =
                    ContextCompat.getColorStateList(context, backgroundTint)
                tvGroupType.text = "$groupTypeString$days"
            }
        }
    }


    class MemberItemViewHolder(
        private val binding: UtilMemberInfoDialogItemBinding,
        private val onClickItem: (Int, GroupBoardItem) -> Unit,
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.MemberItem) {
                binding.includePostMemberInfo.tvUserName.text = item.userInfo.name
                binding.includePostMemberInfo.tvUserGrade.text = item.userInfo.grade.toString()
                item.userInfo.level?.let { binding.includePostMemberInfo.ivLevelDiaIcon.setLevelIcon(it) }
                binding.includePostMemberInfo.tvLevelDiaIcon.text = item.userInfo.level.toString()
                binding.includePostMemberInfo.tvUserIntroduction.text = item.userInfo.info
                binding.root.setOnClickListener { onClickItem(adapterPosition, item) }

                binding.includeDialogButton.btApproval.isVisible = item.isManagementButtonVisible
                binding.includeDialogButton.btRefuse.isVisible = item.isManagementButtonVisible
            }
        }
    }


    class PostItemViewHolder(
        private val binding: ItemPostPaddingBinding,
        private val onClickItem: (Int, GroupBoardItem) -> Unit
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.PostItem) {
                val post = item.post

                binding.tvHomeBottomTitle.text = post.title
                binding.tvHomeBottomDes.text = post.description
                binding.ivHomeBottomThumubnail.load(post.imageUrl)

                val isProject = post.groupType == GroupType.PROJECT
                val isStudy = post.groupType == GroupType.STUDY

                binding.tvHomeBottomProject.visibility =
                    if (isProject) View.VISIBLE else View.INVISIBLE
                binding.tvHomeBottomStudy.visibility = if (isStudy) View.VISIBLE else View.INVISIBLE

                val formattedTags = post.tags?.joinToString(" # ") { it }
                binding.tvHomeBottomKind.text =
                    if (formattedTags?.isNotEmpty() == true) "# $formattedTags" else ""

                binding.root.setOnClickListener { onClickItem(adapterPosition, item) }
            }
        }
    }

    class GroupUnknownViewHolder(binding: ItemUnknownBinding) :
        GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) = Unit
    }

    class GroupTitleViewHolder(
        private val context: Context,
        private val binding: ItemGroupBoardTitleBinding,
        private val adapter: GroupBoardListAdapter,
        private val onClickView: (GroupBoardItem, View) -> Unit,
    ) : GroupViewHolder(binding.root) {

        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.TitleItem) {
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


    class GroupSubTitleViewHolder(
        private val binding: ItemPostSubTitleBinding
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.SubTitleItem) {
                binding.tvSubTitle.text = item.title
            }
        }
    }

    class GroupMessageViewHolder(
        private val binding: ItemPostMessageBinding
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.MessageItem) {
                binding.tvMessage.text = item.message
            }
        }
    }

    private fun countMemberItems(): Int =
        currentList.filterIsInstance<GroupBoardItem.MemberItem>().size

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




