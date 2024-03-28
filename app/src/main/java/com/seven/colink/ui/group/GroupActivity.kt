package com.seven.colink.ui.group

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seven.colink.R
import com.seven.colink.databinding.ActivityGroupBinding
import com.seven.colink.ui.group.board.board.GroupBoardFragment
import com.seven.colink.util.Constants
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupActivity : AppCompatActivity() {
    private val binding: ActivityGroupBinding by lazy {
        ActivityGroupBinding.inflate(layoutInflater)
    }

    companion object {
        fun newIntent(
            context: Context,
            groupType: GroupType? = null,
            entryType: PostEntryType = PostEntryType.CREATE,
            key: String? = null,
        ) = Intent(context, GroupActivity::class.java).apply {
            putExtra(Constants.EXTRA_ENTRY_TYPE, entryType)
            putExtra(Constants.EXTRA_GROUP_TYPE, groupType)
            putExtra(Constants.EXTRA_ENTITY_KEY, key)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() = with(binding){
        supportFragmentManager.beginTransaction().add(R.id.fg_activity_group, GroupBoardFragment()).commit()
    }
}