package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EvaluationProjectAdapter(
    frag: FragmentActivity,
    val mItems: List<EvaluationData.EvalProject>
) : FragmentStateAdapter(frag) {
    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("createFragment", "$position")
        return if (position in mItems.indices){
            EvaluationProjectFragment.newInstanceProject(position)
        }
        else throw IllegalArgumentException("Invalid position $position")
    }
}