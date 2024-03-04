package com.seven.colink.ui.home.child

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.ui.home.HomeViewModel
import com.seven.colink.ui.home.adapter.BottomHomeStudyAdapter
import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import com.seven.colink.ui.post.content.PostContentActivity
import com.seven.colink.util.status.ProjectStatus
import kotlinx.coroutines.launch

class HomeStudyFragment : Fragment() {

    private var _binding: FragmentHomeStudyBinding? = null
    private val binding get() = _binding!!
//    private val homeViewModel : HomeViewModel by viewModels()
    private val homeViewModel : HomeViewModel by activityViewModels()
//    private val mAdapter by lazy { BottomHomeStudyAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeStudyBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setObserve()
    }

    private fun initViews(){
        bottomViewsData()
    }

    private fun bottomViewsData(){
        val num = 5
        homeViewModel.getBottomItems(num)
//        mAdapter.submitList(homeViewModel.bottomItems.value)

        homeViewModel.bottomItems.value?.forEachIndexed { index, bottom ->
            val bottomLayout = when (index) {
                0 -> binding.layStudyBottom1
                1 -> binding.layStudyBottom2
                2 -> binding.layStudyBottom3
                3 -> binding.layStudyBottom4
                else -> binding.layStudyBottom5
            }

            bottomLayout.apply {
                tvHomeBottomStudy.visibility = View.VISIBLE
                tvHomeBottomProject.visibility = View.INVISIBLE
                tvHomeBottomTitle.text = bottom.title
                tvHomeBottomDes.text = bottom.des
                tvHomeBottomKind.text = bottom.kind?.toString()
                tvHomeBottomLv.text = bottom.lv
                ivHomeBottomThumubnail.load(bottom.img)
                if (bottom.blind == ProjectStatus.END) {
                    viewHomeBottomBlind.visibility = View.VISIBLE
                    tvHomeBottomBlind.visibility = View.VISIBLE
                } else {
                    viewHomeBottomBlind.visibility = View.INVISIBLE
                    tvHomeBottomBlind.visibility = View.INVISIBLE
                }
                layBottom.setOnClickListener {
                    lifecycleScope.launch {
                        val key = bottom.key
                        val entity = key?.let { homeViewModel.getPost(it) }
                        Log.d("Detail","#ccc 새로 받은 entitiy = $entity")
                        if (entity != null) {
                            val intent = PostContentActivity.newIntentForUpdate(
                                requireContext(),
                                index,
                                entity
                            )
                            startActivity(intent)
                        }else {
                            Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
//            mAdapter.submitList(homeViewModel.bottomItems.value)
        }
    }
}