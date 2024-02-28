package com.seven.colink.ui.mypage

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageBinding
import com.seven.colink.ui.mypage.adapter.MyPagePostAdapter
import com.seven.colink.ui.mypage.adapter.MyPageSkilAdapter

class MyPageFragment : Fragment() {
    val TAG: String = "로그"

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var skiladapter: MyPageSkilAdapter
    private lateinit var postadapter: MyPagePostAdapter

    companion object {
        fun newInstance() = MyPageFragment()
    }

    private lateinit var viewModel: MyPageViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)


        SkilRecyclerView()
        editClick()
        mypageBlogClick()
        mypagegitClick()

        skiladapter.plusClick = object : MyPageSkilAdapter.PlusClick{
            override fun onClick(item: MyPageItem, position: Int) {

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

    private fun editClick(){
        binding.ivMypageEdit.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.mypage_edit_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()

            val okButton = mDialogView.findViewById<Button>(R.id.btn_mypage_ok)
            val mypageEdit = mDialogView.findViewById<EditText>(R.id.et_mypage_edit)
            val cancelButton = mDialogView.findViewById<Button>(R.id.btn_mypage_cancel)

            okButton.setOnClickListener {
                binding.tvMypageName.text = mypageEdit.text
                mAlertDialog.dismiss()

            }
            cancelButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

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