package com.seven.colink.ui.evaluation

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.databinding.ActivityEvaluationBinding
import com.seven.colink.util.Constants
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EvaluationActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_GROUP_ENTITY = "extra_group_entity"

        fun newIntentEval(
            context: Context,
            groupType: GroupType,
            entityKey: String
        ) = Intent(context, EvaluationActivity::class.java).apply {
            putExtra(Constants.EXTRA_GROUP_TYPE, groupType.ordinal)
            putExtra(EXTRA_GROUP_ENTITY, entityKey)
        }
    }

    private var projectUserList = mutableListOf<EvaluationData.EvalProject>()
    private var studyUserList = mutableListOf<EvaluationData.EvalStudy>()
    private lateinit var evalProjectAdapter: EvaluationProjectAdapter
    private lateinit var evalStudyAdapter: EvaluationStudyAdapter
    private lateinit var evalViewModel: EvaluationViewModel

    private val binding by lazy {
        ActivityEvaluationBinding.inflate(layoutInflater)
    }
    private val groupTypeEntity by lazy {
        intent.getSerializableExtra(Constants.EXTRA_GROUP_TYPE)
    }
    private val groupEntity by lazy {
        intent.getStringExtra("extra_group_entity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        evalViewModel = ViewModelProvider(this)[EvaluationViewModel::class.java]

        initView()

    }

    private fun initView() {
        when (groupTypeEntity) {
            0 -> {
                evalViewModel.getProjectMembers(groupEntity)
                evalProjectAdapter = EvaluationProjectAdapter(this, projectUserList)
                binding.vpEvalViewpager.adapter = evalProjectAdapter
                binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                setProjectObserve()
                setProgress()
            }
            1 -> {
                evalViewModel.getStudyMembers(groupEntity)
                evalStudyAdapter = EvaluationStudyAdapter(this, studyUserList)
                binding.vpEvalViewpager.adapter = evalStudyAdapter
                binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                setStudyObserve()
                setProgress()
            }
            else -> throw IllegalArgumentException("Unknown GroupTypeEntity!")
        }

        Log.d("Evaluation", "evaluationValue = ${groupTypeEntity}, $groupEntity")
    }


    private fun setProgress(){
        binding.vpEvalViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.pbEvalProgress.progress = position
            }
        })
    }

    private fun setProjectObserve() {
        evalViewModel.evalProjectMembersData.observe(this) {
            it?.let { list ->
                val nonNullList = list.filterNotNull()
                evalProjectAdapter.mItems.clear()
                evalProjectAdapter.mItems.addAll(nonNullList)
                val pageCount = nonNullList.size
                binding.pbEvalProgress.max = pageCount - 1
                evalProjectAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setStudyObserve() {
        evalViewModel.evalStudyMembersData.observe(this) { it ->
            it?.let { list ->
                val nonNullList = list.filterNotNull()
                evalStudyAdapter.mItems.clear()
                evalStudyAdapter.mItems.addAll(nonNullList)
                val pageCount = nonNullList.size
                binding.pbEvalProgress.max = pageCount - 1
                evalStudyAdapter.notifyDataSetChanged()
            }
        }
    }
}
