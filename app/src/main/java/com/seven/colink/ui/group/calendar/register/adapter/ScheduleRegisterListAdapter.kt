package com.seven.colink.ui.group.calendar.register.adapter

import android.graphics.drawable.GradientDrawable
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
import com.seven.colink.util.convert.getDateByState
import com.seven.colink.util.dialog.enum.ColorEnum
import com.seven.colink.util.dialog.setScheduleAlarm
import com.seven.colink.util.dialog.setScheduleColor
import com.seven.colink.util.showToast
import com.seven.colink.util.status.ScheduleDateType
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
            oldItem.authId == newItem.authId

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
                    isStartDatePickerVisible = false
                    binding.startDatePicker.visibility = View.GONE
                    binding.startTimePicker.visibility = View.GONE
                } else {
                    isEndDatePickerVisible = false
                    binding.endDatePicker.visibility = View.GONE
                    binding.endTimePicker.visibility = View.GONE

                    isStartDatePickerVisible = true
                    binding.startDatePicker.visibility = View.VISIBLE
                    binding.startTimePicker.visibility = View.VISIBLE
                }
            } else {
                if (isEndDatePickerVisible) {
                    isEndDatePickerVisible = false
                    binding.endDatePicker.visibility = View.GONE
                    binding.endTimePicker.visibility = View.GONE
                } else {
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
                tvStartDate.text = item.startDate ?: getDateByState(ScheduleDateType.CURRENT)
                tvEndDate.text = item.endDate ?: getDateByState(ScheduleDateType.NEXT_DAY)
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

            val startDate = item.startDate?.parseDateTime() ?: Calendar.getInstance().time
            val endDate = item.endDate?.parseDateTime() ?: Calendar.getInstance()
                .apply { add(Calendar.DAY_OF_MONTH, 1) }.time

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

        private fun updateDateTimeText(selectedDateTime: Calendar, isStartDate: Boolean) {
            val formattedDateTime = selectedDateTime.formatDateTime()
            with(binding) {
                if (isStartDate) {
                    val endDateDateTime = tvEndDate.text.toString().parseDateTime()
                    if (selectedDateTime.time.after(endDateDateTime)) {
                        context.showToast("시작 시간은 종료 시간 이전이어야 합니다.")
                        return
                    }
                    dateChangeListener("startDate", formattedDateTime)
                } else {
                    val startDateTime = tvStartDate.text.toString().parseDateTime()
                    if (selectedDateTime.time.before(startDateTime)) {
                        context.showToast("종료 시간은 시작 시간 이후여야 합니다.")
                        return
                    }
                    dateChangeListener("endDate", formattedDateTime)
                }
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

        private fun Calendar.formatDateTime(): String =
            SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(time)

        private fun setDateTimePicker(
            datePicker: DatePicker,
            timePicker: TimePicker,
            dateTime: Date,
            isStartDate: Boolean
        ) {
            val calendar = Calendar.getInstance().apply { time = dateTime }
            datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ) { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateDateTimeText(calendar, isStartDate)
            }
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)
            timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeText(calendar, isStartDate)
            }
        }
    }

}
