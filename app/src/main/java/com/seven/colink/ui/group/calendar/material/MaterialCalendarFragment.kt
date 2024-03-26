package com.seven.colink.ui.group.calendar.material

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMaterialCalendarBinding
import com.seven.colink.ui.group.calendar.CalendarViewModel
import com.seven.colink.ui.group.calendar.adapter.ScheduleListAdapter
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import com.seven.colink.ui.group.calendar.register.RegisterScheduleFragment
import com.seven.colink.ui.group.calendar.status.CalendarEntryType
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class MaterialCalendarFragment : Fragment() {
    private val binding: FragmentMaterialCalendarBinding by lazy {
        FragmentMaterialCalendarBinding.inflate(layoutInflater)
    }
    private val viewModel: CalendarViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()
    private val scheduleListAdapter: ScheduleListAdapter by lazy {
        ScheduleListAdapter(
            onClickItem = { item ->
                onScheduleItemClick(item)
            }
        )
    }

    private lateinit var dayDecorator: DayViewDecorator
    private lateinit var todayDecorator: DayViewDecorator
    private lateinit var selectedMonthDecorator: DayViewDecorator
    private lateinit var sundayDecorator: DayViewDecorator
    private lateinit var saturdayDecorator: DayViewDecorator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewSchedule.adapter = scheduleListAdapter
        recyclerViewSchedule.itemAnimator = null
        binding.ivCalendarFinish.setOnClickListener {
            if (!parentFragmentManager.isStateSaved) {
                parentFragmentManager.popBackStack()
            }
        }
        binding.fbRegisterSchedule.setOnClickListener {
            sharedViewModel.setScheduleKey(null)
            sharedViewModel.setScheduleEntryType(CalendarEntryType.CREATE)
            parentFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.enter_animation,
                    R.anim.exit_animation,
                    R.anim.enter_animation,
                    R.anim.exit_animation
                )
                replace(
                    R.id.fg_activity_group,
                    RegisterScheduleFragment()
                )
                addToBackStack(null)
                commit()
            }
        }
        with(calendarView) {
            dayDecorator = CalendarDecorators.dayDecorator(requireContext())
            todayDecorator = CalendarDecorators.todayDecorator(requireContext())
            sundayDecorator = CalendarDecorators.sundayDecorator()
            saturdayDecorator = CalendarDecorators.saturdayDecorator()
            selectedMonthDecorator = CalendarDecorators.selectedMonthDecorator(
                requireContext(),
                CalendarDay.today().month
            )

            addDecorators(
                dayDecorator,
                todayDecorator,
                sundayDecorator,
                saturdayDecorator,
                selectedMonthDecorator
            )

            setOnMonthChangedListener { widget, date ->
                widget.clearSelection()
                removeDecorators()
                invalidateDecorators()
                selectedMonthDecorator =
                    CalendarDecorators.selectedMonthDecorator(requireContext(), date.month)
                addDecorators(
                    dayDecorator,
                    todayDecorator,
                    sundayDecorator,
                    saturdayDecorator,
                    selectedMonthDecorator
                )
                val today = LocalDate.now()
                val clickedDay = if (date.toLocalDate().month == today.month)
                    CalendarDay.from(today.year, today.monthValue, today.dayOfMonth)
                else
                    CalendarDay.from(date.year, date.month, 1)

                widget.setDateSelected(clickedDay, true)
                viewModel.filterScheduleListByDate(clickedDay.toLocalDate())
                viewModel.filterDataByMonth(clickedDay.toLocalDate())
            }

            setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))
            setHeaderTextAppearance(R.style.CalendarWidgetHeader)
            setOnDateChangedListener { _, date, _ ->
                val localDate = date.toLocalDate()
                viewModel.filterScheduleListByDate(localDate)
            }
        }
    }

    private fun initViewModel() {
        viewModel.apply {
            lifecycleScope.launch {
                filteredByDate.collect { item ->
                    scheduleListAdapter.submitList(item.list)

                    val dayOfWeekString = item.date?.dayOfWeek?.let {
                        val weekdaysArray =
                            requireContext().resources.getStringArray(R.array.custom_weekdays)
                        weekdaysArray.getOrNull(it.ordinal) ?: ""
                    } ?: ""
                    binding.tvDate.text =
                        "${item.date?.monthValue}.${item.date?.dayOfMonth}. $dayOfWeekString"
                }
            }

            lifecycleScope.launch {
                uiState.collect { uiState ->
                }
            }

            lifecycleScope.launch {
                filteredByMonth.collect { uiState ->
                    val eventDecorator =
                        CalendarDecorators.eventDecorator(requireContext(), uiState)
                    binding.calendarView.addDecorator(eventDecorator)
                }
            }
        }

        sharedViewModel.apply {
            lifecycleScope.launch {
                key.collect {
                    it?.let { key ->
                        val currentDate = binding.calendarView.currentDate.toLocalDate()
                        viewModel.setEntity(key, currentDate)
                    }
                }
            }
        }
    }

    private fun onScheduleItemClick(item: ScheduleModel) {
        sharedViewModel.setScheduleKey(item.key!!)
        sharedViewModel.setScheduleEntryType(CalendarEntryType.DETAIL)
        parentFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.enter_animation,
                R.anim.exit_animation,
                R.anim.enter_animation,
                R.anim.exit_animation
            )
            replace(
                R.id.fg_activity_group,
                RegisterScheduleFragment()
            )
            addToBackStack(null)
            commit()
        }
        viewModel.clearFilteredByMonth()
    }

    private fun CalendarDay.toLocalDate(): LocalDate {
        return LocalDate.of(year, month, day)
    }

}
