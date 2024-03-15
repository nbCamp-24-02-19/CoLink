package com.seven.colink.ui.evaluation

import android.content.Intent
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
import com.seven.colink.databinding.FragmentEvaluationProjectBinding
import com.seven.colink.ui.home.HomeFragment
import com.seven.colink.ui.search.SearchFragment
import com.seven.colink.util.dialog.setDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EvaluationProjectFragment : Fragment() {
    private var _binding: FragmentEvaluationProjectBinding? = null
    private val binding get() = _binding!!

    private var projectUserList: Int = 0

    private val evaluationViewModel: EvaluationViewModel by activityViewModels()

    private lateinit var viewPager: ViewPager2
    private var currentPage: Int = 0

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
            projectUserList = it.getInt("projectUserList")
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

    private fun initView() {
        setViewPager()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                evaluationViewModel.updateProjectMembers(
                    projectUserList,
                    binding.rbEvalQuestion1.rating,
                    binding.rbEvalQuestion2.rating,
                    binding.rbEvalQuestion3.rating,
                    binding.rbEvalQuestion4.rating,
                    binding.rbEvalQuestion5.rating
                )
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
        evalProjectMembersData.observe(viewLifecycleOwner) {
            setView(it?.get(projectUserList))
            firstPage()
            lastPage()
        }
    }

    private fun firstPage() {
        val firstPage = projectUserList == 0
        if (firstPage) {
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
        val lastPage =
            projectUserList + 1 == evaluationViewModel.evalProjectMembersData.value?.size
        if (lastPage) {
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
                        evaluationViewModel.updateProjectMembers(
                            projectUserList,
                            binding.rbEvalQuestion1.rating,
                            binding.rbEvalQuestion2.rating,
                            binding.rbEvalQuestion3.rating,
                            binding.rbEvalQuestion4.rating,
                            binding.rbEvalQuestion5.rating
                        )
                        for (i in 0..projectUserList){
                            evaluationViewModel.updateProjectUserGrade(i)
                        }
                        it.dismiss()
                        activity?.finish()
                    },
                    cancelAction = { it.dismiss() }
                ).show()
            }
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