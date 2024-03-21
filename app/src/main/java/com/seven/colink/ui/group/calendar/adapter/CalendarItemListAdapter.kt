package com.seven.colink.ui.group.calendar.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemListCalendarItemBinding
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import java.time.LocalDate

class ScheduleItemListAdapter(
    private val tempMonth: Int,
    private val date: LocalDate
) :
    ListAdapter<ScheduleModel, ScheduleItemListAdapter.ScheduleViewHolder>(
        object : DiffUtil.ItemCallback<ScheduleModel>() {
            override fun areItemsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(
                oldItem: ScheduleModel,
                newItem: ScheduleModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemListCalendarItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        holder.bind(schedule)
    }

    inner class ScheduleViewHolder(private val binding: ItemListCalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: ScheduleModel) {
            val context = binding.root.context
            val backgroundDrawable =
                ContextCompat.getDrawable(context, R.drawable.bg_background_circle)
            if (backgroundDrawable is GradientDrawable) {
                backgroundDrawable.setColor(
                    ContextCompat.getColor(
                        context,
                        schedule.calendarColor?.color ?: R.color.main_color
                    )
                )
            }
            binding.tvScheduleItem.background = backgroundDrawable
            if (tempMonth != date.monthValue) {
                binding.tvScheduleItem.alpha = 0.4f
            } else {
                binding.tvScheduleItem.alpha = 1.0f
            }
        }
    }
}