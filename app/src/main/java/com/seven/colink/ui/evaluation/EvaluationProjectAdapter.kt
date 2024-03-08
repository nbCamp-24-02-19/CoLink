package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EvaluationProjectAdapter(
    frag: FragmentActivity,
    val mItems: MutableList<EvaluationData.EvalProject>
) : FragmentStateAdapter(frag) {
    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("Evaluation", "EvalAdapter <createFragment_Project>")
        val user = mItems.filterIsInstance<EvaluationData.EvalProject>()

        return if (position in user.indices){
            EvaluationProjectFragment.newInstanceProject(user[position])
        }
        else throw IllegalArgumentException("Invalid position $position")
    }
}