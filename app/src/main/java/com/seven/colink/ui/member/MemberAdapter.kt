package com.seven.colink.ui.member

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemMemberGroupAddBinding
import com.seven.colink.databinding.ItemMemberGroupListBinding
import com.seven.colink.databinding.ItemMemberTitleBinding
import com.seven.colink.domain.entity.PostEntity
import java.lang.reflect.Member

class MemberAdapter(val mItems: MutableList<MemberData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val VIEW_TYPE_TITLE = 1
        private const val VIEW_TYPE_LIST = 2
        private const val VIEW_TYPE_ADD = 3
//        private const val VIEW_TYPE_GROUP = 4
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_TITLE -> {
                val binding = ItemMemberTitleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                TitleViewHolder(binding)
            } VIEW_TYPE_LIST -> {
                val binding = ItemMemberGroupListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                ListViewHolder(binding)
            }
//             VIEW_TYPE_GROUP -> {
//                 val binding = ??Binding.inflate(LayoutInflater.from(parent.context),parent,false)
//                 GroupViewHolder(binding)
//             }
            else -> {
                val binding = ItemMemberGroupAddBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                AddViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = mItems[position]){
            is MemberData.MemberTitle -> {
                (holder as TitleViewHolder).title.visibility = View.VISIBLE
            }
            is MemberData.MemberList -> {
                (holder as ListViewHolder).projectName.text = item.projectName
                holder.days.text = item.days.toString() + "일째"
                holder.description.text = item.description
                holder.tags.text = item.tags
            }
            is MemberData.MemberAdd -> {
                (holder as AddViewHolder).addGroup.text = item.addGroup
                holder.appliedGroup.text = item.appliedGroup
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItems[position]){
            is MemberData.MemberTitle -> VIEW_TYPE_TITLE
            is MemberData.MemberList -> VIEW_TYPE_LIST
            is MemberData.MemberAdd -> VIEW_TYPE_ADD
        }
    }

    inner class TitleViewHolder(binding: ItemMemberTitleBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.root
    }

    inner class ListViewHolder(binding: ItemMemberGroupListBinding) : RecyclerView.ViewHolder(binding.root){
        val groupType = binding.ivMemberGroupType
        val thumbnail = binding.ivMemberThumbnail
        val projectName = binding.tvMemberProjectTitle
        val days = binding.tvMemberDays
        val description = binding.tvMemberDescription
        val tags = binding.tvMemberTags
    }

    inner class AddViewHolder(binding: ItemMemberGroupAddBinding) : RecyclerView.ViewHolder(binding.root){
        val addGroup = binding.tvMemberAddGroup
        val appliedGroup = binding.tvMemberAppliedGroup
    }

}