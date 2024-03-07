package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EvaluationStudyAdapter(
    frag: FragmentActivity,
    private val mItems: MutableList<EvaluationData>
) : FragmentStateAdapter(frag) {
    override fun getItemCount(): Int {
//        return mItems.size
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("Evaluation", "EvalAdapter <createFragment_Study>")
        return when (position) {
            0 -> {
                EvaluationStudyFragment.newInstanceStudy(
                    mItems.filterIsInstance<EvaluationData.EvalStudy>()
                )
            }
            1 -> {
                EvaluationStudyFragment.newInstanceStudy(
                    mItems.filterIsInstance<EvaluationData.EvalStudy>()
                )
            }
            2 -> {
                EvaluationStudyFragment.newInstanceStudy(
                    mItems.filterIsInstance<EvaluationData.EvalStudy>()
                )
            }
            3 -> {
                EvaluationStudyFragment.newInstanceStudy(
                    mItems.filterIsInstance<EvaluationData.EvalStudy>()
                )
            }
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }
}