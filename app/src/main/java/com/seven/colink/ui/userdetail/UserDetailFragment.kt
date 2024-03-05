package com.seven.colink.ui.userdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentUserDetailBinding
import com.seven.colink.ui.mypage.MyPageItem
import com.seven.colink.ui.mypage.MyPageSkilItemManager
import com.seven.colink.util.skillCategory

class UserDetailFragment : Fragment() {
    private lateinit var binding: FragmentUserDetailBinding
    private lateinit var viewModel: UserDetailViewModel
    private lateinit var adapter: UserSkillAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {

        binding = FragmentUserDetailBinding.inflate(layoutInflater)


        userSkill()
        viewModel.userDetails.observe(viewLifecycleOwner) {userDetails ->
            updateUI(userDetails)
            adapter.changeDataset(userDetails.userSkill?.map { UserSkillItem(it,UserSkillItemManager.addItem(it)) }?: emptyList())
        }


        return binding.root
    }

    private fun updateUI(user: UserDetailModel){
        binding.tvUserdetailName.text = user.userName
        binding.tvUserdetailAboutMe.text = user.userInfo
        binding.tvUserdetailSpecialization.text = user.userMainSpecialty
        binding.tvUserdetailScore.text = user.userscore.toString()
        val level = user.userLevel
        if (level == 1){
            binding.tvMypageLevel.text = "1"
//            DrawableCompat.setTint(
//                levelicon.mutate(),
//                ContextCompat.getColor(requireContext(),R.color.level1)
//            )
        }
    }

    private fun userSkill(){
        adapter = UserSkillAdapter(UserSkillItemManager.getItem())
        binding.reUserdetailItem.adapter = adapter
        binding.reUserdetailItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

}