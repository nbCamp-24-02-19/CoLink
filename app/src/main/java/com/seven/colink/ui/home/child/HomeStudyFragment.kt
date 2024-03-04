package com.seven.colink.ui.home.child

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.HomeViewModel
import com.seven.colink.ui.home.adapter.BottomHomeStudyAdapter
import com.seven.colink.util.status.ProjectStatus

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
        homeViewModel.getBottomItems(5)
//        mAdapter.submitList(homeViewModel.bottomItems.value)

        homeViewModel.bottomItems.value?.forEachIndexed { index, it ->
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
                tvHomeBottomTitle.text = it.title
                tvHomeBottomDes.text = it.des
                tvHomeBottomKind.text = it.kind?.toString()
                tvHomeBottomLv.text = it.lv
                ivHomeBottomThumubnail.load(it.img)
                if (it.blind == ProjectStatus.END) {
                    viewHomeBottomBlind.visibility = View.VISIBLE
                    tvHomeBottomBlind.visibility = View.VISIBLE
                } else {
                    viewHomeBottomBlind.visibility = View.INVISIBLE
                    tvHomeBottomBlind.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
//            mAdapter.submitList(homeViewModel.bottomItems.value)
        }
    }

    private fun clickItem() = object : BottomHomeStudyAdapter.ItemClick {
        override fun onClick(view: View, position: Int) {
//            val item = mAdapter.currentList[position]

        }
    }
}