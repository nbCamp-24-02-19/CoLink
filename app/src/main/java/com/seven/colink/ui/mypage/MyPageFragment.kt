package com.seven.colink.ui.mypage

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding

    companion object {
        fun newInstance() = MyPageFragment()
    }

    private lateinit var viewModel: MyPageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)

        //test
        val dataList = mutableListOf(
            MyPageData.MyPageName(R.drawable.ic_profile,R.drawable.ic_level_1,"강아지","Android"),
            MyPageData.MyPageAboutMe("저는 코끼리를 좋아합니다. \n붕어빵은 맛있습니다. 먹고싶다.."),
            MyPageData.MyPagePost("참여중", "코링 프로젝트", "현재"),
            MyPageData.MyPageTerms("이용약관"),
            MyPageData.MyPageTerms("개인정보처리방침"),
            MyPageData.MyPageTerms("공지사항"),
            MyPageData.MyPageTerms("운영자 1:1 문의")
        )

        val adapater = MyPageAdapater(dataList)
        binding.reMypage.adapter = adapater
        binding.reMypage.layoutManager = LinearLayoutManager(context)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}