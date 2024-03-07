package com.seven.colink.ui.evaluation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.databinding.ActivityEvaluationBinding
import com.seven.colink.util.Constants
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EvaluationActivity : AppCompatActivity() {
    companion object {
        fun newIntentEval(
            context: Context,
            groupType: GroupType,
            entityKey: String
        ) = Intent(context, EvaluationActivity::class.java).apply {
            putExtra(Constants.EXTRA_GROUP_TYPE, groupType.ordinal)
            putExtra("extra_group_entity", entityKey)
        }
    }

    private var userList = mutableListOf<EvaluationData>()
    private lateinit var evalAdapter: EvaluationProjectAdapter
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
        evalViewModel = ViewModelProvider(this).get(EvaluationViewModel::class.java)

        initView()
        evalViewModel.getMembers(groupEntity)
        setObserve()

    }

    private fun initView() {

        when(groupTypeEntity){
            0 -> {
                evalAdapter = EvaluationProjectAdapter(this, userList)
                binding.vpEvalViewpager.adapter = evalAdapter
                binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
            1 -> {
                evalStudyAdapter = EvaluationStudyAdapter(this, userList)
                binding.vpEvalViewpager.adapter = evalStudyAdapter
                binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
            else -> throw IllegalArgumentException("Unknown GroupTypeEntity!")
        }
//        evalAdapter = EvaluationAdapter(this, userList)
//        binding.vpEvalViewpager.adapter = evalAdapter
//        binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val pageCount = 3
        binding.pbEvalProgress.max = pageCount - 1

        binding.vpEvalViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.pbEvalProgress.progress = position
            }
        })

        Log.d("Evaluation", "evaluationValue = ${groupTypeEntity}, ${groupEntity}")
    }

    private fun setObserve() {
        evalViewModel.evalMembersData.observe(this) {


            Log.d("Evaluation", "evalViewModel Observing!")
            Log.d("Evaluation", "progressbar.size = ${binding.pbEvalProgress.max}")
        }
        Log.d("Evaluation", "setObserve")
    }
}
