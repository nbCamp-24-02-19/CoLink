package com.seven.colink.ui.mypage.showmore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.ActivityMyPageLikeShowMoreBinding
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.search.SearchModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageLikeShowMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageLikeShowMoreBinding
    private lateinit var likeMoreAdapter: MyPageLikeShowMoreAdapter
    private val viewModel: MyPageLikeShowMoreViewModel by viewModels()

    private var wholeLikeList = mutableListOf<SearchModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyPageLikeShowMoreBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()

        // 아이템 클릭 시, 포스트 페이지로 이동
        likeMoreAdapter.itemClick = object : MyPageLikeShowMoreAdapter.ItemClick {
            override fun onClick(item: SearchModel, position: Int) {
                lifecycleScope.launch {
                    val key = item.key
                    val likePost = key.let { viewModel.getPost(it) }
                    if (likePost != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = this@MyPageLikeShowMoreActivity,
                                key = key
                            )
                        )
                    } else {
                        Toast.makeText(
                            this@MyPageLikeShowMoreActivity,
                            "다음에 다시 시도해 주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun initView() {
        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }

        likeMoreAdapter = MyPageLikeShowMoreAdapter(wholeLikeList)
        binding.rvLikeShowMore.adapter = likeMoreAdapter
        binding.rvLikeShowMore.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvLikeShowMore.itemAnimator = null
    }

    private fun initViewModel() = with(viewModel) {
        likeModel.observe(this@MyPageLikeShowMoreActivity) {
            likeMoreAdapter.mItems.clear()
            likeMoreAdapter.mItems.addAll(it)
            likeMoreAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadLikePost()
    }
}