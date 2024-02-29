package com.seven.colink.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.ui.home.adapter.BottomViewPagerAdapter
import com.seven.colink.ui.home.adapter.HomeMainAdapter
import com.seven.colink.ui.home.adapter.TopViewPagerAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainAdapter by lazy { HomeMainAdapter() }
    private lateinit var bottomAdapter : BottomViewPagerAdapter
    private val topAdapter by lazy { TopViewPagerAdapter() }

    private val tolList = mutableListOf<TopItems>(
        TopItems(R.drawable.img_dialog_project,"팀이름","날짜","타이틀"),
        TopItems(R.drawable.img_dialog_study,"팀","yyyy-mm","title2"),
        TopItems(R.drawable.img_user_grade,"team이름","yyyy-mm","title2"),
        TopItems(R.drawable.img_dialog_study,"이름","yyyy-mm","title2"),
        TopItems(R.drawable.img_dialog_project,"이거","yyyy-mm","title2"),
        TopItems(R.drawable.img_dialog_study,"뭐라고","yyyy-mm","title2"),
        TopItems(R.drawable.img_temporary,"할까","yyyy-mm","title2")
    )          // dummy data

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        bottomAdapter = BottomViewPagerAdapter(this)
        binding.vpHome.adapter = bottomAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        topAdapter.submitList(tolList)          // dummy data

        val homeItem : MutableList<HomeAdapterItems> = mutableListOf(
            HomeAdapterItems.TopView(topAdapter),
            HomeAdapterItems.Header("그룹 추천")
        )

        with(binding.rvHome) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        mainAdapter.submitList(homeItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}