package com.seven.colink.ui.group.calendar.register

import com.seven.colink.ui.group.calendar.register.adapter.ScheduleRegisterListAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentRegisterScheduleBinding
import com.seven.colink.ui.group.calendar.register.viewmodel.RegisterScheduleViewModel
import com.seven.colink.ui.group.calendar.status.CalendarButtonUiState
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterScheduleFragment : Fragment() {
    private var _binding: FragmentRegisterScheduleBinding? = null
    private val binding: FragmentRegisterScheduleBinding get() = _binding!!
    private val viewModel: RegisterScheduleViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()
    private var originalStatusBarColor: Int = 0
    private val scheduleRegisterListAdapter by lazy {
        ScheduleRegisterListAdapter(
            onColorSelected = { selectedColor ->
                setStatusBarAndToolbarColor(selectedColor.color ?: R.color.main_color)
                viewModel.onChangedColor(selectedColor)
            },
            notifyOnClickItem = { position, item ->

            },
            dateChangeListener = { key, datetime ->
                viewModel.onChangedDateTime(key, datetime)
            },
            onTextChanged = { _, schedule, description ->
                viewModel.onChangedEditTexts(schedule, description)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewSchedule.adapter = scheduleRegisterListAdapter
        originalStatusBarColor = requireActivity().window.statusBarColor
        tvComplete.setOnClickListener {
            lifecycleScope.launch {
                viewModel.onClickCompleteButton()
            }
        }
        fbDeleteSchedule.setOnClickListener {
            viewModel.onClickDelete()
        }

        ivGroupFinish.setOnClickListener {
            if (!parentFragmentManager.isStateSaved) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            uiState.collect { scheduleList ->
                scheduleRegisterListAdapter.submitList(scheduleList)
                when (scheduleList?.firstOrNull()?.buttonUiState) {
                    CalendarButtonUiState.Create -> {
                        binding.tvComplete.apply {
                            visibility = View.VISIBLE
                            text = "등록"
                        }
                        binding.fbDeleteSchedule.visibility = View.GONE
                    }

                    CalendarButtonUiState.Editing -> {
                        binding.tvComplete.apply {
                            visibility = View.VISIBLE
                            text = "완료"
                        }
                        binding.fbDeleteSchedule.visibility = View.VISIBLE
                    }

                    CalendarButtonUiState.Detail -> {
                        binding.tvComplete.visibility = View.GONE
                        binding.fbDeleteSchedule.visibility = View.GONE
                    }

                    CalendarButtonUiState.Update -> {
                        binding.tvComplete.apply {
                            visibility = View.VISIBLE
                            text = "편집"
                        }
                        binding.fbDeleteSchedule.visibility = View.GONE
                    }

                    else -> Unit
                }
            }

        }
    }


    private fun initSharedViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.setKey(it)
                }
            }
        }

        lifecycleScope.launch {
            scheduleKey.collect {
                if (it != null) {
                    viewModel.setEntity(it)
                }
            }
        }

        lifecycleScope.launch {
            scheduleEntryType.collect {
                if (it != null) {
                    viewModel.setEntryType(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.complete.collect {
                if (!parentFragmentManager.isStateSaved) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onPause() {
        resetStatusBarColor()
        super.onPause()
    }

    private fun setStatusBarAndToolbarColor(color: Int) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), color)
        binding.toolBar.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun resetStatusBarColor() {
        requireActivity().window.statusBarColor = originalStatusBarColor
    }

}