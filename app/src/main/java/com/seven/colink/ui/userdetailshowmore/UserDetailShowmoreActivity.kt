package com.seven.colink.ui.userdetailshowmore

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.ActivityUserDetailShowmoreBinding
import com.seven.colink.ui.userdetailshowmore.adapter.UserDetailShowmoreAdapter
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailShowmoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailShowmoreBinding
    private lateinit var adapter: UserDetailShowmoreAdapter

    private val viewModel: UserDetailShowmoreViewModel by viewModels()

    companion object{
        const val EXTRA_USER_KEY2 = "extra_user_key2"
        fun newIntent(
            context: Context,
            userId: String,
        ) = Intent(
            context, UserDetailShowmoreActivity()::class.java
        ).apply {
            putExtra(EXTRA_USER_KEY2, userId)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserDetailShowmoreBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivUserShowMoreBack.setOnClickListener {
            finish()
        }

        PostRe()


        viewModel.userDetailShowmore.observe(this@UserDetailShowmoreActivity) {it ->
            it?.map {post ->
                if(post.grouptype == GroupType.PROJECT){
                    UserDetailShowmoreItem.ShowMoreProjectItem(
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
                    UserDetailShowmoreItem.ShowMoreStudyItem(
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
                }
            }?.let { it1 -> adapter.changeDataset(it1) }
        }



    }


    private fun PostRe(){
        adapter = UserDetailShowmoreAdapter(UserDetailShowmoreItemManager.getItemAll())
        binding.reUserShowMore.adapter = adapter
        binding.reUserShowMore.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}