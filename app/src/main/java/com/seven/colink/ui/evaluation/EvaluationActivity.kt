package com.seven.colink.ui.evaluation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.databinding.ActivityEvaluationBinding
import com.seven.colink.util.Constants
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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
    private var projectDid : Boolean = false
    private var studyDid : Boolean = false

    private val binding by lazy {
        ActivityEvaluationBinding.inflate(layoutInflater)
    }
    private val groupTypeEntity by lazy {
        GroupType.from(intent.getIntExtra(Constants.EXTRA_GROUP_TYPE, 0))
    }
    private val groupEntity by lazy {
        intent.getStringExtra("extra_group_entity")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        evalViewModel = ViewModelProvider(this)[EvaluationViewModel::class.java]

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(evalViewModel) {
        lifecycleScope.launch {
            result.collect {
                if (it == DataResultStatus.SUCCESS) finish()
                else binding.root.setSnackBar(SnackType.Error, it.message).show()
            }
        }
    }

    private fun initView() {
        when (groupTypeEntity) {
            GroupType.PROJECT -> {
                setProjectObserve()
            }

            GroupType.STUDY -> {
                setStudyObserve()
            }

            else -> throw IllegalArgumentException("Unknown GroupTypeEntity!")
        }
        Log.d("Evaluation", "evaluationValue = ${groupTypeEntity}, $groupEntity")
    }

    private fun setProjectObserve() = with(evalViewModel) {
        lifecycleScope.launch {
            evalViewModel.evalProjectMembersData.observe(this@EvaluationActivity) {
                it?.let { list ->
                    if (!projectDid) {
                        evalProjectAdapter = EvaluationProjectAdapter(
                            this@EvaluationActivity,
                            list.filterNotNull()
                        )
                        binding.vpEvalViewpager.adapter = evalProjectAdapter
                        binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                        binding.dotsindicator.attachTo(binding.vpEvalViewpager)
                    }
                    projectDid = true
                }
            }
        }

        lifecycleScope.launch {
            combine(currentGroup, currentUid) { group, uid ->
                Pair(group, uid)
            }.collect { (group, uid) ->
                if (group.postKey != "" && uid != "") getProjectMembers(group, uid)
                else Unit
            }
        }
    }

    private fun setStudyObserve() = with(evalViewModel) {
        lifecycleScope.launch {
            evalViewModel.evalStudyMembersData.observe(this@EvaluationActivity) {
                it?.let { list ->
                    if (!studyDid) {
                        evalStudyAdapter = EvaluationStudyAdapter(
                            this@EvaluationActivity,
                            list.filterNotNull()
                        )
                        binding.vpEvalViewpager.adapter = evalStudyAdapter
                        binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                        binding.dotsindicator.attachTo(binding.vpEvalViewpager)
                    }
                    studyDid = true
                }
            }
        }

        lifecycleScope.launch {
            combine(currentGroup, currentUid) {group, uid ->
                Pair(group, uid)
            }.collect{ (group, uid) ->
                if (group.postKey != "" && uid != "") getStudyMembers(group, uid)
                else Unit
            }
        }
    }
}
