package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EvaluationStudyAdapter(
    frag: FragmentActivity,
    val mItems: List<EvaluationData.EvalStudy>
) : FragmentStateAdapter(frag) {
    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position in mItems.indices) {
            EvaluationStudyFragment.newInstanceStudy(position)
        }
        else throw IllegalArgumentException("Invalid type of data at position $position")
    }
}
