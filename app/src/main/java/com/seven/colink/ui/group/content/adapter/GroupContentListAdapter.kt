package com.seven.colink.ui.group.content.adapter

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemGroupContentBinding
import com.seven.colink.databinding.ItemGroupProjectStatusBinding
import com.seven.colink.databinding.ItemPostSelectionTypeBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.group.content.GroupContentItem
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

class GroupContentListAdapter(
    private val onClickItem: (View) -> Unit,
    private val onGroupImageClick: (String) -> Unit,
    private val tagAdapterOnClickItem: (TagListItem) -> Unit,
    private val onChangeStatus: (ProjectStatus) -> Unit,
    private val onChangedFocus: (Int, String, String, GroupContentItem) -> Unit,
) : ListAdapter<GroupContentItem, GroupContentListAdapter.GroupContentViewHolder>(
    object : DiffUtil.ItemCallback<GroupContentItem>() {
        override fun areItemsTheSame(
            oldItem: GroupContentItem,
            newItem: GroupContentItem
        ): Boolean =
            when {
                oldItem is GroupContentItem.GroupContent && newItem is GroupContentItem.GroupContent -> {
                    oldItem.key == newItem.key
                }

                oldItem is GroupContentItem.GroupOptionItem && newItem is GroupContentItem.GroupOptionItem -> {
                    oldItem.key == newItem.key
                }

                oldItem is GroupContentItem.GroupProjectStatus && newItem is GroupContentItem.GroupProjectStatus -> {
                    oldItem.status == newItem.status
                }

                else -> oldItem == newItem
            }

        override fun areContentsTheSame(
            oldItem: GroupContentItem,
            newItem: GroupContentItem
        ): Boolean = oldItem == newItem

    }
) {
    abstract class GroupContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: GroupContentItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroupContentItem.GroupContent -> GroupContentViewType.GROUP_ITEM.ordinal
        is GroupContentItem.GroupOptionItem -> GroupContentViewType.OPTION_ITEM.ordinal
        is GroupContentItem.GroupProjectStatus -> GroupContentViewType.PROJECT_STATUS.ordinal
        else -> GroupContentViewType.UNKNOWN.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupContentViewHolder =
        when (GroupContentViewType.from(viewType)) {
            GroupContentViewType.GROUP_ITEM -> GroupContentItemViewHolder(
                ItemGroupContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem,
                onGroupImageClick,
                tagAdapterOnClickItem,
                onChangedFocus
            )

            GroupContentViewType.OPTION_ITEM -> GroupOptionItemViewHolder(
                ItemPostSelectionTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onChangedFocus
            )

            GroupContentViewType.PROJECT_STATUS -> ProjectStatusItemViewHolder(
                ItemGroupProjectStatusBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onChangeStatus,
            )

            else -> GroupUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
        }

    override fun onBindViewHolder(holder: GroupContentViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.onBind(item)
        }
    }

    class GroupContentItemViewHolder(
        private val binding: ItemGroupContentBinding,
        private val onClickItem: (View) -> Unit,
        private val onGroupImageClick: (String) -> Unit,
        private val tagAdapterOnClickItem: (TagListItem) -> Unit,
        private val onChangedFocus: (Int, String, String, GroupContentItem) -> Unit,
    ) : GroupContentViewHolder(binding.root) {
        private var currentItem: GroupContentItem? = null
        private val tagAdapter =
            TagListAdapter { item -> tagAdapterOnClickItem(item) }
        private val editTexts
            get() = with(binding) {
                listOf(
                    etTitle,
                    etDescription
                )
            }
        init {
            binding.recyclerViewTags.adapter = tagAdapter
            setEditorActionListener()
            setTextChangeListener()
        }

        private fun setEditorActionListener() {
            binding.etGroupTag.setOnEditorActionListener { _, actionId, event ->
                if ((actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
                    && binding.etGroupTag.text.toString().trim().isNotBlank()
                ) {
                    onGroupImageClick(binding.etGroupTag.text.toString().trim())
                    binding.etGroupTag.text.clear()
                    return@setOnEditorActionListener true
                }
                false
            }
        }
        private fun setTextChangeListener() {
            editTexts.forEach { editText ->
                editText.addTextChangedListener {
                    notifyTextChange(
                        binding.etTitle.text.toString(),
                        binding.etDescription.text.toString(),
                        currentItem
                    )
                }
            }
        }

        override fun onBind(item: GroupContentItem) {
            if (item is GroupContentItem.GroupContent) {
                currentItem = item
                setupGroupTypeView(item.groupType ?: GroupType.UNKNOWN)
                binding.ivGroupImage.load(item.selectedImageUrl ?: item.imageUrl)
                binding.etTitle.setText(item.teamName)
                binding.etDescription.setText(item.description)
                val tagList = item.tags?.map { TagListItem.Item(it) } ?: emptyList()
                tagAdapter.submitList(tagList)
                binding.ivGroupImage.setOnClickListener { onClickItem(it) }
            }
        }

        private fun setupGroupTypeView(groupType: GroupType) {
            val context = binding.root.context
            val (backgroundTint, typeNameResId) = when (groupType) {
                GroupType.PROJECT -> Pair(R.color.forth_color, R.string.bt_project)
                GroupType.STUDY -> Pair(R.color.study_color, R.string.bt_study)
                else -> Pair(R.color.enable_stroke, R.string.unknown)
            }
            binding.tvGroupType.backgroundTintList =
                ContextCompat.getColorStateList(context, backgroundTint)
            binding.tvGroupType.text = context.getString(typeNameResId)
        }
        private fun notifyTextChange(title: String, description: String, item: GroupContentItem?) {
            item?.let { onChangedFocus(adapterPosition, title, description, it) }
        }
    }

    class GroupOptionItemViewHolder(
        private val binding: ItemPostSelectionTypeBinding,
        private val onChangedFocus: (Int, String, String, GroupContentItem) -> Unit
    ) : GroupContentViewHolder(binding.root) {
        private var currentItem: GroupContentItem? = null
        private val editTexts
            get() = with(binding) {
                listOf(
                    etPrecautions,
                    etRecruitInfo
                )
            }

        init {
            setTextChangeListener()
        }

        private fun setTextChangeListener() {
            editTexts.forEach { editText ->
                editText.addTextChangedListener {
                    notifyTextChange(
                        binding.etPrecautions.text.toString(),
                        binding.etRecruitInfo.text.toString(),
                        currentItem
                    )
                }
            }
        }

        override fun onBind(item: GroupContentItem) {
            if (item is GroupContentItem.GroupOptionItem) {
                currentItem = item
                binding.etPrecautions.setText(item.precautions)
                binding.etRecruitInfo.setText(item.recruitInfo)
            }
        }

        private fun notifyTextChange(
            precautions: String,
            description: String,
            item: GroupContentItem?
        ) {
            item?.let { onChangedFocus(adapterPosition, precautions, description, it) }
        }
    }

    class ProjectStatusItemViewHolder(
        private val binding: ItemGroupProjectStatusBinding,
        private val onChangeStatus: (ProjectStatus) -> Unit
    ) :
        GroupContentViewHolder(binding.root) {
        override fun onBind(item: GroupContentItem) {
            val context = binding.root.context
            if (item is GroupContentItem.GroupProjectStatus) {
                when (item.status) {
                    ProjectStatus.RECRUIT -> {
                        binding.btGroupProjectStatus.setBackgroundResource(R.drawable.bg_round_corner_8dp_stroke)
                        binding.btGroupProjectStatus.setTextColor(context.getColor(R.color.main_color))
                        binding.btGroupProjectStatus.text = "프로젝트 시작하기"
                    }

                    ProjectStatus.START -> {
                        binding.btGroupProjectStatus.setBackgroundResource(R.drawable.bg_round_corner_8dp_stroke)
                        binding.btGroupProjectStatus.setTextColor(context.getColor(R.color.main_color))
                        binding.btGroupProjectStatus.text = "프로젝트 종료하기"
                    }

                    ProjectStatus.END -> {
                        binding.btGroupProjectStatus.text = "프로젝트 종료하기"
                        binding.btGroupProjectStatus.setBackgroundResource(R.drawable.bg_round_corner_8dp_stroke_enabled)
                        binding.btGroupProjectStatus.setTextColor(context.getColor(R.color.enable_stroke))
                    }

                    else -> Unit
                }

                binding.btGroupProjectStatus.setOnClickListener {
                    when (item.status) {
                        ProjectStatus.RECRUIT -> {
                            onChangeStatus(ProjectStatus.START)
                        }

                        ProjectStatus.START -> {
                            onChangeStatus(ProjectStatus.END)
                        }

                        ProjectStatus.END -> {
                            onChangeStatus(ProjectStatus.RECRUIT)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    class GroupUnknownViewHolder(binding: ItemUnknownBinding) :
        GroupContentViewHolder(binding.root) {
        override fun onBind(item: GroupContentItem) = Unit
    }
}