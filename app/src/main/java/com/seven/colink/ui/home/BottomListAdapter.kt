package com.seven.colink.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.databinding.ItemHomeBottomBinding

class BottomListAdapter : ListAdapter<BottomItems,BottomListAdapter.BottomViewHolder>(BottomDiffUtil) {
    object BottomDiffUtil : DiffUtil.ItemCallback<BottomItems>() {
        override fun areItemsTheSame(oldItem: BottomItems, newItem: BottomItems): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BottomItems, newItem: BottomItems): Boolean {
            return oldItem == newItem
        }
    }

    inner class BottomViewHolder(binding : ItemHomeBottomBinding) : ViewHolder(binding.root) {
        val type = binding.tvHomeBottomProject
        val type2 = binding.tvHomeBottomStudy
        val title = binding.tvHomeBottomTitle
        val des = binding.tvHomeBottomDes
        val kind = binding.tvHomeBottomKind
        val lv = binding.tvHomeBottomLv
        val img = binding.ivHomeBottomThumubnail
        val blind = binding.viewHomeBottomBlind
        val complete = binding.tvHomeBottomBlind
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeBottomBinding.inflate(inflater,parent,false)
        return BottomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomViewHolder, position: Int) {
        val item = getItem(position)

        if (item.typeId == "Project") {
            with(holder){
                type.visibility = View.VISIBLE
                type2.visibility = View.INVISIBLE
            }
        }else {
            with(holder){
                type.visibility = View.INVISIBLE
                type2.visibility = View.VISIBLE
            }
        }

        with(holder) {
            title.text = item.title
            des.text = item.des
            kind.text = item.kind
            lv.text = item.lv
            img.load(item.img)
        }

        if (!item.blind) {
            with(holder) {
                blind.visibility = View.INVISIBLE
                complete.visibility = View.INVISIBLE
            }
        }else{
            with(holder) {
                blind.visibility = View.VISIBLE
                complete.visibility = View.VISIBLE
            }
        }
    }
}