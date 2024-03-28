package com.seven.colink.ui.group.calendar.register.adapter

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemRegisterScheduleBinding
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import com.seven.colink.ui.group.calendar.status.CalendarButtonUiState
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.dialog.enum.ColorEnum
import com.seven.colink.util.dialog.setScheduleAlarm
import com.seven.colink.util.dialog.setScheduleColor
import com.seven.colink.util.showToast
import java.text.SimpleDateFormat
import java.util.*

class ScheduleRegisterListAdapter(
    private val onColorSelected: (ColorEnum) -> Unit,
    private val notifyOnClickItem: (Int, TagListItem) -> Unit,
    private val dateChangeListener: (String, String) -> Unit,
    private val onTextChanged: (Int, String, String) -> Unit,
) : ListAdapter<ScheduleModel, ScheduleRegisterListAdapter.ScheduleViewHolder>(
    object : DiffUtil.ItemCallback<ScheduleModel>() {
        override fun areItemsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
            oldItem.key == newItem.key

        override fun areContentsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRegisterScheduleBinding.inflate(inflater, parent, false)
        return ScheduleViewHolder(
            binding,
            onColorSelected,
            notifyOnClickItem,
            dateChangeListener,
            onTextChanged
        )
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScheduleViewHolder(
        private val binding: ItemRegisterScheduleBinding,
        private val onColorSelected: (ColorEnum) -> Unit,
        private val tagAdapterOnClickItem: (Int, TagListItem) -> Unit,
        private val dateChangeListener: (String, String) -> Unit,
        private val onTextChanged: (Int, String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context
        private var isStartDatePickerVisible = false
        private var isEndDatePickerVisible = false
        private var savedColor = ColorEnum.UNKNOWN
        private val tagAdapter =
            TagListAdapter { item -> tagAdapterOnClickItem(adapterPosition, item) }
        private val editTexts
            get() = listOf(binding.etSchedule, binding.etDescription)

        init {
            binding.rcNoti.adapter = tagAdapter
            binding.tvStartDate.setOnClickListener { toggleDateTimePicker(true) }
            binding.tvEndDate.setOnClickListener { toggleDateTimePicker(false) }
            onColorSelected(savedColor)
            setTextChangeListener()
        }

        private fun setTextChangeListener() {
            editTexts.forEach { editText ->
                editText.addTextChangedListener {
                    notifyTextChange(
                        binding.etSchedule.text.toString(),
                        binding.etDescription.text.toString()
                    )
                }
            }
        }

        private fun toggleDateTimePicker(isStart: Boolean) {
            if (isStart) {
                if (isStartDatePickerVisible) {
                    binding.layoutDatetime.setBackgroundResource(R.drawable.bg_date)
                    isStartDatePickerVisible = false
                    binding.startDatePicker.visibility = View.GONE
                    binding.startTimePicker.visibility = View.GONE
                } else {
                    binding.layoutDatetime.setBackgroundResource(R.drawable.bg_start_datetime)
                    isEndDatePickerVisible = false
                    binding.endDatePicker.visibility = View.GONE
                    binding.endTimePicker.visibility = View.GONE

                    isStartDatePickerVisible = true
                    binding.startDatePicker.visibility = View.VISIBLE
                    binding.startTimePicker.visibility = View.VISIBLE
                }
            } else {
                if (isEndDatePickerVisible) {
                    binding.layoutDatetime.setBackgroundResource(R.drawable.bg_date)
                    isEndDatePickerVisible = false
                    binding.endDatePicker.visibility = View.GONE
                    binding.endTimePicker.visibility = View.GONE
                } else {
                    binding.layoutDatetime.setBackgroundResource(R.drawable.bg_end_datetime)
                    isStartDatePickerVisible = false
                    binding.startDatePicker.visibility = View.GONE
                    binding.startTimePicker.visibility = View.GONE

                    isEndDatePickerVisible = true
                    binding.endDatePicker.visibility = View.VISIBLE
                    binding.endTimePicker.visibility = View.VISIBLE
                }
            }
        }

        fun bind(item: ScheduleModel) {
            with(binding) {
                etSchedule.setText(item.title)
                etDescription.setText(item.description)
                tvStartDate.text = item.startDate
                tvEndDate.text = item.endDate
                tagAdapter.submitList(emptyList())
                item.calendarColor?.let { updateColorPalette(it) }

                when (item.buttonUiState) {
                    CalendarButtonUiState.Create, CalendarButtonUiState.Editing -> {
                        etSchedule.isEnabled = true
                        etDescription.isEnabled = true
                        tvStartDate.isEnabled = true
                        tvEndDate.isEnabled = true
                        tvNoti.isEnabled = true
                        tvColorPalette.isEnabled = true
                    }

                    CalendarButtonUiState.Update, CalendarButtonUiState.Detail -> {
                        etSchedule.isEnabled = false
                        etDescription.isEnabled = false
                        tvStartDate.isEnabled = false
                        tvEndDate.isEnabled = false
                        tvNoti.isEnabled = false
                        tvColorPalette.isEnabled = false
                    }

                    else -> Unit
                }
            }

            val startDate = item.startDate?.parseDateTime()!!
            val endDate = item.endDate?.parseDateTime()!!
            setDateTimePicker(binding.startDatePicker, binding.startTimePicker, startDate, true)
            setDateTimePicker(binding.endDatePicker, binding.endTimePicker, endDate, false)

            binding.tvColorPalette.setOnClickListener {
                context.setScheduleColor(savedColor.color!!) { selectedColor ->
                    updateColorPalette(selectedColor)
                }.show()
            }

            binding.tvNoti.setOnClickListener {
                context.setScheduleAlarm(savedColor.color!!) {
                    binding.tvColorPalette.text = it
                }.show()
            }
        }

        private fun updateColorPalette(selectedColor: ColorEnum) {
            with(binding) {
                ivColor.visibility =
                    if (selectedColor == ColorEnum.UNKNOWN) View.GONE else View.VISIBLE
                tvColorPalette.text = selectedColor.title?.let { context.getString(it) }
                onColorSelected(selectedColor)

                val backgroundColor =
                    ContextCompat.getColor(context, selectedColor.color ?: R.color.main_color)
                val backgroundDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(backgroundColor)
                }
                ivColor.setImageDrawable(backgroundDrawable)
                savedColor = selectedColor
            }
        }

        private fun notifyTextChange(schedule: String, description: String) {
            onTextChanged(adapterPosition, schedule, description)
        }

        private fun String?.parseDateTime(): Date? = this?.let {
            SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).parse(this)
        }

        private fun setDateTimePicker(
            datePicker: DatePicker,
            timePicker: TimePicker,
            dateTime: Date,
            isStartDate: Boolean
        ) {
            Log.d("1234", "datetime=$dateTime")
            val calendar = Calendar.getInstance().apply {
                time = dateTime
            }
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

            // dateTime에서 시간 및 분 정보 추출
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            Log.d("1234", "hourOfDay=$hourOfDay, minute=$minute")

            fun updateDateText(selectedDateTime: Calendar, isStart: Boolean) {
                val formattedDateTime = dateFormat.format(selectedDateTime.time)
                if (isStart) {
                    binding.tvStartDate.text = formattedDateTime
                    if (isStartDate) {
                        val endDateCalendar = Calendar.getInstance().apply {
                            time = selectedDateTime.time
                            add(Calendar.DAY_OF_MONTH, 2)
                        }
                        updateDateText(endDateCalendar, isStart = false)
                        dateChangeListener("startDate", formattedDateTime)
                    }
                } else {
                    binding.tvEndDate.text = formattedDateTime
                    if (!isStartDate) {
                        val startDateCalendar = binding.tvStartDate.text.toString().parseDateTime()
                        if (selectedDateTime.time.before(startDateCalendar)) {
                            context.showToast(context.getString(R.string.end_time_selection_error))
                            return
                        }
                    }
                    dateChangeListener("endDate", formattedDateTime)
                }
            }

            datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ) { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                updateDateText(calendar, isStartDate)
            }

            updateDateText(calendar, isStartDate)

            timePicker.setOnTimeChangedListener { _, newHourOfDay, newMinute ->
                Log.d("1234", "newHourOfDay=$newHourOfDay, newMinute=$newMinute")
                updateDateText(calendar.apply {
                    set(Calendar.HOUR_OF_DAY, newHourOfDay)
                    set(Calendar.MINUTE, newMinute)
                }, isStartDate)

            }

            timePicker.hour = hourOfDay
            timePicker.minute = minute
        }
    }

}