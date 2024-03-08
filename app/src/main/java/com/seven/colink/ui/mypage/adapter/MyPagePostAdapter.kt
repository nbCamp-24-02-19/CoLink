package com.seven.colink.ui.mypage.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.MypageRecyclerviewItemPostBinding
import com.seven.colink.ui.mypage.MyPostItem

class MyPagePostAdapter(var mItems: List<MyPostItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object{
        private const val VIEW_TYPE_PROJECT = 1
        private const val VIEW_TYPE_STUDY = 2
    }

    interface PostClick{
        fun onClick(view: View, position: Int, item: MyPostItem.MyPagePostItem)
    }

    interface StudyClick{
        fun onClick(view: View, position: Int, item: MyPostItem.MyPageStudyItem)
    }

    var studyClick: StudyClick? = null

    var postClick: PostClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
                //여기서 if문으로 컬러 바꾸기
                if (holder.holderProjecting.text == "참여중"){
//                    holder.holderProjecting.setBackgroundColor(Color.parseColor("#2DB7FF"))
                    holder.holderProjecting.setBackgroundResource(R.drawable.bg_mypage_ing_blue)
                } else if(holder.holderProjecting.text == "중도하차"){
                    holder.holderProjecting.setBackgroundResource(R.drawable.bg_mypage_ing_gray)
                } else if(holder.holderProjecting.text =="완료"){
                    holder.holderProjecting.setBackgroundResource(R.drawable.bg_mypage_ing_purple)
                }
                holder.projectname.text = item.projectName
                holder.projecttime.text = item.projectTime
                holder.itemView.setOnClickListener {
                    postClick?.onClick(it, position, item)
                }
            }
            is MyPostItem.MyPageStudyItem ->{
                (holder as StudyViewHolder).holderStudying.text = item.studying
                if (holder.holderStudying.text == "참여중"){
                    holder.holderStudying.setBackgroundResource(R.drawable.bg_mypage_ing_blue)
                } else if(holder.holderStudying.text == "완료"){
                    holder.holderStudying.setBackgroundResource(R.drawable.bg_mypage_ing_purple)
                    holder.holderStudying.setTextColor(Color.WHITE)
                }
                holder.studyname.text = item.studyName
                holder.studytime.text = item.studyTime
                holder.itemView.setOnClickListener {
                    studyClick?.onClick(it, position, item)
                }
            }
        }
    }

    fun changeDataset(newDataSet: List<MyPostItem>){
        mItems = newDataSet
        notifyDataSetChanged()
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