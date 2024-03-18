package com.seven.colink.ui.group.calendar.adapter

import android.icu.util.Calendar
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
import java.text.SimpleDateFormat
import java.util.Locale


class ScheduleListAdapter(
    private val onClickItem: (ScheduleModel) -> Unit
) : ListAdapter<ScheduleModel, ScheduleListAdapter.ScheduleViewHolder>(
    object : DiffUtil.ItemCallback<ScheduleModel>() {
        override fun areItemsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
            oldItem.key == newItem.key

        override fun areContentsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
            oldItem == newItem
    }
){
    abstract class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: ScheduleModel)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleViewHolder {
        val binding = ItemListCalendarScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarScheduleView(binding,
            onClickItem)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is CalendarScheduleView) {
            holder.bind(item)
        }
    }


    inner class CalendarScheduleView(
        val binding: ItemListCalendarScheduleBinding,
        private val onClickItem: (ScheduleModel) -> Unit
    ) : ScheduleViewHolder(binding.root) {
        override fun bind(item: ScheduleModel) {
            val context = binding.root.context
            binding.tvSchedule.text = item.title
            binding.tVDatetime.text = "${item.startDate?.dateFormatter()} - ${item.endDate?.dateFormatter()}"
            val color = ContextCompat.getColor(context, item.calendarColor?.color ?: R.color.main_color)
            binding.viewColor.setBackgroundColor(color)

            binding.root.setOnClickListener {
                onClickItem(item)
            }
        }

        private fun String.dateFormatter(): String {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
            val date = dateFormat.parse(this)
            val calendar = Calendar.getInstance()
            calendar.time = date

            val month = calendar.get(java.util.Calendar.MONTH) + 1
            val dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            val dayOfWeek = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "일"
                Calendar.MONDAY -> "월"
                Calendar.TUESDAY -> "화"
                Calendar.WEDNESDAY -> "수"
                Calendar.THURSDAY -> "목"
                Calendar.FRIDAY -> "금"
                Calendar.SATURDAY -> "토"
                else -> ""
            }
            val amPm = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) "오전" else "오후"
            var hour = calendar.get(Calendar.HOUR)
            if (hour == 0) hour = 12
            val minute = calendar.get(Calendar.MINUTE)
            return "$month.$dayOfMonth.$dayOfWeek $amPm ${hour}시 ${minute}분"
        }
    }

}