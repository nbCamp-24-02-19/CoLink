package com.seven.colink.ui.group.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.seven.colink.R
import com.seven.colink.databinding.FragmentCalendarBinding
import com.seven.colink.ui.group.calendar.adapter.ScheduleListAdapter
import com.seven.colink.ui.group.calendar.adapter.MonthListAdapter
import com.seven.colink.ui.group.calendar.register.RegisterScheduleFragment
import com.seven.colink.ui.group.calendar.status.CalendarEntryType
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()

    private val scheduleListAdapter: ScheduleListAdapter by lazy {
        ScheduleListAdapter(
            onClickItem = { item ->
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
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
    }

    private fun initView() = with(binding) {
        rcSchedule.adapter = scheduleListAdapter

        fbRegisterSchedule.setOnClickListener {
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
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            filteredSchedules.collect {
                scheduleListAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            uiState.collect {
                val monthListManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                val monthListAdapter = MonthListAdapter(
                    onDayItemClick = {
                        viewModel.filterScheduleListByDate(it)
                    },
                    uiState = it
                )
                binding.rcCalendar.apply {
                    layoutManager = monthListManager
                    adapter = monthListAdapter
                    scrollToPosition(Int.MAX_VALUE / 2)
                }
            }
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.rcCalendar)
    }

    private fun initSharedViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.setEntity(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}