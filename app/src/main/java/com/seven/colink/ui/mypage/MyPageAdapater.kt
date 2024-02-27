package com.seven.colink.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.MypageRecyclerviewItemAboutMeBinding
import com.seven.colink.databinding.MypageRecyclerviewItemBinding
import com.seven.colink.databinding.MypageRecyclerviewItemNameBinding
import com.seven.colink.databinding.MypageRecyclerviewItemPostBinding
import com.seven.colink.databinding.MypageRecyclerviewItemTermsBinding

class MyPageAdapater(val mItems:MutableList<MyPageData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        private const val VIEW_TYPE_NAME = 1
//        private const val VIEW_TYPE_SKIL = 2
        private const val VIEW_TYPE_ABOUTME = 3
        private const val VIEW_TYPE_POST = 4
        private const val VIEW_TYPE_TERMS = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_NAME ->{
                val binding = MypageRecyclerviewItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NameViewHolder(binding)
//            } VIEW_TYPE_SKIL ->{
//                val binding = MypageRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                SkilViewHolder(binding)
            } VIEW_TYPE_ABOUTME ->{
                val binding = MypageRecyclerviewItemAboutMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AboutmeViewHolder(binding)
            } VIEW_TYPE_POST ->{
                val binding = MypageRecyclerviewItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding)
            } else ->{
                val binding = MypageRecyclerviewItemTermsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TermsViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = mItems[position]){
            is MyPageData.MyPageName ->{
                (holder as NameViewHolder).Mypage_name.text = item.Name
                holder.profile.setImageResource(item.Profile)
                holder.Mypage_level.setImageResource(item.Level)
                holder.specialization.text = item.specialization
            }
//            is MyPageData.MyPageSkil->{
//                (holder as )
//            }
            is MyPageData.MyPageAboutMe ->{
                (holder as AboutmeViewHolder).aboutme.text = item.AboutMe
            }
            is MyPageData.MyPagePost ->{
                (holder as PostViewHolder).item_ing.text = item.parti
                holder.item_project_name.text = item.projectName
                holder.item_project_time.text = item.projectTime
            }
            is MyPageData.MyPageTerms ->{
                (holder as TermsViewHolder).TName.text = item.tName
            }
        }
    }


    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItems[position]){
            is MyPageData.MyPageName -> VIEW_TYPE_NAME
            is MyPageData.MyPageAboutMe -> VIEW_TYPE_ABOUTME
            is MyPageData.MyPagePost -> VIEW_TYPE_POST
            is MyPageData.MyPageTerms -> VIEW_TYPE_TERMS
        }
    }


    inner class NameViewHolder(binding: MypageRecyclerviewItemNameBinding) : RecyclerView.ViewHolder(binding.root){
        val profile = binding.ivMypageProfile
        val Mypage_name = binding.tvMypageName
        val Mypage_level = binding.ivMypageLevel
        val specialization = binding.tvMypageSpecialization2
        val blog = binding.ivMypageBlog
        val git = binding.ivMypageGit
        val edit = binding.tvMypageItemEdit
    }

    inner class SkilViewHolder(binding: MypageRecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.reMypageItem.apply {

            }
        }
    }

    inner class AboutmeViewHolder(binding: MypageRecyclerviewItemAboutMeBinding) : RecyclerView.ViewHolder(binding.root){
        val aboutme = binding.tvMypageAboutMe
    }

    inner class PostViewHolder(binding: MypageRecyclerviewItemPostBinding) : RecyclerView.ViewHolder(binding.root){
        val item_ing = binding.tvMypagePostItemIngText
        val item_project_name = binding.tvMypagePostItemProjectName
        val item_project_time = binding.tvMypagePostItemTime
    }

    inner class TermsViewHolder(binding: MypageRecyclerviewItemTermsBinding) : RecyclerView.ViewHolder(binding.root){
        val TName = binding.mypageReTermsName
        val Tarrow = binding.mypageReBackItemName
    }



}