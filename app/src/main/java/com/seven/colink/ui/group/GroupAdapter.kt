package com.seven.colink.ui.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemGroupGroupAddBinding
import com.seven.colink.databinding.ItemGroupGroupListBinding
import com.seven.colink.databinding.ItemGroupTitleBinding
import com.seven.colink.databinding.ItemHomeBottomBinding

class GroupAdapter(private val mItems: MutableList<GroupData>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface JoinItemClick {
        fun onClick(item: GroupData.GroupList, position: Int)
    }
    var joinItemClick: JoinItemClick? = null

    interface AddItemClick {
        fun onClick(view: View, position: Int)
    }
    var addItemClick: AddItemClick? = null

    interface WantItemClick {
        fun onClick(item: GroupData.GroupWant, position: Int)
    }
    var wantItemClick: WantItemClick? = null

    companion object{
        private const val VIEW_TYPE_TITLE = 1
        private const val VIEW_TYPE_LIST = 2
        private const val VIEW_TYPE_ADD = 3
        private const val VIEW_TYPE_WANT = 4
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_TITLE -> {
                val binding = ItemGroupTitleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                TitleViewHolder(binding)
            } VIEW_TYPE_LIST -> {
                val binding = ItemGroupGroupListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                ListViewHolder(binding)
            }
             VIEW_TYPE_WANT -> {
                 val binding = ItemHomeBottomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                 WantViewHolder(binding)
             }
            else -> {
                val binding = ItemGroupGroupAddBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                AddViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = mItems[position]){
            is GroupData.GroupTitle -> {
                (holder as TitleViewHolder).title.visibility = View.VISIBLE
            }
            is GroupData.GroupList -> {
                (holder as ListViewHolder).projectName.text = item.projectName
                holder.itemBox.setOnClickListener {
                    joinItemClick?.onClick(item, position)
                }
                holder.days.text = item.days.toString() + "일째"
                holder.description.text = item.description
                holder.tags.text = item.tags
            }
            is GroupData.GroupAdd -> {
                (holder as AddViewHolder).appliedGroup.text = item.appliedGroup
                holder.addGroup.setOnClickListener {
                    addItemClick?.onClick(it, position)
                }
            }
            is GroupData.GroupWant -> {
                (holder as WantViewHolder).groupType.text = item.groupType
                holder.title.text = item.title
                holder.description.text = item.description
                holder.kind.text = item.kind
                holder.lv.text = item.lv
                holder.img.setImageResource(R.mipmap.ic_launcher)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItems[position]){
            is GroupData.GroupTitle -> VIEW_TYPE_TITLE
            is GroupData.GroupList -> VIEW_TYPE_LIST
            is GroupData.GroupAdd -> VIEW_TYPE_ADD
            is GroupData.GroupWant -> VIEW_TYPE_WANT
        }
    }

    inner class TitleViewHolder(binding: ItemGroupTitleBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.root
    }

    inner class ListViewHolder(binding: ItemGroupGroupListBinding) : RecyclerView.ViewHolder(binding.root){
        val itemBox = binding.llGroupItem
        val groupType = binding.tvGroupType
        val thumbnail = binding.ivGroupThumbnail
        val projectName = binding.tvGroupProjectTitle
        val days = binding.tvGroupDays
        val description = binding.tvMemberDescription
        val tags = binding.tvGroupTags
    }

    inner class AddViewHolder(binding: ItemGroupGroupAddBinding) : RecyclerView.ViewHolder(binding.root){
        val addBtn = binding.ivGroupAdd
        var addGroup = binding.tvGroupAdd
        val appliedGroup = binding.tvGroupAppliedGroup
    }

    inner class WantViewHolder(binding: ItemHomeBottomBinding) : RecyclerView.ViewHolder(binding.root){
        val groupType = binding.tvHomeBottomProject
        val title = binding.tvHomeBottomTitle
        val description = binding.tvHomeBottomDes
        val kind = binding.tvHomeBottomKind
        val lv = binding.tvHomeBottomLv
        val img = binding.ivHomeBottomThumubnail
    }

}