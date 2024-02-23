package com.seven.colink.util.dialog.adapter

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemLevelDialogBinding
import com.seven.colink.util.dialog.enum.LevelEnum
import com.seven.colink.util.dpToPx
import com.seven.colink.util.setLevelIcon

class LevelDialogAdapter(
    private val selected: Int,
    private val onClick: (id: Int) -> Unit,
) : ListAdapter<LevelEnum, LevelDialogAdapter.DialogViewHolder>(

    object : DiffUtil.ItemCallback<LevelEnum>() {
        override fun areItemsTheSame(
            oldItem: LevelEnum,
            newItem: LevelEnum
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: LevelEnum,
            newItem: LevelEnum
        ): Boolean = oldItem == newItem

    }
) {
    abstract class DialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: LevelEnum)
    }

    override fun onViewRecycled(holder: DialogViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder =
        DialogItemViewHolder(
            binding = ItemLevelDialogBinding.inflate(LayoutInflater.from(parent.context),parent,false),
            selected = selected,
            onClick = onClick,
        )

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class DialogItemViewHolder(
        private val binding: ItemLevelDialogBinding,
        private val selected: Int,
        private val onClick: (id: Int) -> Unit
    ): DialogViewHolder(binding.root) {
        override fun onBind(item: LevelEnum) = with(binding) {
            tvLevelDiaIcon.text = item.num.toString()
            item.title?.let { tvLevelDiaTitle.setText(it) }
            item.info?.let { tvLevelDiaInfo.setText(it) }
            item.num?.let { ivLevelDiaIcon.setLevelIcon(it) }

            Log.d("sle","$item, num: ${item.num}, info: ${item.info}, title: ${item.title},")
            Log.d("selected","$selected")
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.cornerRadius = 4.dpToPx(root.context).toFloat()

            if (selected == item.num) {
                drawable.setStroke(2.dpToPx(root.context), root.context.getColor(R.color.main_color))
                drawable.setColor(ContextCompat.getColor(root.context,R.color.selected_bg))
            } else {
                drawable.setStroke(2.dpToPx(root.context), root.context.getColor(R.color.enable_stroke))
                drawable.setColor(ContextCompat.getColor(root.context,R.color.white))
            }

            clLevelDia.background = drawable
            clLevelDia.setOnClickListener {
                item.num?.let { num -> onClick(num) }
            }
        }

    }
}