package com.seven.colink.ui.group.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.FragmentCalendarBinding
import com.seven.colink.ui.group.calendar.adapter.MonthListAdapter
import com.seven.colink.ui.group.calendar.adapter.ScheduleListAdapter
import com.seven.colink.ui.group.calendar.model.ScheduleItem
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import com.seven.colink.ui.group.calendar.register.RegisterScheduleFragment
import com.seven.colink.ui.group.calendar.status.CalendarEntryType
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()

    private val scrollAmount: Int by lazy {
        resources.displayMetrics.widthPixels
    }

    private val scheduleListAdapter: ScheduleListAdapter by lazy {
        ScheduleListAdapter{item ->
            when (item){
                is ScheduleItem.ScheduleModel -> {
                    onScheduleItemClick(item)
                }

                else -> Unit
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() {
        with(binding) {
            rcSchedule.adapter = scheduleListAdapter
            fbRegisterSchedule.setOnClickListener {
                navigateToRegisterSchedule()
            }
            val snap = PagerSnapHelper()
            snap.attachToRecyclerView(binding.rcCalendar)
        }
    }

    private fun initViewModel() {
        viewModel.apply {
            lifecycleScope.launch {
                filteredSchedules.collect {
                    scheduleListAdapter.submitList(it)
                }
            }

            lifecycleScope.launch {
                uiState.collect { uiState ->
                    setupMonthList(uiState)
                }
            }
        }

        sharedViewModel.apply {
            lifecycleScope.launch {
                key.collect {
                    it?.let { key -> viewModel.setEntity(key) }
                }
            }
        }
    }

    private fun setupMonthList(uiState: List<ScheduleModel>) = with(binding) {
        val monthListManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val scrollAction: (Boolean) -> Unit = { isPreviousMonth ->
            if (isPreviousMonth) {
                rcCalendar.smoothScrollBy(-scrollAmount, 0)
            } else {
                rcCalendar.smoothScrollBy(scrollAmount, 0)
            }
        }

        val monthListAdapter = MonthListAdapter(
            ::onDayItemClick,
            uiState,
            scrollAction
        )
        with(rcCalendar) {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE / 2)
        }

        rcCalendar.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val adapter = recyclerView.adapter as MonthListAdapter
                }
            }
        })
    }

    private fun onDayItemClick(date: LocalDate) {
        viewModel.filterScheduleListByDate(date)
    }

    private fun onScheduleItemClick(item: ScheduleItem.ScheduleModel) {
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

    private fun navigateToRegisterSchedule() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
