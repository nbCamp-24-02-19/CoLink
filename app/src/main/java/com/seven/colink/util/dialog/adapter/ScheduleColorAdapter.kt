package com.seven.colink.util.dialog.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemScheduleColorDialogBinding
import com.seven.colink.util.dialog.enum.ColorEnum

class ScheduleColorAdapter(
    private val selected: Int,
    private val onClick: (item: ColorEnum) -> Unit,
    private val dialog: AlertDialog
) : ListAdapter<ColorEnum, ScheduleColorAdapter.DialogViewHolder>(

    object : DiffUtil.ItemCallback<ColorEnum>() {
        override fun areItemsTheSame(
            oldItem: ColorEnum,
            newItem: ColorEnum
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ColorEnum,
            newItem: ColorEnum
        ): Boolean = oldItem == newItem

    }
) {
    abstract class DialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: ColorEnum)
    }

    override fun onViewRecycled(holder: DialogViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder =
        DialogItemViewHolder(
            binding = ItemScheduleColorDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            selected = selected,
            onClick = onClick,
            dialog = dialog
        )

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class DialogItemViewHolder(
        private val binding: ItemScheduleColorDialogBinding,
        private val selected: Int,
        private val onClick: (item: ColorEnum) -> Unit,
        private val dialog: AlertDialog
    ) : DialogViewHolder(binding.root) {
        override fun onBind(item: ColorEnum) = with(binding) {
            val context = root.context
            tvColor.text = item.title?.let { context.getString(it) }
            ivCheck.isVisible = selected == item.color

            if (item == ColorEnum.UNKNOWN) {
                ivColor.setImageResource(R.drawable.ic_do_not_disturb_alt_24)
            } else {
                val backgroundColor = item.color?.let { ContextCompat.getColor(context, it) }
                val backgroundDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(backgroundColor ?: Color.TRANSPARENT)
                }
                ivColor.setImageDrawable(backgroundDrawable)
            }

            root.setOnClickListener {
                onClick(item)
                dialog.dismiss()
            }
        }
    }

}