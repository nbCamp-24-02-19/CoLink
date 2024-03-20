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
import com.seven.colink.ui.userdetail.UserDetailActivity
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
            onClickItem = {item ->
                when (item) {
                    is GroupBoardItem.MemberItem -> {
                        startActivity(UserDetailActivity.newIntent(requireContext(),item.userInfo.uid?: ""))
                    }

                    else -> Unit
                }
            },
            onClickView = { item, view ->
                when (view.id) {
                    R.id.bt_approval -> {
                        if (item is GroupBoardItem.MemberApplicationInfoItem) {
                            lifecycleScope.launch {
                                viewModel.onClickApproval(item.applicationInfo)
                                viewModel.setNotify(item.userInfo.uid)
                            }
                        }
                    }

                    R.id.bt_refuse -> {
                        if (item is GroupBoardItem.MemberApplicationInfoItem) {
                            lifecycleScope.launch {
                                viewModel.onClickRefuse(item.applicationInfo)
                            }
                        }
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