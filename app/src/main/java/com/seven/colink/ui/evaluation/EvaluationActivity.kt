package com.seven.colink.ui.evaluation

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.R
import com.seven.colink.databinding.ActivityEvaluationBinding
import com.seven.colink.util.Constants
import com.seven.colink.util.status.GroupType

class EvaluationActivity : AppCompatActivity() {
    companion object {
        fun newIntentEval(
            context: Context,
            groupType: GroupType,
            entityKey: String
        ) = Intent(context, EvaluationActivity::class.java).apply {
            putExtra(Constants.EXTRA_GROUP_TYPE, groupType)
            putExtra("extra_group_entity", entityKey)
        }
    }

    private val evalAdapter by lazy {
        EvaluationAdapter(
            this,
            onClickButton = { _, item -> handleItemClick(item) }
        )
    }

    private val binding by lazy {
        ActivityEvaluationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {
        binding.vpEvalViewpager.adapter = evalAdapter
        binding.vpEvalViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val pageCount = 5
        binding.pbEvalProgress.max = pageCount - 1

        binding.vpEvalViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.pbEvalProgress.progress = position
            }
        })
    }

    private fun handleItemClick(item: EvaluationData) {
        when (item) {
            is EvaluationData.EvalProject -> {}
            is EvaluationData.EvalStudy -> {}
            else -> throw UnsupportedOperationException("Unhandled type : $item")
        }
    }

    private fun setFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.vp_eval_viewpager, frag)
            .commit()
    }
}
