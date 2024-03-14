package com.seven.colink.ui.showmore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.ActivityMyPageShowMoreBinding
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.showmore.adapter.MyPageShowMoreAdapter
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageShowMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageShowMoreBinding
    private lateinit var adapter: MyPageShowMoreAdapter

    private val viewModel: MyPageShowMoreViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyPageShowMoreBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivMypageShowMoreBack.setOnClickListener {
            finish()
        }

        PostRecyclerView()

        viewModel.myPost.observe(this@MyPageShowMoreActivity) { it ->
            it?.map{post ->
                if (post.grouptype == GroupType.PROJECT){
                    ShowMoreItem.ShowMoreProjectItem(
                        if (post.ing != ProjectStatus.END) {
                            "참여중"
                        } else "완료",
                        showmoreprojectTitle = post.title.toString(),
                        showmoreprojectTime = post.time.toString(),
                        projectKey = post.key.toString(),
                        showmoreprojectName = post.name.toString(),
                        showmoreprojectDescription = post.description.toString(),
                        showmoreprojectImage = post.image.toString(),
                        showmoreprojectViewCount = post.view!!.toInt()
                    )
                } else {
                    ShowMoreItem.ShowMoreStudyItem(
                        if (post.ing != ProjectStatus.END) {
                            "참여중"
                        } else "완료",
                        post.title.toString(),
                        post.time.toString(),
                        post.key.toString(),
                        post.name.toString(),
                        post.description.toString(),
                        post.view!!.toInt(),
                        post.image.toString()
                    )

                }}?.let { it1 -> adapter.changeDataset(it1) }
            Log.e("Tag","${it}")
        }
        adapter.postClick = object : MyPageShowMoreAdapter.PostClick{
            override fun onClick(
                view: View,
                position: Int,
                item: ShowMoreItem.ShowMoreProjectItem
            ) {
                lifecycleScope.launch {
                    var key =  item.projectKey
                    val post = key?.let { viewModel.getPost(it) }
                    if (post != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = this@MyPageShowMoreActivity,
                                key = key
                            )
                        )
                    } else {
                        Toast.makeText(this@MyPageShowMoreActivity,"다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        adapter.studyClick = object : MyPageShowMoreAdapter.StudyClick{
            override fun onClick(view: View, position: Int, item: ShowMoreItem.ShowMoreStudyItem) {
                lifecycleScope.launch{
                    var key = item.studyKey
                    val post = key?.let { viewModel.getPost(it) }
                    if (post != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = this@MyPageShowMoreActivity,
                                key = key
                            )
                        )
                    } else {
                        Toast.makeText(this@MyPageShowMoreActivity,"다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun PostRecyclerView(){
        adapter = MyPageShowMoreAdapter(MyPageShowMoreItemManager.getItemAll())
        binding.reMypageShowMore.adapter = adapter
        binding.reMypageShowMore.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}