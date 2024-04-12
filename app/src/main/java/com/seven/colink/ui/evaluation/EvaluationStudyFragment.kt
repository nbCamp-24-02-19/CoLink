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

    private val evaluationViewModel: EvaluationViewModel by activityViewModels()

    private lateinit var viewPager: ViewPager2
    private var currentPage: Int = 0

    private var studyUserListPosition: Int = 0

    private val ratingBar by lazy {
        with(binding) {
            listOf(
                rbEvalQuestion1,
                rbEvalQuestion2,
                rbEvalQuestion3
            )
        }
    }

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
        arguments?.let {
            studyUserListPosition = it.getInt("studyUserList", 0)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onResume() {
        initViewModel()
        setRatingListener()
        super.onResume()
    }

    private fun initView() {
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
            }
        }
    }

    private fun setRatingListener() {
        ratingBar.forEach{ ratingBars ->
            ratingBars.setOnRatingBarChangeListener{_, _, _ ->
                val ratings = ratingBar.map { it.rating }
                evaluationViewModel.updateStudyMembers(
                    viewPager.currentItem,
                    q1 = ratings.getOrNull(0),
                    q2 = ratings.getOrNull(1),
                    q3 = ratings.getOrNull(2),
                )
            }
        }
    }

    private fun firstPage() = with(binding) {
        setViewPager()

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
        setViewPager()

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
        setViewPager()

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
                    it.dismiss()
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