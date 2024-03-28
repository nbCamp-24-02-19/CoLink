package com.seven.colink.ui.post.register.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemListRecruitmentBinding
import com.seven.colink.domain.entity.RecruitInfo

class RecruitListAdapter(
    private val onClickItem: (RecruitInfo) -> Unit
) : ListAdapter<RecruitInfo, RecruitListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<RecruitInfo>() {

        override fun areItemsTheSame(oldItem: RecruitInfo, newItem: RecruitInfo): Boolean =
            oldItem.type == newItem.type

        override fun areContentsTheSame(
            oldItem: RecruitInfo,
            newItem: RecruitInfo
        ): Boolean = oldItem == newItem

    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListRecruitmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemListRecruitmentBinding,
        private val onClickItem: (RecruitInfo) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecruitInfo) = with(binding) {
            tvRecruitType.text = item.type
            tvRecruitPersonnel.text = "${item.maxPersonnel}명"
            ivRecruitItemDelete.setOnClickListener {
                onClickItem(item)
            }
        }
    }

}