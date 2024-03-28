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

    private val ratingBar by lazy {
        with(binding) {
            listOf(
                rbEvalQuestion1,
                rbEvalQuestion2,
                rbEvalQuestion3,
                rbEvalQuestion4,
                rbEvalQuestion5,
            )
        }
    }

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
        setRatingListener()
        super.onResume()
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
            combine(currentPage, evalProjectMembersData.asFlow()) { page, data ->
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
                updateProjectUserGrade(group, uid)
            }
        }
    }

    private fun setRatingListener() {
        ratingBar.forEach { ratingBars ->
            ratingBars.setOnRatingBarChangeListener { _, _, _ ->
                val ratings = ratingBar.map { it.rating }
                evaluationViewModel.updateProjectMembers(
                    viewPager.currentItem,
                    q1 = ratings.getOrNull(0),
                    q2 = ratings.getOrNull(1),
                    q3 = ratings.getOrNull(2),
                    q4 = ratings.getOrNull(3),
                    q5 = ratings.getOrNull(4),
                )
            }
        }
    }

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
        tvEvalNext.visibility = View.INVISIBLE
        tvEvalPrev.setOnClickListener {
            prevPage(viewPager)
        }

        btnEvalNext.text = getString(R.string.eval_project_done)
        btnEvalNext.setOnClickListener {
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

    private fun setView(data: EvaluationData.EvalProject?) = with(binding) {
        ivEvalProfileImage.load(data?.photoUrl)
        tvEvalTitle.text =
            (getString(R.string.eval_project_title)) + " " + data?.name + (getString(R.string.eval_request))
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