package com.seven.colink.ui.group.board.board


import android.content.Intent
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
import com.seven.colink.ui.group.board.board.adapter.GroupBoardListAdapter
import com.seven.colink.ui.group.board.list.ApplyRequestFragment
import com.seven.colink.ui.group.content.GroupContentFragment
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.promotion.ProductPromotionActivity
import com.seven.colink.util.Constants
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
            onClickItem = { item ->
                when (item) {
                    is GroupBoardItem.GroupItem -> {
//                        viewModel.onClickStatusButton()
                        val intent = Intent(requireContext(), ProductPromotionActivity::class.java)
                        intent.putExtra(Constants.EXTRA_ENTITY_KEY,item.key)
                        startActivity(intent)
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

                    else -> Unit
                }
            },
            onClickView = { _, view ->
                when (view.id) {
                    R.id.tv_apply_request -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(
                                R.id.fg_activity_group,
                                ApplyRequestFragment()
                            )
                            addToBackStack(null)
                            commit()
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
        _binding = FragmentGroupBoardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()

    }

    private fun initView() = with(binding) {
        setListAdapter()
        binding.ivGroupFinish.setOnClickListener {
            activity?.finish()
        }
        binding.tvComplete.setOnClickListener {
            sharedViewModel.setEntryType(PostEntryType.UPDATE)
            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fg_activity_group,
                    GroupContentFragment()
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun setListAdapter() = with(binding) {
        recyclerViewBoard.adapter = groupBoardListAdapter
    }

    private fun initViewModel() = with(viewModel) {
        uiStateList.observe(requireActivity()) {
            groupBoardListAdapter.submitList(it)
        }

        entity.observe(requireActivity()) {
            when (it?.buttonUiState) {
                ContentButtonUiState.Manager -> {
                    binding.tvComplete.visibility = View.VISIBLE
                }

                else -> binding.tvComplete.visibility = View.INVISIBLE
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