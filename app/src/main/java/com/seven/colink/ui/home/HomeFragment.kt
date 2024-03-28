package com.seven.colink.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.ui.home.adapter.BottomViewPagerAdapter
import com.seven.colink.ui.home.adapter.HomeMainAdapter
import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import com.seven.colink.ui.promotion.ProductPromotionActivity
import com.seven.colink.util.Constants
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
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
        HomeAdapterItems.Header("")
    )
    private var loading = true
    private var backPressed = false

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
        onBackPressed()
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
        binding.vpHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                val currentPx = dpToPixel(40f, requireContext()).toInt()
                val nextPx = dpToPixel(25f,requireContext()).toInt()
                val transX = nextPx + currentPx
                binding.vpHome.offscreenPageLimit = 1

                binding.vpHome.setPageTransformer { page, position ->
                    val scaleFactor = 1 - 0.2 * abs(position)
//                    page.scaleY = scaleFactor.toFloat()
                    page.scaleX = scaleFactor.toFloat()
                    page.translationX = -transX * position
                }
                val pageMargin = currentPx / 2
                val currentItem = binding.vpHome.currentItem
                val marginPx =  pageMargin / 2
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
            dp,context.resources.displayMetrics
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
            topAdapter.notifyDataSetChanged()
            homeItem = mutableListOf(
                HomeAdapterItems.TopView(topAdapter),
                HomeAdapterItems.Header(getString(R.string.home_header))
            )
            mainAdapter.submitList(homeItem)
            mainAdapter.notifyDataSetChanged()
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgressOverlay()
                binding.svHome.visibility = View.INVISIBLE
            }else {
                hideProgressOverlay()
                binding.svHome.visibility = View.VISIBLE
            }
            loading = !isLoading
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (backPressed) {
                    isEnabled = false
                    requireActivity().finish()
                }else{
                    backPressed = true
                    Toast.makeText(requireContext(),getString(R.string.exit_app),Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({backPressed = false},2000)
                }
            }
        })
    }

    private fun topClickItem() = object : TopViewPagerAdapter.ItemClick {
        override fun onClick(view: View, position: Int, item: TopItems) {
            val intent = Intent(requireContext(),ProductPromotionActivity::class.java)
            if (item.key != null) {
                intent.putExtra(Constants.EXTRA_ENTITY_KEY,item.key)
                startActivity(intent)
            }else {
                view.setSnackBar(SnackType.Error,getString(R.string.error_next_time)).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}