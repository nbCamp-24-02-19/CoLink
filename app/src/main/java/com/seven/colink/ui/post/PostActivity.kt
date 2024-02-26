package com.seven.colink.ui.post

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.seven.colink.R
import com.seven.colink.databinding.ActivityPostBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.util.Constants.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_GROUP_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_POSITION_ENTITY
import com.seven.colink.util.Constants.Companion.EXTRA_POST_ENTITY
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    companion object {
        fun newIntentForCreate(
            context: Context,
            groupType: GroupType
        ) = Intent(context, PostActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, PostEntryType.CREATE)
            putExtra(EXTRA_GROUP_TYPE, groupType)
        }

        fun newIntentForUpdate(
            context: Context,
            groupType: GroupType,
            position: Int,
            entity: PostEntity
        ) = Intent(context, PostActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, PostEntryType.UPDATE)
            putExtra(EXTRA_GROUP_TYPE, groupType)
            putExtra(EXTRA_POSITION_ENTITY, position)
            putExtra(EXTRA_POST_ENTITY, entity)
        }
    }


    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        tvLimitedPeople.text = getString(R.string.limited_people, LIMITED_PEOPLE)
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(this@PostActivity) { state ->
            with(binding) {
                etContent.hint = state.editTextContent?.let { getString(it) }

                state.isProjectSelected?.let {
                    setTextViewProperties(
                        btProject,
                        it,
                        state.projectButtonTextColor
                    )
                }
                state.isProjectSelected?.let {
                    setTextViewProperties(
                        btStudy,
                        it.not(),
                        state.studyButtonTextColor
                    )
                }
            }
        }
    }

    private fun setTextViewProperties(textView: TextView, isSelected: Boolean, textColorRes: Int?) {
        textView.isSelected = isSelected
        textColorRes?.let { textView.setTextColor(ContextCompat.getColor(this@PostActivity, it)) }
    }


}