package com.seven.colink.ui.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.UtilMemberInfoDialogItemBinding
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.util.setLevelIcon

class MemberListAdapter(
    private val onClickItem: (Int, UserEntity, isRefuseButton: Boolean) -> Unit
) : ListAdapter<UserEntity, MemberListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean =
            oldItem.uid == newItem.uid


        override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = UtilMemberInfoDialogItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onClickItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: UtilMemberInfoDialogItemBinding,
        private val onClickItem: (Int, UserEntity, isRefuseButton: Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserEntity) = with(binding) {
            includePostMemberInfo.tvUserName.text = item.name
            includePostMemberInfo.tvUserGrade.text = item.grade.toString()
            item.level?.let { includePostMemberInfo.ivLevelDiaIcon.setLevelIcon(it) }
            includePostMemberInfo.tvLevelDiaIcon.text = item.level.toString()
            includePostMemberInfo.tvUserIntroduction.text = item.info

            includeDialogButton.btRefuse.setOnClickListener {
                onClickItem(adapterPosition, item, false)
            }

            includeDialogButton.btApproval.setOnClickListener {
                onClickItem(adapterPosition, item, true)
            }
        }
    }
}