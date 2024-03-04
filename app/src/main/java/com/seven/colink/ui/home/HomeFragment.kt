package com.seven.colink.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.ui.home.adapter.BottomViewPagerAdapter
import com.seven.colink.ui.home.adapter.HomeMainAdapter
import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import com.seven.colink.ui.post.content.PostContentActivity
import com.seven.colink.util.showToast
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
        Log.d("Home","#bbb setTop = ${homeViewModel.topItems.value}")
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
            Log.d("Home","#bbb null이니? = ${it}")
            val newItems = ArrayList(it)
            topAdapter.submitList(newItems){Log.d("Home","#bbb submitList")}
        }
    }

    private fun topClickItem() = object : TopViewPagerAdapter.ItemClick {
        override fun onClick(view: View, position: Int, item: TopItems) {
            lifecycleScope.launch {
                val key = item.key
                Log.d("Detail","#ccc key = $key")
                val entity = key?.let { homeViewModel.getPost(it) }
                Log.d("Detail","#ccc 새로 받은 entitiy = $entity")
                if (entity != null) {
                    val intent = PostContentActivity.newIntentForUpdate(
                        requireContext(),
                        position,
                        entity
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