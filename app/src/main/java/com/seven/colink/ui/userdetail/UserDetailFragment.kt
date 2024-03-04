package com.seven.colink.ui.userdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.seven.colink.R
import com.seven.colink.databinding.FragmentUserDetailBinding

class UserDetailFragment : Fragment() {
    private lateinit var binding: FragmentUserDetailBinding
    private lateinit var viewModel: UserDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {

        binding = FragmentUserDetailBinding.inflate(layoutInflater)


        viewModel.userDetails.observe(viewLifecycleOwner) {userDetails ->
            updateUI(userDetails)

        }


        return binding.root
    }

    private fun updateUI(user: UserDetailModel){
        binding.tvUserdetailName.text = user.userName
    }

}