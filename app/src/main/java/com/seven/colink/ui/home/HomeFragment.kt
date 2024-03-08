package com.seven.colink.ui.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.ui.home.adapter.BottomViewPagerAdapter
import com.seven.colink.ui.home.adapter.HomeMainAdapter
import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import com.seven.colink.ui.post.register.PostActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainAdapter by lazy { HomeMainAdapter() }
    private lateinit var bottomAdapter : BottomViewPagerAdapter
    private lateinit var topAdapter : TopViewPagerAdapter
    private val homeViewModel : HomeViewModel by viewModels()
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
        binding.vpHome.adapter = bottomAdapter
        previewViewPager()
    }

    private fun previewViewPager(){

        val pageMargin = resources.getDimensionPixelOffset(R.dimen.page_home_margin)
        val pagerOffset = resources.getDimensionPixelOffset(R.dimen.offset_home_between_pages)
        val screenWidth = resources.displayMetrics.widthPixels

        binding.vpHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                val currentPx = dpToPixel(40f, requireContext()).toInt()
                Log.d("Home","#iii currentPx = $currentPx")
                val nextPx = dpToPixel(20f,requireContext()).toInt()
                Log.d("Home","#iii nextPx = $nextPx")
                val transX = nextPx + currentPx
                Log.d("Home","#iii transX = $transX")
                binding.vpHome.offscreenPageLimit = 2

                binding.vpHome.setPageTransformer { page, position ->
                    val scaleFactor = if (position <= 0) {
                        (1 - 0.2 * abs(position))
                    } else {
                        (1 - 0.2 * abs(position))
                    }

                    page.scaleX = scaleFactor.toFloat()
                    page.translationX = -transX * position
                }
                val recyclerViewMargin = currentPx / 2
                Log.d("Home","#iii recyclerViewMargin = $recyclerViewMargin")
                val currentItem = binding.vpHome.currentItem
                val marginPx =  recyclerViewMargin / 2
                Log.d("Home","#iii marginPx = $marginPx")
                if (currentItem == 0) {
                    binding.vpHome.setPaddingRelative(0, 0, marginPx, 0)
                }else{
                    binding.vpHome.setPaddingRelative(marginPx, 0, 0, 0)
                }

                binding.vpHome.clipToPadding = false

            }
        })
    }

    private fun dpToPixel(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }



    private fun pixelToDp(px: Int, context: Context) : Float {
        return px / ((context.resources.displayMetrics.densityDpi.toFloat()) / DisplayMetrics.DENSITY_DEFAULT)
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
                    startActivity(
                        PostActivity.newIntent(
                            context = requireActivity(),
                            key = item.key
                        )
                    )
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