package com.seven.colink.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.ui.home.adapter.BottomViewPagerAdapter
import com.seven.colink.ui.home.adapter.HomeMainAdapter
import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainAdapter by lazy { HomeMainAdapter() }
    private lateinit var bottomAdapter : BottomViewPagerAdapter
//    private val topAdapter by lazy { TopViewPagerAdapter() }
    private lateinit var topAdapter : TopViewPagerAdapter
//    private val homeViewModel by viewModels<HomeViewModel>()
    private val homeViewModel : HomeViewModel by activityViewModels()
    private lateinit var homeItem : MutableList<HomeAdapterItems>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        initViewAdapter()
        setObserve()
        setTopItems()
    }

    private fun initViewAdapter() {
        topAdapter = TopViewPagerAdapter()

        homeItem = mutableListOf(
            HomeAdapterItems.TopView(topAdapter),
            HomeAdapterItems.Header("그룹 추천")
        )

        with(binding.rvHome) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        mainAdapter.submitList(homeItem)

        bottomAdapter = BottomViewPagerAdapter(this)
        binding.vpHome.adapter = bottomAdapter

    }

    private fun setTopItems() {
        homeViewModel.getTopItems(7)
        Log.d("Home","#bbb setTop = ${homeViewModel.topItems.value}")
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
            Log.d("Home","#bbb null이니? = ${it}")
            val newItems = it.toMutableList()
            topAdapter.submitList(newItems)
        }
    }

    private fun clickItem() = object : TopViewPagerAdapter.ItemClick {
        override fun onClick(view: View, position: Int) {
            val item = topAdapter.currentList[position]

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}