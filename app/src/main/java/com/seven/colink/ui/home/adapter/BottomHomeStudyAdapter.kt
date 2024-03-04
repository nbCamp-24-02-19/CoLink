package com.seven.colink.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

class BottomHomeStudyAdapter : ListAdapter<BottomItems,BottomHomeStudyAdapter.BottomViewHolder>(BottomDiffUtil) {

    object BottomDiffUtil : DiffUtil.ItemCallback<BottomItems>() {
        override fun areItemsTheSame(oldItem: BottomItems, newItem: BottomItems): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: BottomItems, newItem: BottomItems): Boolean {
            return oldItem == newItem
        }
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick : ItemClick? = null

    inner class BottomViewHolder(binding: FragmentHomeStudyBinding) : RecyclerView.ViewHolder(binding.root) {
        val layout = listOf(
            binding.layStudyBottom1,binding.layStudyBottom2,
            binding.layStudyBottom3,binding.layStudyBottom4,
            binding.layStudyBottom5
        )

        fun onBind(item : BottomItems) {
            layout.forEachIndexed { index, bottom ->
                with(bottom) {
                    if (item.typeId == GroupType.STUDY){
                        tvHomeBottomStudy.visibility = View.VISIBLE
                        tvHomeBottomProject.visibility = View.INVISIBLE
                        tvHomeBottomTitle.text = item.title
                        tvHomeBottomDes.text = item.des
                        tvHomeBottomKind.text = item.kind?.toString()
                        tvHomeBottomLv.text = item.lv
                        ivHomeBottomThumubnail.load(item.img)
                        if (item.blind == ProjectStatus.END) {
                            viewHomeBottomBlind.visibility = View.VISIBLE
                            tvHomeBottomBlind.visibility = View.VISIBLE
                        }else {
                            viewHomeBottomBlind.visibility = View.INVISIBLE
                            tvHomeBottomBlind.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentHomeStudyBinding.inflate(inflater, parent, false)
        return BottomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomViewHolder, position: Int) {
        holder.onBind(currentList[position])
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it,position)
        }
    }
}