package com.seven.colink.ui.post.register.setgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentSetGroupBinding
import com.seven.colink.ui.group.GroupActivity
import com.seven.colink.ui.post.register.setgroup.viewmodel.SetGroupViewModel
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SetGroupFragment : Fragment() {

    private val binding by lazy {
        FragmentSetGroupBinding.inflate(layoutInflater)
    }

    private val sharedViewModel: PostSharedViewModel by activityViewModels()
    private val viewModel: SetGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            groupType.collect {
                binding.tvDialogDescription.text = context?.getString(
                    R.string.group_dialog_description, context?.getString(
                        if (
                            it == GroupType.PROJECT
                        ) R.string.project_kor else R.string.study_kor
                    )
                )
            }
        }

        lifecycleScope.launch {
            combine(key, groupType, viewModel.onGroupEvent) { key,type,event ->
                if (event) Pair(key,type) else null
            }.collect {
                it?.let { (key, type) ->
                    startActivity(
                        GroupActivity.newIntent(
                            requireContext(),
                            groupType = type,
                            entryType = PostEntryType.UPDATE,
                            key = key
                        )
                    )
                }
            }
        }
    }

    private fun initView() = with(binding) {
        btMoveGroupPage.setOnClickListener { viewModel.onGroupEdit() }
        btFinish.setOnClickListener { requireActivity().finish() }
    }


}