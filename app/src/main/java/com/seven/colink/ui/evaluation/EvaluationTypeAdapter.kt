package com.seven.colink.ui.evaluation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.FragmentEvaluationProjectBinding
import com.seven.colink.databinding.FragmentEvaluationStudyBinding

class EvaluationTypeAdapter(val mItems: MutableList<EvaluationData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PROJECT = 1
        private const val VIEW_TYPE_STUDY = 2
    }

    inner class ProjectViewHolder(binding: FragmentEvaluationProjectBinding) :
    RecyclerView.ViewHolder(binding.root){
        val profileImg = binding.ivEvalProfileImage
        val name = binding.tvEvalTitle
        val communication = binding.rbEvalQuestion1
        val technic = binding.rbEvalQuestion2
        val diligence = binding.rbEvalQuestion3
        val flexibility = binding.rbEvalQuestion4
        val creativity = binding.rbEvalQuestion5
    }

    inner class StudyViewHolder(binding: FragmentEvaluationStudyBinding) :
    RecyclerView.ViewHolder(binding.root){
        val profileImg = binding.ivEvalProfileImage
        val name = binding.tvEvalTitle
        val diligence = binding.rbEvalQuestion1
        val communication = binding.rbEvalQuestion2
        val flexibility = binding.rbEvalQuestion3
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if(viewType == VIEW_TYPE_PROJECT) {
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

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(mItems[position]){
            is EvaluationData.EvalProject -> VIEW_TYPE_PROJECT
            is EvaluationData.EvalStudy -> VIEW_TYPE_STUDY
            else -> VIEW_TYPE_PROJECT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = mItems[position]) {
            is EvaluationData.EvalProject -> {
                (holder as ProjectViewHolder).name.text = item.name
                holder.profileImg.load(item.photoUrl)
                holder.communication.rating = item.communication ?: 3.0f
                holder.technic.rating = item.technic ?: 3.0f
                holder.diligence.rating = item.diligence ?: 3.0f
                holder.flexibility.rating = item.flexibility ?: 3.0f
                holder.creativity.rating = item.creativity ?: 3.0f
            }
            is EvaluationData.EvalStudy -> {
                (holder as StudyViewHolder).name.text = item.name
                holder.profileImg.load(item.photoUrl)
                holder.diligence.rating = item.diligence ?: 3.0f
                holder.communication.rating = item.diligence ?: 3.0f
                holder.flexibility.rating = item.diligence ?: 3.0f
            }
        }
    }
}