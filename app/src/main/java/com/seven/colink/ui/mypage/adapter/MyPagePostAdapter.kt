package com.seven.colink.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.MypageRecyclerviewItemPostBinding
import com.seven.colink.ui.mypage.MyPostItem

class MyPagePostAdapter(val mItems: List<MyPostItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object{
        private const val VIEW_TYPE_PROJECT = 1
        private const val VIEW_TYPE_STUDY = 2
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val binding = MypageRecyclerviewItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return postViewHolder(binding)
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_PROJECT -> {
                val binding = MypageRecyclerviewItemPostBinding.inflate(LayoutInflater.from(parent.context),parent, false)
                ProjectViewHolder(binding)
            } else ->{
                val binding = MypageRecyclerviewItemPostBinding.inflate(LayoutInflater.from(parent.context),parent, false)
                StudyViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = mItems[position]){
            is MyPostItem.MyPagePostItem ->{
                (holder as ProjectViewHolder).holderProjecting.text = item.projecting
                holder.projectname.text = item.projectName
                holder.projecttime.text = item.projectTime
            }
            is MyPostItem.MyPageStudyItem ->{
                (holder as StudyViewHolder).holderStudying.text = item.studying
                holder.studyname.text = item.studyName
                holder.studytime.text = item.studyTime
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItems[position]){
            is MyPostItem.MyPagePostItem -> VIEW_TYPE_PROJECT
            is MyPostItem.MyPageStudyItem -> VIEW_TYPE_STUDY
        }
    }

    inner class ProjectViewHolder(val binding: MypageRecyclerviewItemPostBinding): RecyclerView.ViewHolder(binding.root){
        val holderProjecting = binding.tvMypagePostItemIngText
        val projectname = binding.tvMypagePostItemProjectName
        val projecttime = binding.tvMypagePostItemTime
    }
    inner class StudyViewHolder(val binding: MypageRecyclerviewItemPostBinding): RecyclerView.ViewHolder(binding.root){
        val holderStudying = binding.tvMypagePostItemIngText
        val studyname = binding.tvMypagePostItemProjectName
        val studytime = binding.tvMypagePostItemTime
    }
}