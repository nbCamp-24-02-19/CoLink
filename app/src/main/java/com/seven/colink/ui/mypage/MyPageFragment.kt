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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageBinding
import com.seven.colink.databinding.ItemSignUpSkillBinding
import com.seven.colink.databinding.MypageEditDialogBinding
import com.seven.colink.ui.mypage.MyPageItem.skilItems
import com.seven.colink.ui.mypage.adapter.MyPagePostAdapter
import com.seven.colink.ui.mypage.adapter.MyPageSkilAdapter
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.skillCategory
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.AndroidEntryPoint

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


        privacypolicy()
        SkilRecyclerView()
        PostRecyclerView()
//        settingClick()

        //스킬 추가
        skiladapter.plusClick = object : MyPageSkilAdapter.PlusClick{
            override fun onClick(item: MyPageItem, position: Int) {

                val binding_ = ItemSignUpSkillBinding.inflate(layoutInflater)
                skillCategory.setDialog(binding_.root.context, "사용 가능한 언어/툴을 선택해주세요"){
                    binding_.btSignUpSubCategoryBtn.text = it
                    viewModel.updateSkill(it)
                    Log.d("tag", "skil = $it")
                }.show()
            }

        }
        //스킬 삭제
        skiladapter.skilLongClick = object : MyPageSkilAdapter.SkilLongClick{
            override fun onLongClick(language: Any, position: Int) {
                val ad = AlertDialog.Builder(context)
                ad.setTitle("삭제")
                ad.setMessage("정말로 삭제하시겠습니까?")
                ad.setPositiveButton("확인"){dialog,_ ->
                    viewModel.removeSkill(language)
                }
                ad.setNegativeButton("취소"){dialog,_ ->
                    dialog.dismiss()
                }
                ad.show()
            }
        }


        //파이어베이스 유저 정보 연결 & 스킬 연결
        viewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            // Update UI with user details
            updateUI(userDetails)
            skiladapter.changeDataset(userDetails.skill?.map { skilItems(skillCategory.indexOf(it),it,MyPageSkilItemManager.addItem(it)) }
                ?.plus(MyPageItem.plusItems(99,R.drawable.ic_add_24)) ?: emptyList())

            Log.d("Tag", "${userDetails.skill}")
        }

        //파이어베이스 유저 등록글
        viewModel.userPost.observe(viewLifecycleOwner) { it ->
            it?.map{post ->
                if (post.grouptype == GroupType.PROJECT){
                    MyPostItem.MyPagePostItem(if (post.ing != ProjectStatus.END){
                        "참여중"
                    } else "완료", projectName = post.title.toString(), projectTime = post.time.toString())
                } else {
                    MyPostItem.MyPageStudyItem(if(post.ing != ProjectStatus.END){
                        "참여중"
                    } else "완료",  post.title.toString(), post.time.toString()
                    )

                }}?.let { it1 -> postadapter.changeDataset(it1) }
            Log.e("Tag","${it}")
            }

        return binding.root
    }



    private fun updateUI(user: MyPageUserModel) {
        // Update your views with user information
        binding.tvMypageName.text = user.name
        binding.tvMypageSpecialization2.text = user.mainSpecialty
        binding.ivMypageBlog.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.blog))
            startActivity(intent)
        }
        binding.ivMypageGit.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.git))
            startActivity(intent)
        }
        binding.tvMypageAboutMe.text = user.info

        val uri = Uri.parse(user.profile.toString())
        binding.ivMypageProfile.setImageURI(uri)

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



//    private fun settingClick(){
//        binding.ivMypageSetting.setOnClickListener {
//            val myPageEditDetailFragment = LayoutInflater.from(context).inflate(R.layout.fragment_my_page_edit_detail, null)
//            val myBuilder = AlertDialog.Builder(context)
//                .setView(myPageEditDetailFragment)
//            val mAlertDialog = myBuilder.show()
//
//            val mypageBackButton = myPageEditDetailFragment.findViewById<ImageView>(R.id.iv_mypage_detail_back)
//
//            mypageBackButton.setOnClickListener {
//                mAlertDialog.dismiss()
//            }
//        }
//    }


    private fun SkilRecyclerView(){
        skiladapter = MyPageSkilAdapter(MyPageSkilItemManager.getAllItem())
        binding.reMypageItem.adapter = skiladapter
        binding.reMypageItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun PostRecyclerView(){
        postadapter = MyPagePostAdapter(MyPagePostItemManager.getItemAll())
        binding.reMypageProject.adapter = postadapter
        binding.reMypageProject.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }



}