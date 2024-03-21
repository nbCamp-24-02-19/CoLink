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
import com.seven.colink.databinding.ItemSelectedDateBinding
import com.seven.colink.ui.group.calendar.model.ScheduleItem
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleListAdapter(
    private val onClickItem: (ScheduleItem) -> Unit
) : ListAdapter<ScheduleItem, ScheduleListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ScheduleItem>() {
        override fun areItemsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean =
            when {
                oldItem is ScheduleItem.ScheduleModel && newItem is ScheduleItem.ScheduleModel -> {
                    oldItem.key == newItem.key
                }

                else -> oldItem == newItem
            }

        override fun areContentsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean =
            oldItem == newItem
    }
) {

    companion object {
        private const val TYPE_SCHEDULE = 1
        private const val TYPE_TITLE = 2
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: ScheduleItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_SCHEDULE -> {
                val binding = ItemListCalendarScheduleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ScheduleViewHolder(binding)
            }

            TYPE_TITLE -> {
                val binding = ItemSelectedDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TitleViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is ScheduleItem.ScheduleModel -> TYPE_SCHEDULE
            is ScheduleItem.DateTitle -> TYPE_TITLE
            else -> throw IllegalArgumentException("Unknown item type at position $position: $item")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ScheduleViewHolder -> {
                if (item is ScheduleItem.ScheduleModel) {
                    holder.bind(item)
                }
            }

            is TitleViewHolder -> {
                if (item is ScheduleItem.DateTitle) {
                    holder.bind(item)
                }
            }
        }
    }


    inner class TitleViewHolder(
        private val binding: ItemSelectedDateBinding
    ) : ViewHolder(binding.root) {
        override fun bind(item: ScheduleItem) {
            if (item is ScheduleItem.DateTitle) {
                val context = binding.root.context
                val date = item.date
                val dayOfWeek = when (date.dayOfWeek) {
                    DayOfWeek.SUNDAY -> "일"
                    DayOfWeek.MONDAY -> "월"
                    DayOfWeek.TUESDAY -> "화"
                    DayOfWeek.WEDNESDAY -> "수"
                    DayOfWeek.THURSDAY -> "목"
                    DayOfWeek.FRIDAY -> "금"
                    DayOfWeek.SATURDAY -> "토"
                }
                val formattedDate = date.format(DateTimeFormatter.ofPattern("MM.dd"))
                val textColor = when (date.dayOfWeek) {
                    DayOfWeek.SUNDAY -> ContextCompat.getColor(context, R.color.red)
                    DayOfWeek.SATURDAY -> ContextCompat.getColor(context, R.color.forth_color)
                    else -> ContextCompat.getColor(context, R.color.black)
                }
                binding.tvSelectedDate.setTextColor(textColor)
                binding.tvSelectedDate.text = "$formattedDate. $dayOfWeek"
            }
        }
    }


    inner class ScheduleViewHolder(private val binding: ItemListCalendarScheduleBinding) :
        ViewHolder(binding.root) {

        override fun bind(item: ScheduleItem) {
            if (item is ScheduleItem.ScheduleModel) {
                val context = binding.root.context
                if (allFieldsNull(item)) {
                    binding.tvSchedule.text = "일정이 없습니다."
                    binding.tvSchedule.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.enabled_color
                        )
                    )
                    binding.tvDatetime.visibility = View.GONE
                    val color = ContextCompat.getColor(context, R.color.enabled_color)
                    binding.viewColor.setBackgroundColor(color)
                } else {
                    binding.tvSchedule.text = item.title
                    val startDate = item.startDate?.dateFormatter()
                    val endDate = item.endDate?.dateFormatter()
                    binding.tvDatetime.visibility = View.VISIBLE
                    binding.tvDatetime.text = "$startDate - $endDate"

                    val color = ContextCompat.getColor(
                        context,
                        item.calendarColor?.color ?: R.color.main_color
                    )
                    binding.viewColor.setBackgroundColor(color)

                    binding.root.setOnClickListener {
                        onClickItem(item)
                    }
                }
            }
        }

        private fun allFieldsNull(item: ScheduleItem.ScheduleModel): Boolean {
            return listOf(
                item.title,
                item.startDate,
                item.endDate,
                item.calendarColor,
                item.description,
                item.buttonUiState
            ).all { it == null }
        }

        private fun String.dateFormatter(): String {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
            val date = dateFormat.parse(this)
            val calendar = Calendar.getInstance()
            calendar.time = date

            val month = calendar.get(Calendar.MONTH) + 1
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
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
