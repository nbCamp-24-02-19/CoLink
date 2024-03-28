package com.seven.colink.ui.mypage

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageBinding
import com.seven.colink.databinding.ItemSignUpSkillBinding
import com.seven.colink.databinding.MypageEditDialogBinding
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.mypage.MyPageItem.skilItems
import com.seven.colink.ui.mypage.adapter.MyPageLikeAdapter
import com.seven.colink.ui.mypage.adapter.MyPagePostAdapter
import com.seven.colink.ui.mypage.adapter.MyPageSkilAdapter
import com.seven.colink.ui.mypage.showmore.MyPageLikeShowMoreActivity
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.showmore.MyPageShowMoreActivity
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.util.convert.convertError
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.skillCategory
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import com.seven.colink.util.status.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var _binding: MypageEditDialogBinding
    private lateinit var skiladapter: MyPageSkilAdapter
    private lateinit var postadapter: MyPagePostAdapter
    private lateinit var likeAdapter: MyPageLikeAdapter

    private var likeList = mutableListOf<MyPageLikeModel>()

    companion object {
        fun newInstance() = MyPageFragment()
    }

    private val viewModel: MyPageViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)
        _binding = MypageEditDialogBinding.inflate(layoutInflater)

        binding.ntMypage.visibility = View.GONE
        privacypolicy()
        SkilRecyclerView()
        PostRecyclerView()
        likeRecyclerView()
        setLogout()
        postShowMore()
        likeShowMore()
        inquiryOperator()
        initViewModel()



        //스킬 추가
        skiladapter.plusClick = object : MyPageSkilAdapter.PlusClick {
            override fun onClick(item: MyPageItem, position: Int) {

                val binding_ = ItemSignUpSkillBinding.inflate(layoutInflater)
                skillCategory.setDialog(binding_.root.context, "사용 가능한 언어/툴을 선택해주세요") {
                    binding_.btSignUpSubCategoryBtn.text = it
                    viewModel.updateSkill(it)
                    Log.d("tag", "skill = $it")
                }.show()
            }

        }
        //스킬 삭제
        skiladapter.skilLongClick = object : MyPageSkilAdapter.SkilLongClick {
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

        postadapter.postClick = object : MyPagePostAdapter.PostClick {
            override fun onClick(view: View, position: Int, item: MyPostItem.MyPagePostItem) {
                lifecycleScope.launch {
                    var key = item.projectKey
                    Log.d("postClick", "key = $key")
                    val post = key.let { viewModel.getPost(it) }
                    Log.d("postClick", "post = $post")
                    if (post != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = requireActivity(),
                                key = key
                            )
                        )
                    } else {
                        Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }

        postadapter.studyClick = object : MyPagePostAdapter.StudyClick {
            override fun onClick(view: View, position: Int, item: MyPostItem.MyPageStudyItem) {
                lifecycleScope.launch {
                    var key = item.studyKey
                    Log.d("postClick", "key = ${key}")
                    val post = key.let { viewModel.getPost(it) }
                    Log.d("postClick", "post = ${post}")
                    if (post != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = requireActivity(),
                                key = key
                            )
                        )
                    } else {
                        Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        likeAdapter.itemClick = object : MyPageLikeAdapter.ItemClick {
            override fun onClick(item: MyPageLikeModel, position: Int) {
                lifecycleScope.launch {
                    val key = item.key
                    val likePost = key.let { viewModel.getPost(it.toString()) }
                    if (likePost != null) {
                        startActivity(
                            PostActivity.newIntent(
                                context = requireActivity(),
                                key= key
                            )
                        )
                    } else {
                        Toast.makeText(requireContext(), "다음에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        //파이어베이스 유저 정보 연결 & 스킬 연결
        viewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            when (userDetails) {
                is UiState.Loading -> showProgressOverlay()
                is UiState.Success -> {
                    hideProgressOverlay()
                    // Update UI with user details
                    updateUI(userDetails.data)
                    skiladapter.changeDataset(userDetails.data.skill?.map {
                        skilItems(
                            skillCategory.indexOf(
                                it
                            ), it, MyPageSkilItemManager.addItem(it)
                        )
                    }
                        ?.plus(MyPageItem.plusItems(99, R.drawable.ic_add_24)) ?: emptyList())
                    binding.ntMypage.visibility = View.VISIBLE

                }

                is UiState.Error -> {
                    hideProgressOverlay()
                    if (userDetails.throwable.message == "No user") startActivity(Intent(requireContext(),SignInActivity::class.java))
                    Log.i("MyPage", "${userDetails.throwable}")
                }
            }
        }

        //파이어베이스 유저 등록글
        viewModel.userPost.observe(viewLifecycleOwner) {
            it?.map { post ->
                if (post.grouptype == GroupType.PROJECT) {
                    MyPostItem.MyPagePostItem(
                        if (post.ing != ProjectStatus.END) {
                            "참여중"
                        } else "완료",
                        projectName = post.title.toString(),
                        projectTime = post.time.toString(),
                        projectKey = post.key.toString()
                    )
                } else {
                    MyPostItem.MyPageStudyItem(
                        if (post.ing != ProjectStatus.END) {
                            "참여중"
                        } else "완료",
                        post.title.toString(),
                        post.time.toString(),
                        post.key.toString()
                    )

                }
            }?.let { it1 -> postadapter.changeDataset(it1) }
            Log.e("Tag", "${it}")
        }

        viewModel.likePost.observe(viewLifecycleOwner){
            if (it != null) {
                likeAdapter.mItems.clear()
                likeAdapter.mItems.addAll(it)
                likeAdapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            operatorChat.collect {
                startActivity(
                    ChatRoomActivity.newIntent(requireContext(), it)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserPost()
        viewModel.loadUserDetails()
        viewModel.loadLikePost()
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
        if (user.link == null) {
            binding.ivMypageLink.visibility = View.GONE
        } else {
            binding.ivMypageLink.visibility = View.VISIBLE
            binding.ivMypageLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.link))
                startActivity(intent)
            }
        }

        //전문분야가 없으면
        if (user.specialty != null) {
            binding.tvMypageSpecialization2.text = user.mainSpecialty
        } else {
            binding.tvMypageSpecialization2.text = "없음"
        }

        //블로그 주소가 없으면
        if(user.blog != null){
            binding.ivMypageBlog.visibility = View.VISIBLE
            binding.ivMypageBlog.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.blog))
                startActivity(intent)
            }
        } else binding.ivMypageBlog.visibility = View.GONE

        //깃헙 주소가 없으면
        if (user.git != null){
            binding.ivMypageGit.visibility = View.VISIBLE
            binding.ivMypageGit.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.git))
                startActivity(intent)
            }
        } else binding.ivMypageGit.visibility = View.GONE



        if (user.info != null) {
            binding.tvMypageAboutMe.text = user.info
        } else {
            binding.tvMypageAboutMe.text = "자기소개가 없습니다."
        }

//        프로필이 없으면
        if (user.profile == null) {
            binding.ivMypageProfile
        } else {
            binding.ivMypageProfile.load(user.profile)
            binding.ivMypageProfile.clipToOutline = true
        }

       user.level?.let { binding.ivMypageLevel.setLevelIcon(it) }
        binding.tvMypageLevel.text = user.level.toString()

        binding.tvMypageScore.text = user.score.toString()

        Log.d("Tag", "user = ${user}")
    }


    private fun privacypolicy() {
        binding.ctMypage2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://guri999.github.io/"))
            startActivity(intent)
        }
    }

    private fun inquiryOperator() {
        binding.ctMypage4.setOnClickListener {
            viewModel.setOperatorChat()
        }
    }

    private fun postShowMore(){
        binding.tvMypagePostShowMore.setOnClickListener {
            val showMore = Intent(context, MyPageShowMoreActivity::class.java)
            startActivity(showMore)
        }
    }

    private fun likeShowMore(){
        binding.tvLikeMore.setOnClickListener {
            val intent = Intent(requireContext(), MyPageLikeShowMoreActivity::class.java)
            startActivity(intent)
        }
    }

    private fun SkilRecyclerView() {
        skiladapter = MyPageSkilAdapter(MyPageSkilItemManager.getAllItem())
        binding.reMypageItem.adapter = skiladapter
        binding.reMypageItem.layoutManager = GridLayoutManager(context, 4)
    }


    private fun PostRecyclerView() {
        postadapter = MyPagePostAdapter(MyPagePostItemManager.getItemAll())
        binding.reMypageProject.adapter = postadapter
        binding.reMypageProject.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun likeRecyclerView(){
        likeAdapter = MyPageLikeAdapter(likeList)
        binding.rvLike.adapter = likeAdapter
        binding.rvLike.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvLike.itemAnimator = null
    }

    private fun setLogout() = with(viewModel) {
        binding.tvLogout.setOnClickListener {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            logout()
        }
    }

    fun onEditProfile() {

    }
}
