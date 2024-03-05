package com.seven.colink.ui.home

import android.content.Context
import android.content.Intent
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
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.dialog.setLevelDialog
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

    override fun onResume() {
        super.onResume()
        setTopItems()
    }

    private fun initViews() {
        initViewAdapter()
        setObserve()
    }

    private fun initViewAdapter() {
        topAdapter = TopViewPagerAdapter()
        topAdapter.itemClick = topClickItem()

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
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
//            val newItems = ArrayList(it)
//            topAdapter.submitList(newItems)
            topAdapter.submitList(it)
            Log.d("Home","#ddd it = $it")
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