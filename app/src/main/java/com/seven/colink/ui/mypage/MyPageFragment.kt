package com.seven.colink.ui.mypage

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.findFragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageBinding
import com.seven.colink.databinding.ItemSignUpSkillBinding
import com.seven.colink.databinding.MypageEditDialogBinding
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.mypage.adapter.MyPagePostAdapter
import com.seven.colink.ui.mypage.adapter.MyPageSkilAdapter
import com.seven.colink.ui.userdetail.UserDetailFragment
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.skillCategory


class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var _binding: MypageEditDialogBinding
    private lateinit var skiladapter: MyPageSkilAdapter
    private lateinit var postadapter: MyPagePostAdapter



    var imageUri: Uri? = null


    companion object {
        fun newInstance() = MyPageFragment()
    }

    private lateinit var viewModel: MyPageViewModel



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)
        _binding = MypageEditDialogBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)

        SkilRecyclerView()
        mypageBlogClick()
        mypagegitClick()
        PostRecyclerView()
        settingClick()

        //스킬 추가
        skiladapter.plusClick = object : MyPageSkilAdapter.PlusClick{
            override fun onClick(item: MyPageItem, position: Int) {

                val binding_ = ItemSignUpSkillBinding.inflate(layoutInflater)
                skillCategory.setDialog(binding_.root.context, "사용 가능한 언어/툴을 선택해주세요"){
                    binding_.btSignUpSubCategoryBtn.text = it
                    MyPageSkilItemManager.addItem(it)
                    skiladapter.changeDataset(MyPageSkilItemManager.getAllItem())
                    skiladapter.notifyDataSetChanged()

                    Log.d("tag", "skil = $it")
                }.show()
            }
        }
        //스킬 삭제
        skiladapter.skilLongClick = object : MyPageSkilAdapter.SkilLongClick{
            override fun onLongClick(language: String, position: Int) {
                val ad = AlertDialog.Builder(context)
                ad.setTitle("삭제")
                ad.setMessage("정말로 삭제하시겠습니까?")
                ad.setPositiveButton("확인"){dialog,_ ->
                    MyPageSkilItemManager.removeItem(language)
                    skiladapter.changeDataset(MyPageSkilItemManager.getAllItem())
                }
                ad.setNegativeButton("취소"){dialog,_ ->
                    dialog.dismiss()
                }
                ad.show()
            }
        }

        return binding.root
    }




    private fun settingClick(){
        binding.ivMypageSetting.setOnClickListener {
            val myPageEditDetailFragment = LayoutInflater.from(context).inflate(R.layout.fragment_my_page_edit_detail, null)
            val myBuilder = AlertDialog.Builder(context)
                .setView(myPageEditDetailFragment)
            val mAlertDialog = myBuilder.show()

            val mypageBackButton = myPageEditDetailFragment.findViewById<ImageView>(R.id.iv_mypage_detail_back)

            mypageBackButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
    }


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


    private fun mypageBlogClick(){
        binding.ivMypageBlog.setOnClickListener {
            //test 주소
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com/"))
            startActivity(intent)
        }
    }

    private fun mypagegitClick(){
        binding.ivMypageBlog.setOnClickListener {
            //test 주소
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com/"))
            startActivity(intent)
        }
    }

}