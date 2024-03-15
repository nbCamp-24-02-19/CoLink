package com.seven.colink.ui.evaluation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentEvaluationStudyBinding
import com.seven.colink.util.dialog.setDialog
import dagger.hilt.android.AndroidEntryPoint

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

        arguments?.let {
            studyUserList = it.getInt("studyUserList")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initView()
        firstPage()
        lastPage()
    }

    private fun initView(){
        setViewPager()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                evaluationViewModel.updateStudyMembers(
                    studyUserList,
                    binding.rbEvalQuestion1.rating,
                    binding.rbEvalQuestion2.rating,
                    binding.rbEvalQuestion3.rating
                )
            }
        })

        with(binding){
            tvEvalPrev.visibility = View.VISIBLE
            tvEvalPrev.setOnClickListener {
                viewPager.setCurrentItem(currentPage - 1, true)
            }

            tvEvalNext.visibility = View.VISIBLE
            tvEvalNext.setOnClickListener {
                viewPager.setCurrentItem(currentPage + 1 , true)
            }

            btnEvalNext.setOnClickListener {
                viewPager.setCurrentItem(currentPage + 1, true)
            }
        }

    }

    private fun initViewModel() = with(evaluationViewModel){
        evalStudyMembersData.observe(viewLifecycleOwner){
            setView(it?.get(studyUserList))
            firstPage()
            lastPage()
        }
    }

    private fun firstPage() {
        val firstPage = studyUserList == 0
        if (firstPage){
            binding.tvEvalPrev.visibility = View.INVISIBLE
            binding.tvEvalNext.visibility = View.VISIBLE
            binding.tvEvalNext.setOnClickListener {
                setViewPager()
                if (currentPage == 0) {
                    viewPager.setCurrentItem(currentPage + 1, true)
                }
            }
        }
    }

    private fun lastPage() {
        val lastPage = studyUserList + 1 == evaluationViewModel.evalStudyMembersData.value?.size
        if (lastPage){
            binding.tvEvalNext.visibility = View.INVISIBLE
            binding.tvEvalPrev.setOnClickListener {
                setViewPager()
                if (currentPage > 0){
                    viewPager.setCurrentItem(currentPage - 1, true)
                }
            }

            binding.btnEvalNext.text = getString(R.string.eval_project_done)
            binding.btnEvalNext.setOnClickListener {
                Log.d("Evaluation" ,"BTN CLICKED")
                requireContext().setDialog(
                    title = getString(R.string.eval_dialog_title),
                    message = getString(R.string.eval_dialog_des),
                    confirmAction = {
                        evaluationViewModel.updateStudyMembers(
                            studyUserList,
                            binding.rbEvalQuestion1.rating,
                            binding.rbEvalQuestion2.rating,
                            binding.rbEvalQuestion3.rating
                        )
                        for (i in 0..studyUserList){
                            evaluationViewModel.updateStudyUserGrade(i)
                        }
                        it.dismiss()
                        activity?.finish()
                    },
                    cancelAction = {it.dismiss()}
                ).show()
            }
        }
    }

    private fun setViewPager(){
        viewPager = requireActivity().findViewById(R.id.vp_eval_viewpager)
        currentPage = viewPager.currentItem
    }

    private fun setView(data: EvaluationData.EvalStudy?) = with(binding){
        ivEvalProfileImage.load(data?.photoUrl)
        tvEvalTitle.text = getString(R.string.eval_study_title) + " " + data?.name + getString(R.string.eval_request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}