package com.seven.colink.ui.group.board.board.adapter

import android.content.Context
import android.text.InputType
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
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemPostMessageBinding
import com.seven.colink.databinding.ItemPostPaddingBinding
import com.seven.colink.databinding.ItemPostSelectionTypeBinding
import com.seven.colink.databinding.ItemPostSubTitleBinding
import com.seven.colink.databinding.ItemPostTitleBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.databinding.UtilMemberInfoDialogItemBinding
import com.seven.colink.ui.group.board.board.GroupBoardItem
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class GroupBoardListAdapter(
    private val onClickItem: (GroupBoardItem) -> Unit,
    private val onClickView: (GroupBoardItem, View) -> Unit,
    private val onChangeStatus: (GroupBoardItem, ProjectStatus) -> Unit
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

                oldItem is GroupBoardItem.GroupOptionItem && newItem is GroupBoardItem.GroupOptionItem -> {
                    oldItem.key == newItem.key
                }

                oldItem is GroupBoardItem.PostItem && newItem is GroupBoardItem.PostItem -> {
                    oldItem.post.key == newItem.post.key
                }

                oldItem is GroupBoardItem.MemberItem && newItem is GroupBoardItem.MemberItem -> {
                    oldItem.userInfo.uid == newItem.userInfo.uid
                }

                oldItem is GroupBoardItem.MemberApplicationInfoItem && newItem is GroupBoardItem.MemberApplicationInfoItem -> {
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
                ItemGroupBoardContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickView,
                onChangeStatus
            )

            GroupContentViewType.OPTION_ITEM -> GroupOptionItemViewHolder(
                ItemPostSelectionTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
            )

            GroupContentViewType.MEMBER_ITEM -> MemberItemViewHolder(
                ItemPostMemberInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem,
            )

            GroupContentViewType.APPLICATION_INFO -> MemberApplicationInfoItemViewHolder(
                UtilMemberInfoDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickView,
            )

            GroupContentViewType.POST_ITEM -> PostItemViewHolder(
                ItemPostPaddingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem
            )

            GroupContentViewType.TITLE -> GroupTitleViewHolder(
                ItemGroupBoardTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                this,
                onClickView
            )

            GroupContentViewType.SINGLE_TITLE -> GroupTitleSingleViewHolder(
                ItemPostTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
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
        is GroupBoardItem.GroupOptionItem -> GroupContentViewType.OPTION_ITEM
        is GroupBoardItem.PostItem -> GroupContentViewType.POST_ITEM
        is GroupBoardItem.MemberItem -> GroupContentViewType.MEMBER_ITEM
        is GroupBoardItem.MemberApplicationInfoItem -> GroupContentViewType.APPLICATION_INFO
        is GroupBoardItem.TitleItem -> GroupContentViewType.TITLE
        is GroupBoardItem.TitleSingleItem -> GroupContentViewType.SINGLE_TITLE
        is GroupBoardItem.SubTitleItem -> GroupContentViewType.SUB_TITLE
        is GroupBoardItem.MessageItem -> GroupContentViewType.MESSAGE
        else -> GroupContentViewType.UNKNOWN
    }.ordinal

    class GroupItemViewHolder(
        private val binding: ItemGroupBoardContentBinding,
        private val onClickView: (GroupBoardItem, View) -> Unit,
        private val onChangeStatus: (GroupBoardItem, ProjectStatus) -> Unit
    ) : GroupViewHolder(binding.root) {
        private val context = binding.root.context
        private val tagAdapter = TagListAdapter { _ -> }

        init {
            binding.recyclerViewTags.adapter = tagAdapter
        }

        override fun onBind(item: GroupBoardItem) {
            if (item !is GroupBoardItem.GroupItem) return
            with(binding) {
                ivGroupImage.load(item.imageUrl)
                ivGroupImage.clipToOutline = true
                tagAdapter.submitList(item.tags?.map { TagListItem.ContentItem(name = it) }
                    ?: emptyList())
                btStatus.isVisible = item.isOwner ?: false
                ivCalendar.setOnClickListener { onClickView(item, it) }
                tvCalendar.setOnClickListener { onClickView(item, it) }
                tvTeamName.text = item.teamName
                etDescription.text = item.description
                btStatus.setOnClickListener {
                    onChangeStatus(item, item.status)
                }
                setProgress(item.status)
                setStatusTextColors(context, item.status)
                tvStatusMessage.text = getStatusMessage(context, item.status, item.startDate)
                btStatus.text = getStatusButtonText(item.status)
            }
        }

        private fun setProgress(status: ProjectStatus) {
            when (status) {
                ProjectStatus.RECRUIT -> binding.progressBar.progress = 33
                ProjectStatus.START -> binding.progressBar.progress = 66
                ProjectStatus.END -> binding.progressBar.progress = 100
            }
        }

        private fun setStatusTextColors(context: Context, status: ProjectStatus) {
            with(binding) {
                val textColor = ContextCompat.getColorStateList(context, R.color.black)
                tvStatusRecruit.setTextColor(textColor)
                tvStatusOngoing.setTextColor(textColor)
                tvStatusCompletion.setTextColor(textColor)

                when (status) {
                    ProjectStatus.RECRUIT -> {
                        tvStatusRecruit.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.black
                            )
                        )
                        tvStatusOngoing.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.enabled_color
                            )
                        )
                        tvStatusCompletion.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.enabled_color
                            )
                        )
                    }

                    ProjectStatus.START -> {
                        tvStatusRecruit.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.enabled_color
                            )
                        )
                        tvStatusOngoing.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.black
                            )
                        )
                        tvStatusCompletion.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.enabled_color
                            )
                        )
                    }

                    ProjectStatus.END -> {
                        tvStatusRecruit.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.enabled_color
                            )
                        )
                        tvStatusOngoing.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.enabled_color
                            )
                        )
                        tvStatusCompletion.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.black
                            )
                        )
                    }
                }
            }
        }

        private fun getStatusMessage(
            context: Context,
            status: ProjectStatus,
            startDate: String?
        ): String {
            val daysSinceStart = startDate?.daysSinceTargetDate()?.toInt() ?: 0
            val message = when (status) {
                ProjectStatus.RECRUIT ->
                    if (daysSinceStart > 0) {
                        context.getString(
                            R.string.progress_status_recruit,
                            daysSinceStart.toString()
                        )
                    } else {
                        context.getString(
                            R.string.progress_status_recruit_d_day,
                            daysSinceStart.absoluteValue.toString()
                        )
                    }

                ProjectStatus.START ->
                    context.getString(R.string.progress_status_ongoing, daysSinceStart.toString())

                ProjectStatus.END ->
                    context.getString(R.string.progress_status_completion)
            }
            return message
        }

        private fun getStatusButtonText(status: ProjectStatus): String {
            return when (status) {
                ProjectStatus.RECRUIT -> context.getString(R.string.project_start)
                ProjectStatus.START -> context.getString(R.string.project_end)
                ProjectStatus.END -> context.getString(R.string.promotion)
            }
        }

        private fun String?.daysSinceTargetDate(): Long {
            if (this.isNullOrBlank()) return 0
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val targetDate = LocalDate.parse(this, formatter)
                val currentDate = LocalDate.now()
                ChronoUnit.DAYS.between(targetDate, currentDate)
            } catch (e: DateTimeParseException) {
                0
            }
        }
    }


    class GroupOptionItemViewHolder(
        private val binding: ItemPostSelectionTypeBinding,
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.GroupOptionItem) {
                binding.etPrecautions.setText(item.precautions)
                binding.etPrecautions.inputType = InputType.TYPE_NULL
                binding.etRecruitInfo.setText(item.recruitInfo)
                binding.etRecruitInfo.inputType = InputType.TYPE_NULL
                binding.layoutDate.visibility = View.GONE
            }
        }
    }

    class MemberItemViewHolder(
        private val binding: ItemPostMemberInfoBinding,
        private val onClickItem: (GroupBoardItem) -> Unit,
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.MemberItem) {
                with(binding) {
                    ivUser.load(item.userInfo.photoUrl)
                    ivUser.clipToOutline = true
                    tvUserName.text = item.userInfo.name
                    tvUserGrade.text = item.userInfo.grade.toString()
                    item.userInfo.level?.let { ivLevelDiaIcon.setLevelIcon(it) }
                    tvLevelDiaIcon.text = item.userInfo.level.toString()
                    tvUserIntroduction.text = item.userInfo.info
                    root.setOnClickListener { onClickItem(item) }
                }
            }
        }
    }

    class MemberApplicationInfoItemViewHolder(
        private val binding: UtilMemberInfoDialogItemBinding,
        private val onClickView: (GroupBoardItem, View) -> Unit,
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            if (item is GroupBoardItem.MemberApplicationInfoItem) {
                binding.includePostMemberInfo.ivUser.load(item.userInfo.photoUrl)
                binding.includePostMemberInfo.ivUser.clipToOutline = true
                binding.includePostMemberInfo.tvUserName.text = item.userInfo.name
                binding.includePostMemberInfo.tvUserGrade.text = item.userInfo.grade.toString()
                item.userInfo.level?.let {
                    binding.includePostMemberInfo.ivLevelDiaIcon.setLevelIcon(
                        it
                    )
                }
                binding.includePostMemberInfo.tvLevelDiaIcon.text = item.userInfo.level.toString()
                binding.includePostMemberInfo.tvUserIntroduction.text = item.userInfo.info
                binding.root.setOnClickListener { onClickView(item, it) }
                binding.includeDialogButton.btApproval.setOnClickListener {
                    onClickView(item, it)
                }
            }
        }
    }

    class PostItemViewHolder(
        private val binding: ItemPostPaddingBinding,
        private val onClickItem: (GroupBoardItem) -> Unit
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

                binding.root.setOnClickListener { onClickItem(item) }
            }
        }
    }

    class GroupUnknownViewHolder(binding: ItemUnknownBinding) :
        GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) = Unit
    }

    class GroupTitleViewHolder(
        private val binding: ItemGroupBoardTitleBinding,
        private val adapter: GroupBoardListAdapter,
        private val onClickView: (GroupBoardItem, View) -> Unit,
    ) : GroupViewHolder(binding.root) {

        override fun onBind(item: GroupBoardItem) {
            val context = binding.root.context
            if (item is GroupBoardItem.TitleItem) {
                binding.tvTitle.text = context.getString(item.titleRes)

                when (item.viewType) {
                    GroupContentViewType.POST_ITEM -> {
                        binding.ivApplyRequest.visibility = View.INVISIBLE
                        binding.tvApplyRequest.visibility = View.INVISIBLE
                    }

                    GroupContentViewType.MEMBER_ITEM -> {
                        val buttonState = item.buttonUiState
                        binding.ivApplyRequest.isVisible =
                            buttonState == ContentButtonUiState.Manager
                        binding.tvApplyRequest.isVisible =
                            buttonState == ContentButtonUiState.Manager

                        binding.ivNotify.isVisible = buttonState == ContentButtonUiState.Manager &&
                                adapter.countPostApplyRequester() > 0

                        binding.tvApplyRequest.setOnClickListener {
                            if (buttonState == ContentButtonUiState.Manager) {
                                onClickView(item, it)
                            }
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
                val context = binding.root.context
                binding.tvMessage.text = item.message?.let { context.getString(it) }
            }
        }
    }

    class GroupTitleSingleViewHolder(
        private val binding: ItemPostTitleBinding
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupBoardItem) {
            val context = binding.root.context
            if (item is GroupBoardItem.TitleSingleItem) {
                binding.tvTitle.text = context.getString(item.titleRes)
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