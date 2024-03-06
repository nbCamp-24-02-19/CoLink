package com.seven.colink.ui.post.register.setgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentSetGroupBinding
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.util.status.GroupType
import kotlinx.coroutines.launch

class SetGroupFragment : Fragment() {

    private val binding by lazy {
        FragmentSetGroupBinding.inflate(layoutInflater)
    }

    private val sharedViewModel: PostSharedViewModel by activityViewModels()

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
    }

    private fun initView() = with(binding) {

        btMoveGroupPage.setOnClickListener { requireActivity().finish() }
        btFinish.setOnClickListener { requireActivity().finish() }
    }


}