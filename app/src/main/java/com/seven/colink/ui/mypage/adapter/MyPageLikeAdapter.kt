package com.seven.colink.ui.mypage.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.MypageRvLikeItemBinding
import com.seven.colink.ui.mypage.MyPageLikeModel
import com.seven.colink.util.status.ProjectStatus
import kotlin.math.min

class MyPageLikeAdapter(var mItems: MutableList<MyPageLikeModel>) :
    RecyclerView.Adapter<MyPageLikeAdapter.LikeViewHolder>() {

    interface ItemClick {
        fun onClick(item: MyPageLikeModel, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeViewHolder {
        val binding =
            MypageRvLikeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        val limit = 4
        return min(mItems.size, limit)
    }

    override fun onBindViewHolder(holder: LikeViewHolder, position: Int) {
        val item = mItems[position]

        holder.itemBox.setOnClickListener {
            if (mItems.isNullOrEmpty()) {
                Log.e("Error", "MyPage Like mItems is NullOrEmpty")
                return@setOnClickListener
            } else {
                itemClick?.onClick(mItems[position], position)
            }
        }

        if (item.status == ProjectStatus.RECRUIT) {
            holder.status.text = "모집중"
        } else {
            holder.status.text = "모집완료"
        }

        holder.title.text = item.title
        holder.time.text = item.time

    }

    inner class LikeViewHolder(val binding: MypageRvLikeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemBox = binding.clRvItem
        val status = binding.tvMypageRecruitState
        val title = binding.tvMypageLikeProjectName
        val time = binding.tvMypageLikeItemTime
    }
}