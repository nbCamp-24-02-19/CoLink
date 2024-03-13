package com.seven.colink.ui.promotion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.ui.promotion.MembersItem
import com.seven.colink.util.setLevelIcon

class ProductPromotionMemberAdapter : ListAdapter<MembersItem, ProductPromotionMemberAdapter.MemberViewHolder>(ProductMemberDiffUtil) {

    object ProductMemberDiffUtil : DiffUtil.ItemCallback<MembersItem>() {
        override fun areItemsTheSame(
            oldItem: MembersItem, newItem: MembersItem
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: MembersItem, newItem: MembersItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class MemberViewHolder(binding: ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemPostMemberInfoBinding.inflate(inflate,parent,false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            img.load(item.img)
            name.text = item.name
            intro.text = item.info
            grade.text = item.grade?.toString()
            item.level?.let { levelColor.setLevelIcon(it) }
            level.text = item.level?.toString()
        }
    }
}