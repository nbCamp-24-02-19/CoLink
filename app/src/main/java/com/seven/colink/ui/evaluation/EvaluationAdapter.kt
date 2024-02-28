package com.seven.colink.ui.evaluation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.FragmentEvaluationProjectBinding
import com.seven.colink.databinding.FragmentEvaluationStudyBinding
import com.seven.colink.databinding.ItemSearchPostBinding

class EvaluationAdapter(private val mItems: MutableList<EvaluationData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PROJECT = 1
        private const val VIEW_TYPE_STUDY = 2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PROJECT) {
            val binding = FragmentEvaluationProjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ProjectViewHolder(binding)
        } else {
            val binding = FragmentEvaluationStudyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            StudyViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = mItems[position]) {
            is EvaluationData.EvalProject -> {
                (holder as ProjectViewHolder).name.text = item.name
            }
            is EvaluationData.EvalStudy -> {
                (holder as StudyViewHolder).name.text = item.name
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItems[position]){
            is EvaluationData.EvalProject -> VIEW_TYPE_PROJECT
            is EvaluationData.EvalStudy -> VIEW_TYPE_STUDY
        }
    }

    override fun getItemCount(): Int = mItems.size

    inner class ProjectViewHolder(binding: FragmentEvaluationProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvEvalTitle
    }

    inner class StudyViewHolder(binding: FragmentEvaluationStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvEvalTitle
    }
}