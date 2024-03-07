package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EvaluationProjectAdapter(
    frag: FragmentActivity,
    private val mItems: MutableList<EvaluationData>
) : FragmentStateAdapter(frag) {
    override fun getItemCount(): Int {
//        return mItems.size
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("Evaluation", "EvalAdapter <createFragment_Project>")
        return when (position) {
            0 -> {
                EvaluationProjectFragment.newInstanceProject(
                    mItems.filterIsInstance<EvaluationData.EvalProject>()
                )
            }
            1 -> {
                EvaluationProjectFragment.newInstanceProject(
                    mItems.filterIsInstance<EvaluationData.EvalProject>()
                )
            }
            2 -> {
                EvaluationProjectFragment.newInstanceProject(
                    mItems.filterIsInstance<EvaluationData.EvalProject>()
                )
            }
            3 -> {
                EvaluationProjectFragment.newInstanceProject(
                    mItems.filterIsInstance<EvaluationData.EvalProject>()
                )
            }
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }
}