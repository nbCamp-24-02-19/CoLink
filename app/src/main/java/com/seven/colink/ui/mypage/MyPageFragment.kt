package com.seven.colink.ui.mypage

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageBinding
import com.seven.colink.databinding.ItemSignUpSkillBinding
import com.seven.colink.databinding.MypageEditDialogBinding
import com.seven.colink.databinding.UtilCustomBasicDialogBinding
import com.seven.colink.ui.mypage.MyPageItem.skilItems
import com.seven.colink.ui.mypage.adapter.MyPagePostAdapter
import com.seven.colink.ui.mypage.adapter.MyPageSkilAdapter
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.skillCategory
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import com.seven.colink.util.status.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import okhttp3.internal.notifyAll

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var _binding: MypageEditDialogBinding
    private lateinit var skiladapter: MyPageSkilAdapter
    private lateinit var postadapter: MyPagePostAdapter



    var imageUri: Uri? = null


    companion object {
        fun newInstance() = MyPageFragment()
    }

    private val viewModel: MyPageViewModel by viewModels()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)
        _binding = MypageEditDialogBinding.inflate(layoutInflater)


        showProgressOverlay()
        binding.ntMypage.visibility = View.GONE
        privacypolicy()
        SkilRecyclerView()
        PostRecyclerView()
        setLogout()

        //스킬 추가
        skiladapter.plusClick = object : MyPageSkilAdapter.PlusClick{
            override fun onClick(item: MyPageItem, position: Int) {

                val binding_ = ItemSignUpSkillBinding.inflate(layoutInflater)
                skillCategory.setDialog(binding_.root.context, "사용 가능한 언어/툴을 선택해주세요"){
                    binding_.btSignUpSubCategoryBtn.text = it
                    viewModel.updateSkill(it)
                    Log.d("tag", "skill = $it")
                }.show()
            }

        }
        //스킬 삭제
        skiladapter.skilLongClick = object : MyPageSkilAdapter.SkilLongClick{
            override fun onLongClick(language: String, position: Int) {
                context?.setDialog("삭제",
                    "정말로 삭제하시겠습니까?",
                    null,
                    confirmAction = {
                        viewModel.removeSkill(language)
                        it.dismiss()
                    },
                    cancelAction = {
                        it.dismiss()
                    })?.show()
            }
        }

        postadapter.postClick = object :MyPagePostAdapter.PostClick{
            override fun onClick(view: View, position: Int, item: MyPostItem.MyPagePostItem) {
                lifecycleScope.launch {
                    var key = item.projectKey
                    Log.d("postClick","key = $key")
                    val post = key.let { viewModel.getPost(it) }
                    Log.d("postClick","post = $post")
                    if (post != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = requireActivity(),
                                key = key
                            )
                        )
                    }else {
                        Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        postadapter.studyClick = object : MyPagePostAdapter.StudyClick{
            override fun onClick(view: View, position: Int, item: MyPostItem.MyPageStudyItem) {
                lifecycleScope.launch {
                    var key = item.studyKey
                    Log.d("postClick","key = ${key}")
                    val post = key?.let { viewModel.getPost(it) }
                    Log.d("postClick","post = ${post}")
                    if (post != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = requireActivity(),
                                key = key
                            )
                        )
                    }else {
                        Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        //파이어베이스 유저 정보 연결 & 스킬 연결
        viewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            if (userDetails!= null) {
                // Update UI with user details
                updateUI(userDetails)
                skiladapter.changeDataset(userDetails.skill?.map {
                    skilItems(
                        skillCategory.indexOf(
                            it
                        ), it, MyPageSkilItemManager.addItem(it)
                    )
                }
                    ?.plus(MyPageItem.plusItems(99, R.drawable.ic_add_24)) ?: emptyList())
            }else{
                startActivity(Intent(requireContext(), SignInActivity::class.java))
            }
            binding.ntMypage.visibility = View.VISIBLE
            hideProgressOverlay()
        }

        //파이어베이스 유저 등록글
        viewModel.userPost.observe(viewLifecycleOwner) {
            it?.map{post ->
                if (post.grouptype == GroupType.PROJECT){
                    MyPostItem.MyPagePostItem(if (post.ing != ProjectStatus.END){
                        "참여중"
                    } else "완료", projectName = post.title.toString(), projectTime = post.time.toString(), projectKey = post.key.toString())
                } else {
                    MyPostItem.MyPageStudyItem(if(post.ing != ProjectStatus.END){
                        "참여중"
                    } else "완료",  post.title.toString(), post.time.toString(), post.key.toString()
                    )

                }}?.let { it1 -> postadapter.changeDataset(it1) }
            Log.e("Tag","${it}")
            }

        return binding.root
    }



    private fun updateUI(user: MyPageUserModel) {
        // Update your views with user information
        //이름을 안 적고 넘어갈 수 있나..........?
//        if (user.name != null){
            binding.tvMypageName.text = user.name
//        } else {
//            binding.tvMypageName.text = user.email
//        }
        //링크가 있으면 버튼 활성화 없으면 사라짐
        if (user.link == null){
            binding.ivMypageLink.visibility = View.GONE
        } else {
            binding.ivMypageLink.visibility = View.VISIBLE
            binding.ivMypageLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.link))
                startActivity(intent)
            }
        }

        //전문분야가 없으면
        if (user.specialty != null){
            binding.tvMypageSpecialization2.text = user.mainSpecialty
        } else {
            binding.tvMypageSpecialization2.text = "없음"
        }

        //블로그 주소가 없으면
        binding.ivMypageBlog.setOnClickListener {
            if (user.blog != null){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.blog))
                startActivity(intent)
            } else{
                Toast.makeText(context,"블로그 주소가 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }

        //깃헙 주소가 없으면
        binding.ivMypageGit.setOnClickListener {
            if (user.git != null){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.git))
                startActivity(intent)
            } else{
                Toast.makeText(context,"깃허브 주소가 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }


        if(user.info != null){
            binding.tvMypageAboutMe.text = user.info
        } else {
            binding.tvMypageAboutMe.text = "자기소개가 없습니다."
        }

//        프로필이 없으면
        if(user.profile == null){
            binding.ivMypageProfile
        } else{
            binding.ivMypageProfile.load(user.profile)
        }

        val level = user.level
        val levelicon: Drawable = DrawableCompat.wrap(binding.ivMypageLevel.drawable)
        if (level == 1){
            binding.tvMypageLevel.text = "1"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(requireContext(),R.color.level1)
            )
        } else if(level == 2){
            binding.tvMypageLevel.text = "2"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(requireContext(),R.color.level2)
            )
        }
        else if(level == 3){
            binding.tvMypageLevel.text = "3"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(requireContext(),R.color.level3)
            )
        }
        else if(level == 4){
            binding.tvMypageLevel.text = "4"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(requireContext(),R.color.level4)
            )
        }
        else if(level == 5){
            binding.tvMypageLevel.text = "5"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(requireContext(),R.color.level5)
            )
        }
        else if(level == 6){
            binding.tvMypageLevel.text = "6"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(requireContext(),R.color.level6)
            )
        } else{
            binding.tvMypageLevel.text = "7"
            DrawableCompat.setTint(
                levelicon.mutate(),
                ContextCompat.getColor(requireContext(),R.color.level7)
            )
        }
        binding.tvMypageScore.text = user.score.toString()

        Log.d("Tag","user = ${user}")
    }


    private fun privacypolicy(){
        binding.ctMypage2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://guri999.github.io/"))
            startActivity(intent)
        }
    }




//    private fun SkilRecyclerView(){
//        skiladapter = MyPageSkilAdapter(MyPageSkilItemManager.getAllItem())
//        binding.reMypageItem.adapter = skiladapter
//        binding.reMypageItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//    }
    private fun SkilRecyclerView(){
        skiladapter = MyPageSkilAdapter(MyPageSkilItemManager.getAllItem())
        binding.reMypageItem.adapter = skiladapter
        binding.reMypageItem.layoutManager = GridLayoutManager(context, 4)
    }


    private fun PostRecyclerView(){
        postadapter = MyPagePostAdapter(MyPagePostItemManager.getItemAll())
        binding.reMypageProject.adapter = postadapter
        binding.reMypageProject.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun setLogout() = with(viewModel){
        binding.tvLogout.setOnClickListener {
            startActivity(Intent(requireContext(),SignInActivity::class.java))
            logout()
        }
    }
}
