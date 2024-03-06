package com.seven.colink.ui.post.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seven.colink.R
import com.seven.colink.databinding.ActivityPostBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.ui.group.register.GroupContentActivity
import com.seven.colink.ui.post.register.post.PostFragment
import com.seven.colink.ui.post.register.post.model.AddTagResult
import com.seven.colink.ui.post.register.post.model.DialogEvent
import com.seven.colink.util.Constants.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_GROUP_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_POST_ENTITY
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.dialog.RecruitDialog
import com.seven.colink.util.dialog.setUpGroupDialog
import com.seven.colink.util.showToast
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {

    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    companion object {
        fun newIntent(
            context: Context,
            groupType: GroupType? = null,
            entryType: PostEntryType = PostEntryType.CREATE,
            key: String? = null,
        ) = Intent(context, PostActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, entryType)
            putExtra(EXTRA_GROUP_TYPE, groupType)
            putExtra(EXTRA_POST_ENTITY, key)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding){
        supportFragmentManager.beginTransaction().add(R.id.fg_activity_post, PostFragment()).commit()
    }


}