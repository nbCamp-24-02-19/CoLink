package com.seven.colink.ui.group

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentGroupBinding
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.showToast
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val groupAdapter by lazy {
        GroupAdapter(
            requireContext(),
            onClickItem = { _, item -> handleItemClick(item) },
            onClickAddButton = { _, item -> handleItemClick(item) },
        )
    }

    private val groupViewModel: GroupViewModel by viewModels()

    private val groupTypeOptions: List<String>
        get() = listOf(
            getString(R.string.project_kor),
            getString(R.string.study_kor)
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setObserve()
        initViewModel()

    }

    private fun initView() = with(binding) {
        rvGroupRecyclerView.adapter = groupAdapter
        rvGroupRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initViewModel(){
        groupViewModel.getCurrentUser()
        lifecycleScope.launch {
            groupViewModel.getInPost()
        }
    }

    private fun setObserve() {

        groupViewModel.joinList.observe(viewLifecycleOwner) {
            groupViewModel.itemUpdate()
        }

        groupViewModel.groupData.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }

    }

    private fun handleItemClick(item: GroupData) {
        when (item) {
            is GroupData.GroupList -> {
                if (groupViewModel.checkLogin.value == true) {
                    lifecycleScope.launch {
                        if (item.key != null) {
                            val intent = GroupActivity.newIntent(
                                context = requireContext(),
                                key = item.key
                            )
                            startActivity(intent)
                        } else {
                            requireContext().showToast("알 수 없는 오류")
                        }
                    }
                }
                else {
                    requireContext().showToast("로그인 후 이용해 주세요")
                }
            }

            is GroupData.GroupAdd -> {
                if (groupViewModel.checkLogin.value == true) {
                    groupTypeOptions.setDialog(
                        requireContext(),
                        getString(R.string.group_type_options)
                    ) { selectedOption ->
                        when (selectedOption) {
                            getString(R.string.project_kor) -> {
                                startActivity(
                                    PostActivity.newIntent(
                                        requireContext(),
                                        GroupType.PROJECT
                                    )
                                )
                            }

                            getString(R.string.study_kor) -> {
                                startActivity(
                                    PostActivity.newIntent(
                                        requireContext(),
                                        GroupType.STUDY
                                    )
                                )
                            }

                            else -> Unit
                        }
                    }.show()
                } else {
                    requireContext().setDialog(
                        title = "로그인 필요",
                        message = "서비스를 이용하기 위해서는 로그인이 필요합니다. \n로그인 페이지로 이동하시겠습니까?",
                        confirmAction = {
                            val intent = Intent(requireContext(), SignInActivity::class.java)
                            startActivity(intent)
                            it.dismiss()
                        },
                        cancelAction = { it.dismiss() }
                    ).show()
                }
            }

            is GroupData.GroupWant -> {
                lifecycleScope.launch {
                    if (item.key != null) {
                        val intent = GroupActivity.newIntent(
                            context = requireContext(),
                            key = item.key
                        )
                        startActivity(intent)
                    } else {
                        requireContext().showToast("알 수 없는 오류")
                    }
                }
            }

            else -> throw UnsupportedOperationException("Unhandled type: $item")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}