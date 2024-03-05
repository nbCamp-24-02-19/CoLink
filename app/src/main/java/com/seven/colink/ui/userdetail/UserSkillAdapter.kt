package com.seven.colink.ui.userdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.MypageRecyclerviewItemSkilitemBinding
import com.seven.colink.ui.mypage.MyPageItem

class UserSkillAdapter(var mItems: List<UserSkillItem>): RecyclerView.Adapter<UserSkillAdapter.SkillViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding = MypageRecyclerviewItemSkilitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        holder.icon.setImageResource(mItems[position].sckillicon)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun changeDataset(newDataSet: List<UserSkillItem>){
        mItems = newDataSet
        notifyDataSetChanged()
    }

    inner class SkillViewHolder(val binding: MypageRecyclerviewItemSkilitemBinding): RecyclerView.ViewHolder(binding.root){
        val icon = binding.ivMypageSkilIcon
    }
}