/*
package com.example.colink.util.dialog

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class DialogAdapter(
    private val onRepliesClick: (parentId: String?) -> Unit,
) : ListAdapter<String> (

    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            TODO("Not yet implemented")
        }

    }
) {

}
*/
