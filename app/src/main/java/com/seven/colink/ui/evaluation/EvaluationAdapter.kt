package com.seven.colink.ui.evaluation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.FragmentEvaluationProjectBinding
import com.seven.colink.databinding.FragmentEvaluationStudyBinding
import com.seven.colink.databinding.ItemSearchPostBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.GroupViewType

class EvaluationAdapter(
    private val context: Context,
    private val onClickButton: (Int, EvaluationData) -> Unit,
) : ListAdapter<EvaluationData, EvaluationAdapter.EvalViewHolder>(
    object : DiffUtil.ItemCallback<EvaluationData>() {

        override fun areItemsTheSame(oldItem: EvaluationData, newItem: EvaluationData): Boolean =
            when {
                oldItem is EvaluationData.EvalProject && newItem is EvaluationData.EvalProject -> {
                    oldItem.name == newItem.name
                }

                oldItem is EvaluationData.EvalStudy && newItem is EvaluationData.EvalStudy -> {
                    oldItem.name == newItem.name
                }

                else -> oldItem == newItem
            }


        override fun areContentsTheSame(
            oldItem: EvaluationData,
            newItem: EvaluationData
        ): Boolean = oldItem == newItem
    }
) {

    abstract class EvalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: EvaluationData)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is EvaluationData.EvalProject -> GroupType.PROJECT
        is EvaluationData.EvalStudy -> GroupType.STUDY
        else -> GroupType.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvalViewHolder =
        when (GroupType.from(viewType)) {
            GroupType.PROJECT -> EvalProjectViewHolder(
                context,
                FragmentEvaluationProjectBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            GroupType.STUDY -> EvalStudyViewHolder(
                context,
                FragmentEvaluationStudyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> EvalUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    override fun onBindViewHolder(holder: EvalViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class EvalProjectViewHolder(
        context: Context,
        private val binding: FragmentEvaluationProjectBinding
    ) : EvalViewHolder(binding.root) {
        override fun onBind(item: EvaluationData) {
            if (item is EvaluationData.EvalProject) {
                binding.tvEvalTitle.text = item.name
            }
        }
    }

    class EvalStudyViewHolder(
        context: Context,
        private val binding: FragmentEvaluationStudyBinding
    ) : EvalViewHolder(binding.root) {
        override fun onBind(item: EvaluationData) {
            if (item is EvaluationData.EvalStudy) {
                binding.tvEvalTitle.text = item.name
            }
        }
    }

    class EvalUnknownViewHolder(binding: ItemUnknownBinding) :
        EvalViewHolder(binding.root) {
        override fun onBind(item: EvaluationData) = Unit
    }
}