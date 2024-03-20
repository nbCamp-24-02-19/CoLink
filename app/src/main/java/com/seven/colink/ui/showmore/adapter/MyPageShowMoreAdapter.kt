package com.seven.colink.ui.showmore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.ItemSearchPostBinding
import com.seven.colink.ui.showmore.ShowMoreItem

class MyPageShowMoreAdapter(var mItem: List<ShowMoreItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val VIEW_TYPE_PROJECT = 1
        private const val VIEW_TYPE_STUDY = 2
    }

    interface PostClick{
        fun onClick(view: View, position: Int, item: ShowMoreItem.ShowMoreProjectItem)
    }

    interface StudyClick{
        fun onClick(view: View, position: Int, item: ShowMoreItem.ShowMoreStudyItem)
    }

    var studyClick: StudyClick? = null

    var postClick: PostClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_PROJECT ->{
                val binding = ItemSearchPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProjectViewHolder(binding)
            } else -> {
                val binding = ItemSearchPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StudyViewHolder(binding)
            }
        }
    }

    fun changeDataset(newDataSet: List<ShowMoreItem>){
        mItem = newDataSet
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItem.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = mItem[position]){
            is ShowMoreItem.ShowMoreProjectItem ->{
                (holder as ProjectViewHolder).projecttitle.text = item.showmoreprojectTitle
                holder.projectname.text = item.showmoreprojectName
                holder.projectdescription.text = item.showmoreprojectDescription
                holder.projecttime.text = item.showmoreprojectTime
                holder.viewCount.text = item.showmoreprojectViewCount.toString()
                holder.projectImage.load(item.showmoreprojectImage)
                holder.itemView.setOnClickListener {
                    postClick?.onClick(it, position, item)
                }
            }
            is ShowMoreItem.ShowMoreStudyItem ->{
                (holder as StudyViewHolder).studytitle.text = item.showmorestudytitle
                holder.studyname.text = item.showmorestudyName
                holder.studydescription.text = item.showmorestudyDescription
                holder.studytime.text = item.showmorestudyTime
                holder.studyviewcount.text = item.showmorestudyViewCount.toString()
                holder.studyImage.load(item.showmorestudyImage)
                holder.itemView.setOnClickListener {
                    studyClick?.onClick(it, position, item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItem[position]){
            is ShowMoreItem.ShowMoreProjectItem -> VIEW_TYPE_PROJECT
            is ShowMoreItem.ShowMoreStudyItem -> VIEW_TYPE_STUDY
        }
    }

    inner class ProjectViewHolder(val binding: ItemSearchPostBinding) : RecyclerView.ViewHolder(binding.root){
        val projecttitle = binding.tvSearchItemTitle
        val projectdescription = binding.tvSearchItemDescription
        val projectname = binding.tvSearchItemPoster
        val projecttime = binding.tvSearchItemTime
        val viewCount = binding.tvSearchItemViewCount
        val projectImage = binding.ivSearchItemThumbnail
    }

    inner class StudyViewHolder(val binding: ItemSearchPostBinding): RecyclerView.ViewHolder(binding.root){
        val studytitle = binding.tvSearchItemTitle
        val studydescription = binding.tvSearchItemDescription
        val studyname = binding.tvSearchItemPoster
        val studytime = binding.tvSearchItemTime
        val studyviewcount = binding.tvSearchItemViewCount
        val studyImage = binding.ivSearchItemThumbnail
    }


}