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
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageBinding
import com.seven.colink.databinding.ItemSignUpSkillBinding
import com.seven.colink.databinding.MypageEditDialogBinding
import com.seven.colink.ui.mypage.adapter.MyPagePostAdapter
import com.seven.colink.ui.mypage.adapter.MyPageSkilAdapter
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


        SkilRecyclerView()
        editClick()
        mypageBlogClick()
        mypagegitClick()
        PostRecyclerView()

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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
        // TODO: Use the ViewModel
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


    private fun editClick(){
        binding.ivMypageEdit.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.mypage_edit_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()


            val profileCilck = mDialogView.findViewById<ImageView>(R.id.iv_mypage_edit)
            val okButton = mDialogView.findViewById<Button>(R.id.btn_mypage_ok)
            val mypageEdit = mDialogView.findViewById<EditText>(R.id.et_mypage_edit)
            val cancelButton = mDialogView.findViewById<Button>(R.id.btn_mypage_cancel)

            profileCilck.setOnClickListener {
                //갤러리 권한...ㅠㅠ
                if (imageUri != null) { //이미 프로필 사진이 있는 경우, 프로필 삭제
                    imageUri = null

                    _binding.ivMypageEdit.clipToOutline = true

                    return@setOnClickListener
                }
                //갤러리에서 프로필 사진 가져오기
                val imageIntent = Intent(
                    Intent.ACTION_GET_CONTENT,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )

                imageIntentLauncher.launch(imageIntent)

//                imageIntent = binding.ivMypageProfile.setImageResource()

            }

            okButton.setOnClickListener {
                binding.tvMypageName.text = mypageEdit.text
                mAlertDialog.dismiss()

            }
            cancelButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

        }
    }

    private val imageIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            imageUri = it.data?.data
            activity?.grantUriPermission(
                "com.seven.colink.ui.mypage",
                imageUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            _binding.ivMypageEdit.setImageURI(imageUri)
            _binding.ivMypageEdit.clipToOutline = true
        }
    }

    private fun editTextClick(){
        binding.tvMypageItemEdit.setOnClickListener {
            //회원가입때 받았던 정보입력 페이지 재활용?
        }
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