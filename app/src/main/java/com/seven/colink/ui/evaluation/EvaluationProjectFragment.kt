package com.seven.colink.ui.evaluation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentEvaluationProjectBinding
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.PageState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EvaluationProjectFragment : Fragment() {
    private var _binding: FragmentEvaluationProjectBinding? = null
    private val binding get() = _binding!!

    private val evaluationViewModel: EvaluationViewModel by activityViewModels()

    private lateinit var viewPager: ViewPager2
    private var currentPage: Int = 0

    private var projectUserListPosition: Int = 0
    companion object {
        fun newInstanceProject(position: Int)
                : EvaluationProjectFragment {
            val fragment = EvaluationProjectFragment()
            val args = Bundle()
            args.putInt("projectUserList", position)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEvaluationProjectBinding.inflate(inflater, container, false)
        arguments?.let {
            projectUserListPosition = it.getInt("projectUserList", 0)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onResume() {
        initViewModel()
        currentPage = projectUserListPosition
        super.onResume()
    }

    private fun initView() {
        setViewPager()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (projectUserListPosition == position) evaluationViewModel.updatePage(position)
            }
        })

        with(binding) {
            tvEvalPrev.visibility = View.VISIBLE
            tvEvalPrev.setOnClickListener {
                viewPager.setCurrentItem(currentPage - 1, true)
            }

            tvEvalNext.visibility = View.VISIBLE
            tvEvalNext.setOnClickListener {
                viewPager.setCurrentItem(currentPage + 1, true)
            }

            btnEvalNext.setOnClickListener {
                viewPager.setCurrentItem(currentPage + 1, true)
            }
        }
    }

    private fun initViewModel() = with(evaluationViewModel) {
        lifecycleScope.launch {
            combine(currentPage, evalProjectMembersData.asFlow()) { page, data ->
                Pair(page, data)
            }.collect { (page, data) ->
                setView(data?.get(page.num))
            }
        }

        lifecycleScope.launch {
            currentPage.collect { state ->
                if (projectUserListPosition == state.num) {
                    when (state) {
                        PageState.FIRST -> firstPage()
                        PageState.MIDDLE -> Unit
                        PageState.LAST -> lastPage()
                    }

                    updateMembers(state.num)
                }
            }
        }
    }

    private fun updateGrade() = with(evaluationViewModel) {
        lifecycleScope.launch {
            combine(currentGroup, currentUid, currentPage) { group, uid, page ->
                Triple(group, uid, page)
            }.filter {
                it.third == PageState.LAST
            }.collect { (group, uid, _) ->
                updateProjectUserGrade(group, uid)
            }
        }
    }

    private fun updateMembers(position: Int) =
        if (currentPage == position) {
            evaluationViewModel.updateProjectMembers(
                position,
                binding.rbEvalQuestion1.rating,
                binding.rbEvalQuestion2.rating,
                binding.rbEvalQuestion3.rating,
                binding.rbEvalQuestion4.rating,
                binding.rbEvalQuestion5.rating
            )
        } else {

        }

    private fun firstPage() {
        binding.tvEvalPrev.visibility = View.INVISIBLE
        binding.tvEvalNext.visibility = View.VISIBLE
        binding.tvEvalNext.setOnClickListener {
            setViewPager()
            if (currentPage == 0) {
                viewPager.setCurrentItem(currentPage + 1, true)
            }
        }
    }

    private fun lastPage() {
        binding.tvEvalNext.visibility = View.INVISIBLE
        binding.tvEvalPrev.setOnClickListener {
            setViewPager()
            if (currentPage > 0) {
                viewPager.setCurrentItem(currentPage - 1, true)
            }
        }

        binding.btnEvalNext.text = getString(R.string.eval_project_done)
        binding.btnEvalNext.setOnClickListener {
            requireContext().setDialog(
                title = getString(R.string.eval_dialog_title),
                message = getString(R.string.eval_dialog_des),
                confirmAction = {
                    updateMembers(currentPage)
                    updateGrade()
                    it.dismiss()
                },
                cancelAction = { it.dismiss() }
            ).show()
        }
    }

    private fun setViewPager() {
        viewPager = requireActivity().findViewById(R.id.vp_eval_viewpager)
        currentPage = viewPager.currentItem
    }

    private fun setView(data: EvaluationData.EvalProject?) = with(binding) {
        ivEvalProfileImage.load(data?.photoUrl)
        tvEvalTitle.text =
            (getString(R.string.eval_project_title)) + " " + data?.name + (getString(R.string.eval_request))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}