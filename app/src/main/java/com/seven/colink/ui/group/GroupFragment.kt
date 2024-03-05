package com.seven.colink.ui.group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentGroupBinding
import com.seven.colink.ui.post.content.PostContentActivity
import com.seven.colink.ui.post.register.PostActivity
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

    }

    private fun initView() = with(binding) {
        rvGroupRecyclerView.adapter = groupAdapter
        rvGroupRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setObserve() {
        groupViewModel.groupData.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }
    }

    private fun handleItemClick(item: GroupData) {
        when (item) {
            is GroupData.GroupList -> {
                lifecycleScope.launch {
                    if (item.key != null) {
                        val intent = PostContentActivity.newIntent(
                            requireContext(),
                            item.key
                        )
                        startActivity(intent)
                    } else {
                        requireContext().showToast("알 수 없는 오류")
                    }
                }
            }

            is GroupData.GroupAdd -> {
                groupTypeOptions.setDialog(
                    requireContext(),
                    getString(R.string.group_type_options)
                ) { selectedOption ->
                    when (selectedOption) {
                        getString(R.string.project_kor) -> {
                            startActivity(
                                PostActivity.newIntentForCreate(
                                    requireContext(),
                                    GroupType.PROJECT
                                )
                            )
                        }

                        getString(R.string.study_kor) -> {
                            startActivity(
                                PostActivity.newIntentForCreate(
                                    requireContext(),
                                    GroupType.STUDY
                                )
                            )
                        }

                        else -> Unit
                    }
                }.show()
            }

            is GroupData.GroupWant -> {
                lifecycleScope.launch {
                    if (item.key != null) {
                        val intent = PostContentActivity.newIntent(
                            requireContext(),
                            item.key
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