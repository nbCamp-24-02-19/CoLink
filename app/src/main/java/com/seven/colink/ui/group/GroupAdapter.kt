package com.seven.colink.ui.group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemGroupEmptyListBinding
import com.seven.colink.databinding.ItemGroupGroupAddBinding
import com.seven.colink.databinding.ItemGroupGroupListBinding
import com.seven.colink.databinding.ItemGroupTitleBinding
import com.seven.colink.databinding.ItemHomeBottomBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.GroupViewType
import com.seven.colink.ui.group.GroupData
import com.seven.colink.util.convert.convertToDaysAgo

class GroupAdapter(
    private val context: Context,
    private val onClickItem: (Int, GroupData) -> Unit,
    private val onClickAddButton: (Int, GroupData) -> Unit,
) : ListAdapter<GroupData, GroupAdapter.GroupViewHolder>(
    object : DiffUtil.ItemCallback<GroupData>() {

        override fun areItemsTheSame(oldItem: GroupData, newItem: GroupData): Boolean =
            when {
                oldItem is GroupData.GroupTitle && newItem is GroupData.GroupTitle -> {
                    oldItem.title == newItem.title
                }

                oldItem is GroupData.GroupList && newItem is GroupData.GroupList -> {
                    oldItem.projectName == newItem.projectName
                }

                oldItem is GroupData.GroupAdd && newItem is GroupData.GroupAdd -> {
                    oldItem.addGroupText == newItem.addGroupText
                }

                oldItem is GroupData.GroupWant && newItem is GroupData.GroupWant -> {
                    oldItem.description == newItem.description
                }

                oldItem is GroupData.GroupEmpty && newItem is GroupData.GroupEmpty -> {
                    oldItem.text == newItem.text
                }
                else -> oldItem == newItem
            }

        override fun areContentsTheSame(
            oldItem: GroupData,
            newItem: GroupData
        ): Boolean = oldItem == newItem
    }
) {

    abstract class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: GroupData)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroupData.GroupTitle -> GroupViewType.TITLE
        is GroupData.GroupList -> GroupViewType.LIST
        is GroupData.GroupAdd -> GroupViewType.ADD
        is GroupData.GroupWant -> GroupViewType.WANT
        is GroupData.GroupEmpty -> GroupViewType.EMPTY
        else -> GroupViewType.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder =
        when (GroupViewType.from(viewType)) {
            GroupViewType.TITLE -> TitleViewHolder(
                context,
                ItemGroupTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            GroupViewType.LIST -> ListViewHolder(
                context,
                ItemGroupGroupListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem
            )

            GroupViewType.ADD -> AddViewHolder(
                context,
                ItemGroupGroupAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickAddButton
            )

            GroupViewType.WANT -> WantViewHolder(
                context,
                ItemHomeBottomBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem
            )

            GroupViewType.EMPTY -> EmptyViewHolder(
                context,
                ItemGroupEmptyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> GroupUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class TitleViewHolder(
        context: Context,
        private val binding: ItemGroupTitleBinding
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupData) {
            if (item is GroupData.GroupTitle) {
                binding.tvGroupTopTitle.text = item.title
            }
        }
    }

    class ListViewHolder(
        context: Context,
        private val binding: ItemGroupGroupListBinding,
        private val onClickItem: (Int, GroupData) -> Unit
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupData) {
            if (item is GroupData.GroupList) {
//                binding.tvGroupType.text = if (item.groupType==GroupType.PROJECT)"P" else "S"
                if (item.groupType == GroupType.PROJECT) {
                    binding.tvGroupType.text = "P"
                } else {
                    binding.tvGroupType.setBackgroundResource(R.drawable.ic_level_insignia_study)
                    binding.tvGroupType.text = "S"
                }
                binding.ivGroupThumbnail.load(item.thumbnail)
                binding.llGroupItem.setOnClickListener {
                    onClickItem(adapterPosition, item)
                }
                binding.tvGroupProjectTitle.text = item.projectName
                binding.tvGroupDescription.text = item.description
                binding.tvGroupDays.text = item.days.toString()
                binding.tvGroupTags.text = item.tags?.map { "# " + it }?.joinToString("   ", "", "")
            }
        }
    }

    class AddViewHolder(
        context: Context,
        private val binding: ItemGroupGroupAddBinding,
        private val onClickAddButton: (Int, GroupData) -> Unit
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupData) {
            if (item is GroupData.GroupAdd) {
                binding.ivGroupAdd.load(item.addGroupImage)
                binding.ivGroupAdd.setOnClickListener {
                    onClickAddButton(adapterPosition, item)
                }
                binding.tvGroupAdd.setOnClickListener {
                    onClickAddButton(adapterPosition, item)
                }
                binding.tvGroupAdd.text = item.addGroupText
                binding.tvGroupAppliedGroup.text = item.appliedGroup
            }
        }
    }

    class WantViewHolder(
        context: Context,
        private val binding: ItemHomeBottomBinding,
        private val onClickItem: (Int, GroupData) -> Unit
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupData) {
            if (item is GroupData.GroupWant) {
                binding.ivHomeBottomThumubnail.load(item.img)
                binding.tvHomeBottomTitle.text = item.title
                binding.tvHomeBottomDes.text = item.description
                binding.tvHomeBottomKind.text = item.kind
                binding.layBottom.setOnClickListener {
                    onClickItem(adapterPosition, item)
                }
            }
        }
    }

    class EmptyViewHolder(
        context: Context,
        private val binding: ItemGroupEmptyListBinding
    ) : GroupViewHolder(binding.root) {
        override fun onBind(item: GroupData) {
            if (item is GroupData.GroupEmpty){
                binding.tvEmptyGroup.text = item.text
            }
        }
    }

    class GroupUnknownViewHolder(binding: ItemUnknownBinding) :
        GroupViewHolder(binding.root) {
        override fun onBind(item: GroupData) = Unit
    }
}