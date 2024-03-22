package com.seven.colink.ui.group.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemListDayBinding
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayListAdapter(
    private val tempMonth: Int,
    private val dayList: MutableList<LocalDate>,
    private val uiState: List<ScheduleModel>,
    private val onItemClick: (position: Int, date: LocalDate) -> Unit,
    private val onMonthChange: (isPreviousMonth: Boolean) -> Unit
) :
    ListAdapter<LocalDate, DayListAdapter.DayView>(
        object : DiffUtil.ItemCallback<LocalDate>() {
            override fun areItemsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean =
                oldItem == newItem
        }
    ) {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var selectedDate: LocalDate? = null
    private var isFirstTimeShowing = true

    abstract class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(date: LocalDate, position: Int, isSelected: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val binding = ItemListDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DayView(binding, tempMonth, onItemClick, onMonthChange)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        val date = dayList[position]
        holder.bind(date, position, position == selectedPosition)
        val currentMonth = LocalDate.now().monthValue
        val isFirstDayOfMonth = date.dayOfMonth == 1 && tempMonth == date.monthValue
        val isCurrentMonth = tempMonth == currentMonth

        if (isFirstDayOfMonth && !isCurrentMonth && isFirstTimeShowing) {
            holder.itemView.post {
                holder.itemView.performClick()
            }
            isFirstTimeShowing = false
        }

    }
    inner class DayView(
        val binding: ItemListDayBinding,
        private val tempMonth: Int,
        private val onItemClick: (position: Int, date: LocalDate) -> Unit,
        private val onMonthChange: (isPreviousMonth: Boolean) -> Unit
    ) : DayViewHolder(binding.root) {
        private val context = binding.root.context
        private val scheduleRecyclerView: RecyclerView = binding.rcScheduleItem

        init {
            scheduleRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        override fun bind(date: LocalDate, position: Int, isSelected: Boolean) {
            val scheduleItemListAdapter = ScheduleItemListAdapter(tempMonth, date)
            scheduleRecyclerView.adapter = scheduleItemListAdapter

            val context = binding.root.context
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            val matchingSchedules = uiState.filter { schedule ->
                val scheduleStartDate = LocalDate.parse(schedule.startDate, formatter)
                val scheduleEndDate = LocalDate.parse(schedule.endDate, formatter)

                date.isEqual(scheduleStartDate) || date.isEqual(scheduleEndDate) ||
                        (date.isAfter(scheduleStartDate) && date.isBefore(
                            scheduleEndDate
                        ))
            }

            scheduleItemListAdapter.submitList(matchingSchedules)

            binding.itemDayText.text = date.dayOfMonth.toString()
            if (date.getCurrentDate()) {
                binding.itemDayText.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.today_color)
                binding.itemDayText.setTextColor(context.getColor(R.color.main_color))
            } else {
                binding.itemDayText.setTextColor(
                    when (position % 7) {
                        0 -> context.getColor(R.color.red)
                        6 -> context.getColor(R.color.forth_color)
                        else -> context.getColor(R.color.black)
                    }
                )
                binding.itemDayText.backgroundTintList =
                    ContextCompat.getColorStateList(
                        context,
                        if (isSelected) R.color.selected_color else R.color.white
                    )
            }

            if (tempMonth != date.monthValue) {
                binding.itemDayText.alpha = 0.4f
            } else {
                binding.itemDayText.alpha = 1.0f
            }

            binding.root.setOnClickListener {
                val isPreviousMonth = date.monthValue < tempMonth
                val isNextMonth = date.monthValue > tempMonth
                if (isPreviousMonth) {
                    onMonthChange(true)
                } else if (isNextMonth) {
                    onMonthChange(false)
                } else {
                    onItemClick(adapterPosition, date)
                }
                selectedDate = date
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
            }

            val currentMonth = LocalDate.now().monthValue
            val isFirstDayOfMonth = date.dayOfMonth == 1 && tempMonth == date.monthValue
            val isCurrentMonth = tempMonth == currentMonth
            if (position == 0 && isFirstDayOfMonth && !isCurrentMonth && isFirstTimeShowing) {
                itemView.post {
                    itemView.performClick()
                }
                isFirstTimeShowing = false
            }
        }

        private fun LocalDate.getCurrentDate(): Boolean {
            val today = LocalDate.now()
            val date = LocalDate.parse(this.toString())
            return today.isEqual(date)
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

}