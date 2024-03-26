package com.seven.colink.ui.userdetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.seven.colink.databinding.ActivityUserDetailBinding
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.userdetail.adapter.UserDetailPostAdapter
import com.seven.colink.ui.userdetail.adapter.UserSkillAdapter
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.ui.userdetailshowmore.UserDetailShowmoreActivity
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUserDetailBinding
    private lateinit var adapter: UserSkillAdapter
    private lateinit var postadapter: UserDetailPostAdapter

    private val viewModel: UserDetailViewModel by viewModels()

    companion object {
        const val EXTRA_USER_KEY = "extra_user_key"

        fun newIntent(
            context: Context,
            userId: String,
        ) = Intent(
            context, UserDetailActivity()::class.java
        ).apply {
            putExtra(EXTRA_USER_KEY, userId)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userSkill()
        PostRecyclerView()
        initViewModel()

        binding.tvUserdetailShowmore.setOnClickListener {
            viewModel.detailEvent()

        }

        postadapter.postClick = object : UserDetailPostAdapter.PostClick {
            override fun onClick(view: View, position: Int, item: UserPostItem.UserDetailPostItem) {
                lifecycleScope.launch {
                    var key = item.userprojectKey
                    val post = key.let { viewModel.getPost(it)}
                    if (post != null){
                        startActivity(
                            PostActivity.newIntent(
                                context = this@UserDetailActivity,
                                key = key
                            )
                        )
                    } else {
                        Toast.makeText(this@UserDetailActivity, "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        postadapter.studyClick = object : UserDetailPostAdapter.StudyClick {
            override fun onClick(view: View, position: Int, item: UserPostItem.UserDetailStudyItem) {
                lifecycleScope.launch{
                    var key = item.userstudykey
                    val post = key.let { viewModel.getPost(it) }
                    if (post != null ){
                        startActivity(
                            PostActivity.newIntent(
                                context = this@UserDetailActivity,
                                key = key
                            )
                        )
                    } else {
                        Toast.makeText(this@UserDetailActivity, "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

        binding.ivUserdetailBackArrow.setOnClickListener {
            finish()
        }



        viewModel.userDetails.observe(this@UserDetailActivity) { userDetails ->
            updateUI(userDetails)
            adapter.changeDataset(userDetails.userSkill?.map { UserSkillItem(it,UserSkillItemManager.addItem(it)) }?: emptyList())
        }

        viewModel.userDetailPost.observe(this@UserDetailActivity) { it ->
            it?.map{post ->
                if (post.grouptype == GroupType.PROJECT){
                    UserPostItem.UserDetailPostItem(if (post.ing != ProjectStatus.END){
                        "참여중"
                    } else "완료", userprojectName = post.title.toString(), userprojectTime = post.time.toString(), userprojectKey = post.key.toString())
                } else {
                    UserPostItem.UserDetailStudyItem(if(post.ing != ProjectStatus.END){
                        "참여중"
                    } else "완료",  post.title.toString(), post.time.toString(), post.key.toString()
                    )

                }}?.let { it1 -> postadapter.changeDataset(it1) }
            Log.e("Tag","${it}")
        }

        lifecycleScope.launch {
            viewModel.detailEvent.collect{
                startActivity(UserDetailShowmoreActivity.newIntent(
                this@UserDetailActivity,
                        it
            ))
            }
        }
    }

    private fun updateUI(user: UserDetailModel){
        if (user.userLink == null){
            binding.ivUserdetailLink.visibility = View.GONE
        } else {
            binding.ivUserdetailLink.visibility = View.VISIBLE
            binding.ivUserdetailLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.userLink))
                startActivity(intent)
            }
        }

        binding.btnUserdetailChat.setOnClickListener {
            viewModel.registerChatRoom()
        }

        binding.btnUserdetailGroup.setOnClickListener {
            viewModel.setPostList()
        }

        binding.tvUserdetailName.text = user.userName

        if (user.userInfo != null){
            binding.tvUserdetailAboutMe.text = user.userInfo
        } else {
            binding.tvUserdetailAboutMe.text = "자기소개가 없습니다."
        }

        if (user.userMainSpecialty != null) {
            binding.tvUserdetailSpecialization.text = user.userMainSpecialty
        } else{
            binding.tvUserdetailSpecialization.text = "없음"
        }

        binding.tvUserdetailScore.text = user.userscore.toString()

        //블로그
        if(user.userBlog != null){
            binding.ivUserdetailBlog.visibility = View.VISIBLE
            binding.ivUserdetailBlog.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.userBlog))
                startActivity(intent)
            }
        } else binding.ivUserdetailBlog.visibility = View.GONE

        //깃
        if (user.userGit != null){
            binding.ivUserdetailGit.visibility = View.VISIBLE
            binding.ivUserdetailGit.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.userGit))
                startActivity(intent)
            }
        } else binding.ivUserdetailGit.visibility = View.GONE

        if (user.userProfile == null){
            binding.ivUserdetailProfile
        } else {
            binding.ivUserdetailProfile.load(user.userProfile)
            binding.ivUserdetailProfile.clipToOutline = true
        }
        user.userLevel?.let { binding.ivUserdetailLevel.setLevelIcon(it) }
        binding.tvUserdetailLevel.text = user.userLevel.toString()
    }


    private fun userSkill(){
        adapter = UserSkillAdapter(UserSkillItemManager.getItem())
        binding.reUserdetailItem.adapter = adapter
        binding.reUserdetailItem.layoutManager = GridLayoutManager(this, 4)
    }

    private fun PostRecyclerView(){
        postadapter = UserDetailPostAdapter(UserDetailPostItemManager.getItemAll())
        binding.reUserdetailProject.adapter = postadapter
        binding.reUserdetailProject.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            chatRoom.collect {
                startActivity(ChatRoomActivity.newIntent(this@UserDetailActivity,it.key))
            }
        }

        lifecycleScope.launch {
            currentUserPostList.collect{ list ->
                if (list.isEmpty()) binding.root.setSnackBar(SnackType.Error, "초대 가능한 그룹이 없습니다").show()
                else {
                    list.mapNotNull { it.title }.setDialog(this@UserDetailActivity, "그룹을 선택 해주세요") { title ->
                        list.find { it.title == title }.let { post -> viewModel.inviteGroup(post!!) }
                    }
                }
            }
        }
    }
}
