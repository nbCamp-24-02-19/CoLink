package com.seven.colink.ui.group.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemListMonthBinding
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class MonthListAdapter(
    private val onDayItemClick: (date: LocalDate) -> Unit,
    private val uiState: List<ScheduleModel>
) : ListAdapter<Int, MonthListAdapter.MonthViewHolder>(
    object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem
    }
) {
    val center = Int.MAX_VALUE / 2
    private val calendar = Calendar.getInstance()

    abstract class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(month: Int, position: Int)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val binding = ItemListMonthBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MonthView(binding,
            onDayItemClick)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month = position - center
        holder.bind(month, position)
    }

    inner class MonthView(
        private val binding: ItemListMonthBinding,
        private val onDayItemClick: (date: LocalDate) -> Unit
    ) : MonthViewHolder(binding.root) {
        override fun bind(month: Int, position: Int) {
            calendar.time = Date()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.add(Calendar.MONTH, position - center)
            binding.itemMonthText.text =
                "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
            val tempMonth = calendar.get(Calendar.MONTH) + 1

            val dayList: MutableList<LocalDate> = MutableList(6 * 7) { LocalDate.now() }
            for (i in 0..5) {
                for (k in 0..6) {
                    calendar.add(
                        Calendar.DAY_OF_MONTH,
                        (1 - calendar.get(Calendar.DAY_OF_WEEK)) + k
                    )
                    dayList[i * 7 + k] =
                        calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                }
                calendar.add(Calendar.WEEK_OF_MONTH, 1)
            }
            val dayListManager = GridLayoutManager(binding.root.context, 7)
            val dayListAdapter =
                DayListAdapter(tempMonth, dayList, uiState, onItemClick = { _, date ->
                    onDayItemClick(date)
                })

            binding.itemMonthDayList.apply {
                layoutManager = dayListManager
                adapter = dayListAdapter
            }
        }
    }

}
