package com.seven.colink.ui.group.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.seven.colink.databinding.ActivityGroupBoardBinding

class GroupBoardActivity : AppCompatActivity() {
    private val binding: ActivityGroupBoardBinding by lazy {
        ActivityGroupBoardBinding.inflate(layoutInflater)
    }

    private val viewModel: GroupBoardViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {

    }

    private fun initViewModel() = with(viewModel) {

    }
}