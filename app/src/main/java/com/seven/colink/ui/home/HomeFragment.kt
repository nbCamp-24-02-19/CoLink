package com.seven.colink.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.ui.home.adapter.BottomViewPagerAdapter
import com.seven.colink.ui.home.adapter.HomeMainAdapter
import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import com.seven.colink.ui.post.content.PostContentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainAdapter by lazy { HomeMainAdapter() }
    private lateinit var bottomAdapter : BottomViewPagerAdapter
    private lateinit var topAdapter : TopViewPagerAdapter
    private val homeViewModel : HomeViewModel by activityViewModels()
    private var homeItem = mutableListOf<HomeAdapterItems>(
        HomeAdapterItems.TopView(TopViewPagerAdapter()),
        HomeAdapterItems.Header("그룹 추천")
    )

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
        setTopItems()
        initViews()
    }

    private fun initViews() {
        initViewAdapter()
        setObserve()
    }

    private fun initViewAdapter() {
        topAdapter = TopViewPagerAdapter()
        topAdapter.itemClick = topClickItem()

        with(binding.rvHome) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        bottomAdapter = BottomViewPagerAdapter(this)
        previewViewPager()
    }

    private fun previewViewPager(){
        binding.vpHome.adapter = bottomAdapter
        binding.vpHome.offscreenPageLimit = 2
        val pageMargin = resources.getDimensionPixelOffset(R.dimen.page_home_margin)
        val pagerOffset = resources.getDimensionPixelOffset(R.dimen.offset_home_between_pages)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - 4*(pageMargin + pagerOffset) + (pagerOffset/4)

        binding.vpHome.setPageTransformer { page, position ->
            page.translationX = position * (-offsetPx )
        }
    }

    private fun setTopItems() {
        homeViewModel.getTopItems(7)
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
            topAdapter.submitList(it)
            homeItem = mutableListOf(
                HomeAdapterItems.TopView(topAdapter),
                HomeAdapterItems.Header("그룹 추천")
            )
            mainAdapter.submitList(homeItem)
        }
    }

    private fun topClickItem() = object : TopViewPagerAdapter.ItemClick {
        override fun onClick(view: View, position: Int, item: TopItems) {
            lifecycleScope.launch {
                val key = item.key
                val entity = key?.let { homeViewModel.getPost(it) }
                if (entity != null) {
                    val intent = PostContentActivity.newIntent(
                        requireContext(),
                        entity.key
                    )
                    startActivity(intent)
                }else {
                    Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}