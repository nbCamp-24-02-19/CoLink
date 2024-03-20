package com.seven.colink.ui.userdetail

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ActivityUserDetailBinding
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.sign.signup.SignUpActivity
import com.seven.colink.ui.sign.signup.model.SignUpUserModel
import com.seven.colink.ui.sign.signup.type.SignUpEntryType
import com.seven.colink.ui.userdetail.adapter.UserDetailPostAdapter
import com.seven.colink.ui.userdetail.adapter.UserSkillAdapter
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
                    } else "완료", userprojectName = post.title.toString(), userprojectTime = post.time.toString())
                } else {
                    UserPostItem.UserDetailStudyItem(if(post.ing != ProjectStatus.END){
                        "참여중"
                    } else "완료",  post.title.toString(), post.time.toString()
                    )

                }}?.let { it1 -> postadapter.changeDataset(it1) }
            Log.e("Tag","${it}")
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

        binding.ivUserdetailBlog.setOnClickListener {
            if (user.userBlog != null){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.userBlog))
                startActivity(intent)
            } else {
                Toast.makeText(this, "블로그 주소가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.ivUserdetailGit.setOnClickListener {
            if (user.userGit != null){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.userGit))
                startActivity(intent)
            } else {
                Toast.makeText(this, "깃허브 주소가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (user.userProfile == null){
            binding.ivUserdetailProfile
        } else {
            binding.ivUserdetailProfile.load(user.userProfile)
            binding.ivUserdetailProfile.clipToOutline = true
        }

        val level = user.userLevel
        val levelicon: Drawable = DrawableCompat.wrap(binding.ivUserdetailLevel.drawable)
        if (level == 1){
            binding.tvUserdetailLevel.text = "1"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(this,R.color.level1)
            )
        } else if(level == 2){
            binding.tvUserdetailLevel.text ="2"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(this,R.color.level1)
            )
        } else if(level == 3){
            binding.tvUserdetailLevel.text ="3"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(this,R.color.level3)
            )
        } else if(level == 4){
            binding.tvUserdetailLevel.text ="4"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(this,R.color.level4)
            )
        } else if(level == 5){
            binding.tvUserdetailLevel.text ="5"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(this,R.color.level5)
            )
        } else if(level == 6){
            binding.tvUserdetailLevel.text ="6"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(this,R.color.level6)
            )
        } else {
            binding.tvUserdetailLevel.text ="7"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(this,R.color.level7)
            )
        }
        Log.d("Tag","user = ${user}")
    }

//    private fun userSkill(){

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

    //    }
//        binding.reUserdetailItem.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.reUserdetailItem.adapter = adapter
//        adapter = UserSkillAdapter(UserSkillItemManager.getItem())

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            chatRoom.collect {
                startActivity(ChatRoomActivity.newIntent(this@UserDetailActivity,it.key, it.title?: ""))
            }
        }

        lifecycleScope.launch {
            currentUserPostList.collect{ list ->
                if (list.isEmpty()) binding.root.setSnackBar(SnackType.Error, "초대 가능한 그룹이 없습니다")
                else {
                    list.mapNotNull { it.title }.setDialog(this@UserDetailActivity, "그룹을 선택 해주세요") { title ->
                        list.find { it.title == title }.let { post -> viewModel.inviteGroup(post!!) }
                    }
                }
            }
        }
    }
}
