package com.seven.colink.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.seven.colink.databinding.ItemSignUpSkillBinding
import com.seven.colink.databinding.MypageRecyclerviewItemSkilitemBinding
import com.seven.colink.ui.mypage.MyPageItem
import com.seven.colink.ui.mypage.MyPostItem
import com.seven.colink.ui.sign.signup.adater.SignUpProfileAdapter
import com.seven.colink.ui.sign.signup.type.SignUpProfileViewType

class MyPageSkilAdapter(var mItems: List<MyPageItem>): RecyclerView.Adapter<ViewHolder>() {

    companion object{
        private const val VIEW_TYPE_SKIL = 1
        private const val VIEW_TYPE_PLUS = 2
    }

    interface PlusClick{
        fun onClick(item: MyPageItem, position: Int)
    }

    var plusClick: PlusClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SKIL ->{
                val binding = MypageRecyclerviewItemSkilitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SkilViewHolder(binding)
            } else -> {
                val binding = MypageRecyclerviewItemSkilitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PlusViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun changeDataset(newDataSet: List<MyPageItem>){
        mItems = newDataSet
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mItems[position]
        when (item){
            is MyPageItem.skilItems -> {
                (holder as SkilViewHolder).icon.setImageResource(item.languageIcon)
            }
            is MyPageItem.plusItems -> {
                (holder as PlusViewHolder).icon.setImageResource(item.plusIcon)
                holder.itemView.setOnClickListener {
                    plusClick?.onClick(item, position)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItems[position]){
            is MyPageItem.skilItems -> VIEW_TYPE_SKIL
            is MyPageItem.plusItems -> VIEW_TYPE_PLUS
        }
    }


    inner class PlusViewHolder(val binding: MypageRecyclerviewItemSkilitemBinding) : RecyclerView.ViewHolder(binding.root){
        val icon = binding.ivMypageSkilIcon
    }

    inner class SkilViewHolder(val binding: MypageRecyclerviewItemSkilitemBinding) : RecyclerView.ViewHolder(binding.root){
        val icon = binding.ivMypageSkilIcon
    }


}