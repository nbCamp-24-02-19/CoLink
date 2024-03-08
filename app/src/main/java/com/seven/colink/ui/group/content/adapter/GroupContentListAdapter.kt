package com.seven.colink.ui.group.content.adapter

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemGroupContentBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.group.content.GroupContentItem
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

class GroupContentListAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val onClickItem: (GroupContentItem, View) -> Unit,
    private val onGroupImageClick: (String) -> Unit,
    private val tagAdapterOnClickItem: (Int, TagListItem) -> Unit,
    private val onSwitchChanged: (Boolean) -> Unit
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
        is GroupContentItem.GroupContent -> GroupContentViewType.GROUP_ITEM
        else -> GroupContentViewType.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupContentViewHolder =
        when (GroupContentViewType.from(viewType)) {
            GroupContentViewType.GROUP_ITEM -> GroupContentItemViewHolder(
                context,
                ItemGroupContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem,
                onGroupImageClick,
                tagAdapterOnClickItem,
                onSwitchChanged
            )

            else -> GroupUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
        }


    override fun onBindViewHolder(
        holder: GroupContentViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }

    class GroupContentItemViewHolder(
        private val context: Context,
        private val binding: ItemGroupContentBinding,
        private val onClickItem: (GroupContentItem, View) -> Unit,
        private val onGroupImageClick: (String) -> Unit,
        private val tagAdapterOnClickItem: (Int, TagListItem) -> Unit,
        private val onSwitchChanged: (Boolean) -> Unit
    ) : GroupContentViewHolder(binding.root) {

        private val tagAdapter = TagListAdapter { item -> tagAdapterOnClickItem(adapterPosition, item) }

        init {
            binding.recyclerViewTags.adapter = tagAdapter
            initializeEditorActionListener()
            binding.switchProjectStatus.setOnCheckedChangeListener { _, isChecked ->

                onSwitchChanged(isChecked)
            }
        }

        private fun initializeEditorActionListener() {
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

        fun getEtTitleText(): String = binding.etTitle.text.toString().trim()

        fun getEtDescriptionText(): String = binding.etDescription.text.toString().trim()

        override fun onBind(item: GroupContentItem) {
            if (item is GroupContentItem.GroupContent) {
                binding.ivGroupImage.load(item.selectedImageUrl ?: item.imageUrl)
                binding.etTitle.setText(item.teamName)
                binding.etDescription.setText(item.description)

                val tagListItems = item.tags?.map { TagListItem.Item(it.name) } ?: emptyList()
                tagAdapter.submitList(tagListItems)

                binding.ivGroupImage.setOnClickListener { onClickItem(item, it) }

                setupGroupTypeView(item.groupType ?: GroupType.UNKNOWN)

                binding.switchProjectStatus.isChecked = item.status == ProjectStatus.START
            }
        }

        private fun setupGroupTypeView(groupType: GroupType) {
            val (backgroundTint, typeNameResId) = when (groupType) {
                GroupType.PROJECT -> Pair(R.color.forth_color, R.string.bt_project)
                GroupType.STUDY -> Pair(R.color.study_color, R.string.bt_study)
                else -> Pair(R.color.enable_stroke, R.string.unknown)
            }

            binding.tvGroupType.backgroundTintList = ContextCompat.getColorStateList(context, backgroundTint)
            binding.tvGroupType.text = context.getString(typeNameResId)
        }
    }



    class GroupUnknownViewHolder(binding: ItemUnknownBinding) :
        GroupContentViewHolder(binding.root) {
        override fun onBind(item: GroupContentItem) = Unit
    }

    fun getEtTitleAndDescription(position: Int): Pair<String, String> {
        val viewHolder =
            recyclerView.findViewHolderForAdapterPosition(position) as? GroupContentItemViewHolder
        return if (viewHolder != null) {
            Pair(viewHolder.getEtTitleText(), viewHolder.getEtDescriptionText())
        } else {
            Pair("", "")
        }
    }
}