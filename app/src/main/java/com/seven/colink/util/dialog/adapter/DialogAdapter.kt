package com.seven.colink.util.dialog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemListStringBinding

class DialogAdapter(
    private val onClick: (id: String) -> Unit,
    private val dialog: AlertDialog
) : ListAdapter<String, DialogAdapter.DialogViewHolder> (

    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem == newItem

    }
) {
    abstract class DialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: String)
    }

    override fun onViewRecycled(holder: DialogViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder =
        DialogItemViewHolder(
            binding = ItemListStringBinding.inflate(LayoutInflater.from(parent.context),parent,false),
            onClick = onClick,
            dialog = dialog
        )

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class DialogItemViewHolder(
        private val binding: ItemListStringBinding,
        private val onClick: (id: String) -> Unit,
        private val dialog: AlertDialog
    ): DialogViewHolder(binding.root) {
        override fun onBind(item: String) = with(binding) {
            tvPersonalHeader.text = item
            tvPersonalHeader.setOnClickListener {
                onClick(item)
                dialog.dismiss()
            }
        }

    }
}
