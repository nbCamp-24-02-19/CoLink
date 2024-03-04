package com.seven.colink.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seven.colink.R
import com.seven.colink.databinding.FragmentMyPageEditDetailBinding

class MyPageEditDetailFragment : Fragment() {

    private lateinit var binding: FragmentMyPageEditDetailBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyPageEditDetailBinding.inflate(layoutInflater)
        return binding.root

        binding.ivMypageDetailBack.setOnClickListener {
//            finish()
        }
    }


}