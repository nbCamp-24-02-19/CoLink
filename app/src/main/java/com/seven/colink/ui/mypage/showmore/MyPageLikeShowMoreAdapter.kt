package com.seven.colink.ui.mypage.showmore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.ItemSearchPostBinding
import com.seven.colink.ui.search.SearchModel
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

class MyPageLikeShowMoreAdapter(var mItems: MutableList<SearchModel>) :
    RecyclerView.Adapter<MyPageLikeShowMoreAdapter.MoreLikeViewHolder>() {

    interface ItemClick {
        fun onClick(item: SearchModel, position: Int)
    }

    var itemClick: ItemClick? = null

    inner class MoreLikeViewHolder(binding: ItemSearchPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemBox = binding.linearLayout
        val thumbnail = binding.ivSearchItemThumbnail
        val project = binding.tvSearchItemProject
        val study = binding.tvSearchItemStudy
        val recruit = binding.tvSearchItemRecruit
        val recruitEnd = binding.tvSearchItemRecruitEnd
        val title = binding.tvSearchItemTitle
        val description = binding.tvSearchItemDescription
        val tag = binding.tvSearchItemTag1
        val poster = binding.tvSearchItemPoster
        val time = binding.tvSearchItemTime
        val heartCount = binding.tvSearchItemHeartCount
        val viewCount = binding.tvSearchItemViewCount
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoreLikeViewHolder {
        val binding =
            ItemSearchPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoreLikeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MoreLikeViewHolder,
        position: Int
    ) {
        val item = mItems[position]

        holder.itemBox.setOnClickListener {
            if (mItems.isNullOrEmpty()) {
                Log.e("Error", "Like Show More mItems is NullOrEmpty02")
                return@setOnClickListener
            } else {
                itemClick?.onClick(mItems[position], position)
            }
        }

        if (item.groupType == GroupType.PROJECT) {
            holder.project.visibility = View.VISIBLE
            holder.study.visibility = View.GONE
        } else {
            holder.study.visibility = View.VISIBLE
            holder.project.visibility = View.GONE
        }
        if (item.status == ProjectStatus.RECRUIT) {
            holder.recruit.visibility = View.VISIBLE
            holder.recruitEnd.visibility = View.GONE
        } else {
            holder.recruitEnd.visibility = View.VISIBLE
            holder.recruit.visibility = View.GONE
        }
        holder.thumbnail.load(item.thumbnail)
        holder.title.text = item.title
        holder.description.text = item.description
        holder.tag.text = item.tags?.map { "# " + it }?.joinToString("   ", "", "")
        holder.poster.text = item.authId
        holder.time.text = item.registeredDate
        holder.heartCount.text = item.likes.toString()
        holder.viewCount.text = item.views.toString()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }
}