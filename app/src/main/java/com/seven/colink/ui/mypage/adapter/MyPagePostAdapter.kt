package com.seven.colink.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.MypageRecyclerviewItemPostBinding
import com.seven.colink.ui.mypage.MyPagePostItem

class MyPagePostAdapter(val mItems: List<MyPagePostItem>) : RecyclerView.Adapter<MyPagePostAdapter.postViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postViewHolder {
        val binding = MypageRecyclerviewItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return postViewHolder(binding)
    }

    override fun onBindViewHolder(holder: postViewHolder, position: Int) {
        holder.ing.text = mItems[position].ing
        holder.projectname.text = mItems[position].projectName
        holder.projecttime.text = mItems[position].projectTime
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class postViewHolder(val binding: MypageRecyclerviewItemPostBinding): RecyclerView.ViewHolder(binding.root){
        val ing = binding.tvMypagePostItemIngText
        val projectname = binding.tvMypagePostItemProjectName
        val projecttime = binding.tvMypagePostItemTime
    }
}