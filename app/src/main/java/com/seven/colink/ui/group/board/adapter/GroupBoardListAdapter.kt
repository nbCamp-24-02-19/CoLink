package com.seven.colink.ui.group.board.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemGroupBoardContentBinding
import com.seven.colink.databinding.ItemGroupBoardTitleBinding
import com.seven.colink.databinding.ItemHomeBottomBinding
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemPostSubTitleBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.group.board.GroupBoardItem
import com.seven.colink.ui.group.board.GroupContentViewType
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.convert.convertCalculateDays
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

class GroupBoardListAdapter(
    private val context: Context,
    private val onClickItem: (Int, GroupBoardItem) -> Unit
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
                context,
                ItemPostMemberInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem,
            )

            GroupContentViewType.POST_ITEM -> PostItemViewHolder(
                context,
                ItemHomeBottomBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem
            )

            GroupContentViewType.TITLE -> GroupTitleViewHolder(
                context,
                ItemGroupBoardTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                this
            )

            GroupContentViewType.SUB_TITLE -> GroupSubTitleViewHolder(
                context,
                ItemPostSubTitleBinding.inflate(
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
        else -> GroupContentViewType.UNKNOWN
    }.ordinal


    class GroupItemViewHolder(
        private val context: Context,
        private val binding: ItemGroupBoardContentBinding,
        private val onClickItem: (Int, GroupBoardItem) -> Unit
    ) : GroupViewHolder(binding.root) {

        private val tagAdapter = TagListAdapter { _, _ -> }

        init {
            binding.recyclerViewTags.adapter = tagAdapter
        }

        override fun onBind(item: GroupBoardItem) {
            if (item !is GroupBoardItem.GroupItem) return

            with(binding) {
                ivGroupImage.load(item.imageUrl)
                val tagListItems = item.tags?.map { TagListItem.ContentItem(tagName = it) } ?: emptyList()
                tagAdapter.submitList(tagListItems)

                btStatus.visibility = if (item.status != ProjectStatus.END) View.GONE else View.VISIBLE
                btStatus.isEnabled = item.isOwner ?: false
                btStatus.setOnClickListener { onClickItem(adapterPosition, item) }

                tvTeamName.text = item.teamName
                tvDescription.text = item.description

                val days = item.startDate?.convertCalculateDays()

                val (backgroundTint, groupTypeString) = when (item.groupType) {
                    GroupType.PROJECT -> R.color.forth_color to context.getString(R.string.project_kor)
                    GroupType.STUDY -> R.color.study_color to context.getString(R.string.study_kor)
                    else -> R.color.enable_stroke to context.getString(R.string.unknown)
                }

                tvGroupType.backgroundTintList = ContextCompat.getColorStateList(context, backgroundTint)
                tvGroupType.text = "$groupTypeString$days"
            }
        }
    }


    class MemberItemViewHolder(
        private val context: Context,
        private val binding: ItemPostMemberInfoBinding,
        private val onClickItem: (Int, GroupBoardItem) -> Unit,
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.MemberItem) {
                binding.tvUserName.text = item.userInfo.name
                binding.tvUserGrade.text = item.userInfo.grade.toString()
                item.userInfo.level?.let { binding.ivLevelDiaIcon.setLevelIcon(it) }
                binding.tvLevelDiaIcon.text = item.userInfo.level.toString()
                binding.tvUserIntroduction.text = item.userInfo.info
            }
        }
    }


    class PostItemViewHolder(
        private val context: Context,
        private val binding: ItemHomeBottomBinding,
        private val onClickItem: (Int, GroupBoardItem) -> Unit
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.PostItem) {
                binding.tvHomeBottomTitle.text = item.post.title
                binding.tvHomeBottomDes.text = item.post.description
                binding.ivHomeBottomThumubnail.load(item.post.imageUrl)

                when (item.post.groupType) {
                    GroupType.PROJECT -> {
                        binding.tvHomeBottomProject.visibility = View.VISIBLE
                        binding.tvHomeBottomStudy.visibility = View.INVISIBLE
                    }

                    GroupType.STUDY -> {
                        binding.tvHomeBottomProject.visibility = View.INVISIBLE
                        binding.tvHomeBottomStudy.visibility = View.VISIBLE
                    }

                    else -> {
                        binding.tvHomeBottomProject.visibility = View.INVISIBLE
                        binding.tvHomeBottomStudy.visibility = View.INVISIBLE
                    }
                }

                binding.root.setOnClickListener {
                    onClickItem(adapterPosition, item)
                }
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
        private val adapter: GroupBoardListAdapter
    ) : GroupViewHolder(binding.root) {

        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.TitleItem) {

                binding.tvTitle.text = context.getString(item.titleRes)

                when (item.viewType) {
                    GroupContentViewType.POST_ITEM -> {
                        binding.tvMemberPersonnel.visibility = View.INVISIBLE
                    }

                    GroupContentViewType.MEMBER_ITEM -> {
                        binding.tvMemberPersonnel.visibility = View.VISIBLE

                        val memberListSize = adapter.countMemberItems()

                        binding.tvMemberPersonnel.text = "${memberListSize}/${LIMITED_PEOPLE}"
                    }

                    else -> Unit
                }
            }
        }
    }


    class GroupSubTitleViewHolder(
        private val context: Context,
        private val binding: ItemPostSubTitleBinding
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.SubTitleItem) {
                binding.tvSubTitle.text = item.title
            }
        }
    }

    private fun countMemberItems(): Int =
        currentList.filterIsInstance<GroupBoardItem.MemberItem>().size

}




