package com.seven.colink.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.seven.colink.databinding.FragmentChatBinding
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.ui.chat.viewmodel.ChatTabViewModel
import com.seven.colink.ui.chat.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private lateinit var viewModel: ChatViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        setViewPager()
    }

    private fun setViewPager() = with(binding) {
        vpChatPager.adapter = object : FragmentStateAdapter(this@ChatFragment) {
            override fun getItemCount() = 3
            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0 -> ChatTabFragment.newInstance(ChatTabType.GENERAL)
                    1 -> ChatTabFragment.newInstance(ChatTabType.PROJECT)
                    else -> ChatTabFragment.newInstance(ChatTabType.STUDY)
                }
            }
        }

        TabLayoutMediator(tlChatTab, vpChatPager) { tab, position ->
            tab.text = when (position) {
                0 -> "일반 채팅"
                1 -> "프로젝트 채팅"
                else -> "스터디 채팅"
            }
        }.attach()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}