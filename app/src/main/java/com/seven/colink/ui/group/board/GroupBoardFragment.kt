package com.seven.colink.ui.group.board


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentGroupBoardBinding
import com.seven.colink.ui.group.board.adapter.GroupBoardListAdapter
import com.seven.colink.ui.group.content.GroupContentFragment
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import com.seven.colink.ui.post.content.model.ContentOwnerButtonUiState
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupBoardFragment : Fragment() {
    private var _binding: FragmentGroupBoardBinding? = null
    private val binding: FragmentGroupBoardBinding get() = _binding!!
    private val viewModel: GroupBoardViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()

    private val groupBoardListAdapter by lazy {
        GroupBoardListAdapter(
            requireContext(),
            onClickItem = { _, item ->
                when (item) {
                    is GroupBoardItem.GroupItem -> {
                        viewModel.onClickStatusButton()
                    }

                    is GroupBoardItem.PostItem -> {
                        startActivity(
                            PostActivity.newIntent(
                                context = requireActivity(),
                                key = item.post.key
                            )
                        )

                    }

                    is GroupBoardItem.MemberItem -> {
                        // TODO 멤버 상세 화면 이동
                    }

                    is GroupBoardItem.TitleItem -> {
                        // TODO 지원 요청 목록, 멤버 추천 화면 이동
                    }

                    else -> Unit
                }
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewBoard.adapter = groupBoardListAdapter
        binding.ivGroupFinish.setOnClickListener {
            activity?.finish()
        }
        binding.tvComplete.setOnClickListener {
            viewModel.onClickUpdate()
        }
    }

    private fun initViewModel() = with(viewModel) {
        uiStateList.observe(requireActivity()) {
            groupBoardListAdapter.submitList(it)
        }

        entity.observe(requireActivity()) {
            when (it?.buttonUiState) {
                ContentOwnerButtonUiState.Owner -> {
                    binding.tvComplete.visibility = View.VISIBLE
                }

                else -> binding.tvComplete.visibility = View.INVISIBLE
            }

        }

        event.observe(requireActivity()) { event ->
            when (event) {
                is GroupContentEvent.Update -> {
                    if (event.isOwner) {
                        sharedViewModel.setEntryType(PostEntryType.UPDATE)
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.fg_activity_group, GroupContentFragment())
//                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            }
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}