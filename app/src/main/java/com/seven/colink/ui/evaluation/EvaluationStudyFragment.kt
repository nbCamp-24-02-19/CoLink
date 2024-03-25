package com.seven.colink.ui.evaluation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentEvaluationStudyBinding
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.status.PageState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EvaluationStudyFragment : Fragment() {
    private var _binding: FragmentEvaluationStudyBinding? = null
    private val binding get() = _binding!!

    private var studyUserList: Int = 0

    private val evaluationViewModel: EvaluationViewModel by activityViewModels()

    private lateinit var viewPager: ViewPager2
    private var currentPage: Int = 0

    companion object {
        fun newInstanceStudy(position: Int)
                : EvaluationStudyFragment {
            val fragment = EvaluationStudyFragment()
            val args = Bundle()
            args.putInt("studyUserList", position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEvaluationStudyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initView()
    }

    private fun initView() {
        setViewPager()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                evaluationViewModel.updatePage(position)
            }
        })
    }

    private fun initViewModel() = with(evaluationViewModel) {
        lifecycleScope.launch {
            combine(currentPage, evalStudyMembersData.asFlow()) { page, data ->
                Pair(page, data)
            }.collect { (page, data) ->
                setView(data?.get(page.num))
            }
        }

        lifecycleScope.launch {
            currentPage.collect { state ->
                when (state) {
                    PageState.FIRST -> firstPage()
                    PageState.MIDDLE -> middlePage()
                    PageState.LAST -> lastPage()
                }

                updateMembers(state.num)
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
                updateStudyUserGrade(group, uid)
                requireActivity().finish()
            }
        }
    }

    private fun updateMembers(position: Int) =
        evaluationViewModel.updateStudyMembers(
            position,
            binding.rbEvalQuestion1.rating,
            binding.rbEvalQuestion2.rating,
            binding.rbEvalQuestion3.rating
        )

    private fun firstPage() = with(binding) {
        tvEvalPrev.visibility = View.INVISIBLE
        tvEvalNext.visibility = View.VISIBLE
        tvEvalNext.setOnClickListener {
            nextPage(viewPager)
        }
        btnEvalNext.text = getString(R.string.eval_project_next)
        btnEvalNext.setOnClickListener {
            nextPage(viewPager)
        }
    }

    private fun middlePage() = with(binding) {
        tvEvalPrev.visibility = View.VISIBLE
        tvEvalPrev.setOnClickListener {
            prevPage(viewPager)
        }
        tvEvalNext.visibility = View.VISIBLE
        tvEvalNext.setOnClickListener {
            nextPage(viewPager)
        }
        btnEvalNext.text = getString(R.string.eval_project_next)
        btnEvalNext.setOnClickListener {
            nextPage(viewPager)
        }
    }

    private fun lastPage() = with(binding) {
        binding.tvEvalNext.visibility = View.INVISIBLE
        binding.tvEvalPrev.setOnClickListener {
            prevPage(viewPager)
        }
        binding.btnEvalNext.text = getString(R.string.eval_project_done)
        binding.btnEvalNext.setOnClickListener {
            requireContext().setDialog(
                title = getString(R.string.eval_dialog_title),
                message = getString(R.string.eval_dialog_des),
                confirmAction = {
                    updateGrade()
                    activity?.finish()
                },
                cancelAction = { it.dismiss() }
            ).show()
        }

    }

    private fun setViewPager() {
        viewPager = requireActivity().findViewById(R.id.vp_eval_viewpager)
        currentPage = viewPager.currentItem
    }

    private fun setView(data: EvaluationData.EvalStudy?) = with(binding) {
        ivEvalProfileImage.load(data?.photoUrl)
        tvEvalTitle.text =
            getString(R.string.eval_study_title) + " " + data?.name + getString(R.string.eval_request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun prevPage(viewPager: ViewPager2) {
        viewPager.setCurrentItem(currentPage - 1, true)
    }

    private fun nextPage(viewPager: ViewPager2) {
        viewPager.setCurrentItem(currentPage + 1, true)
    }
}