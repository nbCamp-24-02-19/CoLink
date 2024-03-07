package com.seven.colink.ui.evaluation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.seven.colink.databinding.FragmentEvaluationStudyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EvaluationStudyFragment : Fragment() {
    private var _binding: FragmentEvaluationStudyBinding? = null
    private val binding get() = _binding!!
    private val evalStudyViewModel: EvaluationStudyViewModel by viewModels()
    private var studyUserList = mutableListOf<EvaluationData.EvalStudy>()


    companion object {
        fun newInstanceStudy(list: List<EvaluationData.EvalStudy>)
                : EvaluationStudyFragment {
            val fragment = EvaluationStudyFragment()
            val args = Bundle()
            args.putParcelableArrayList("studyUserList", ArrayList(list))
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEvaluationStudyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setObserve()
    }

    private fun initView(){
        Log.d("Evaluation","EvaluationStudyFragment")
        arguments?.let {
            val studyUserList: ArrayList<EvaluationData.EvalStudy> =
                it.getParcelableArrayList("studyUserList") ?: arrayListOf()
            Log.d("Evaluation", "studyUserlist = ${studyUserList}")
        }

    }

    private fun setObserve(){
        evalStudyViewModel.evalStudyData.observe(viewLifecycleOwner){

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}