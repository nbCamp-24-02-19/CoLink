package com.seven.colink.ui.group.board.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentApplyRequestBinding
import com.seven.colink.ui.group.board.board.GroupBoardItem
import com.seven.colink.ui.group.board.board.adapter.GroupBoardListAdapter
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApplyRequestFragment : Fragment() {
    private var _binding: FragmentApplyRequestBinding? = null
    private val binding: FragmentApplyRequestBinding get() = _binding!!

    private val viewModel: ApplyRequestViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()

    private val groupBoardListAdapter by lazy {
        GroupBoardListAdapter(
            requireContext(),
            onClickItem = { _, item ->
                when (item) {
                    is GroupBoardItem.MemberItem -> {
                        // TODO 멤버 상세 화면 이동

                    }

                    is GroupBoardItem.TitleItem -> {
                        // TODO 지원 요청 목록, 멤버 추천 화면 이동
                    }

                    else -> Unit
                }
            },
            onClickView = { item, view ->
                when (view.id) {
                    R.id.bt_approval -> {
                        lifecycleScope.launch {
                            viewModel.onClickApproval()
                        }
                    }

                    R.id.bt_refuse -> {
                        viewModel.onClickRefuse()
                    }

                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplyRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewList.adapter = groupBoardListAdapter
        ivGroupFinish.setOnClickListener {
            if (!parentFragmentManager.isStateSaved) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(requireActivity()) {
            groupBoardListAdapter.submitList(it)
        }
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

}