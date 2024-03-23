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
import java.time.DayOfWeek
import java.time.LocalDate

@AndroidEntryPoint
class MaterialCalendarFragment : Fragment() {
    private var _binding: FragmentMaterialCalendarBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentMaterialCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewSchedule.adapter = scheduleListAdapter
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
                val clickedDay = CalendarDay.from(date.year, date.month, 1)
                widget.setDateSelected(clickedDay, true)
                viewModel.filterScheduleListByDate(date.toLocalDate())
                viewModel.filterDataByMonth(date.toLocalDate())
            }

            setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

            setHeaderTextAppearance(R.style.CalendarWidgetHeader)

            setOnRangeSelectedListener { widget, dates -> }

            setOnDateChangedListener { widget, date, selected ->
                val localDate = date.toLocalDate()
                viewModel.filterScheduleListByDate(localDate)
            }


        }
    }

    private fun initViewModel() {
        viewModel.apply {
            lifecycleScope.launch {
                filteredByDate.collect {
                    scheduleListAdapter.submitList(it.list)

                    val dayOfWeekString = when (it.date?.dayOfWeek) {
                        DayOfWeek.MONDAY -> "월"
                        DayOfWeek.TUESDAY -> "화"
                        DayOfWeek.WEDNESDAY -> "수"
                        DayOfWeek.THURSDAY -> "목"
                        DayOfWeek.FRIDAY -> "금"
                        DayOfWeek.SATURDAY -> "토"
                        DayOfWeek.SUNDAY -> "일"
                        else -> ""
                    }
                    binding.tvDate.text =
                        "${it.date?.monthValue}.${it.date?.dayOfMonth}. $dayOfWeekString"
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
                key.collect { it?.let { key -> viewModel.setEntity(key) } }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
    }

    private fun CalendarDay.toLocalDate(): LocalDate {
        return LocalDate.of(year, month, day)
    }
}
