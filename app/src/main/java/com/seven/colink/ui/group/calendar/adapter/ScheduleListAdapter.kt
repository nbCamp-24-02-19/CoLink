package com.seven.colink.ui.group.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemListCalendarScheduleBinding
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import com.seven.colink.util.Constants
import java.time.format.DateTimeFormatter

class ScheduleListAdapter(
    private val onClickItem: (ScheduleModel) -> Unit
) : ListAdapter<ScheduleModel, ScheduleListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ScheduleModel>() {
        override fun areItemsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
            oldItem.key == newItem.key

        override fun areContentsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
            oldItem == newItem
    }
) {

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: ScheduleModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListCalendarScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ScheduleViewHolder -> holder.bind(item)
        }
    }

    inner class ScheduleViewHolder(private val binding: ItemListCalendarScheduleBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: ScheduleModel) {
            val context = binding.root.context
            if (allFieldsNull(item)) {
                binding.tvSchedule.apply {
                    text = context.getString(R.string.no_schedule)
                    setTextColor(ContextCompat.getColor(context, R.color.enabled_color))
                }
                binding.tvDatetime.visibility = View.GONE
                binding.viewColor.setBackgroundColor(ContextCompat.getColor(context, R.color.enabled_color))
            } else {
                binding.tvSchedule.apply {
                    text = item.title
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                }
                val startDate = item.startDate?.format(DateTimeFormatter.ofPattern(Constants.CALENDAR_TIME_FORMAT))
                val endDate = item.endDate?.format(DateTimeFormatter.ofPattern(Constants.CALENDAR_TIME_FORMAT))
                binding.tvDatetime.apply {
                    visibility = View.VISIBLE
                    text = "$startDate - $endDate"
                }
                val color = ContextCompat.getColor(context, item.calendarColor?.color ?: R.color.main_color)
                binding.viewColor.setBackgroundColor(color)
                binding.root.setOnClickListener { onClickItem(item) }
            }
        }

        private fun allFieldsNull(item: ScheduleModel): Boolean {
            return listOf(
                item.title,
                item.startDate,
                item.endDate,
                item.calendarColor,
                item.description,
                item.buttonUiState
            ).all { it == null }
        }
    }
}
