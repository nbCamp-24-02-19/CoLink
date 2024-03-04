package com.seven.colink.ui.home.child

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeProjectBinding
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.ui.home.HomeFragment
import com.seven.colink.ui.home.HomeViewModel
import com.seven.colink.ui.home.adapter.BottomHomeProjectAdapter
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

class HomeProjectFragment : Fragment() {

    private var _binding: FragmentHomeProjectBinding? = null
    private val binding get() = _binding!!

    //    private val homeViewModel : HomeViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
//    private val mAdapter by lazy { BottomHomeProjectAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setObserve()
    }

    private fun initViews() {
        bottomViewsData()
    }

    private fun bottomViewsData() {
        homeViewModel.getBottomItems(5)
//        mAdapter.submitList(homeViewModel.bottomItems.value)
        Log.d("Child", "#ccc bottomItems = ${homeViewModel.bottomItems.value}")

        homeViewModel.bottomItems.value?.forEachIndexed { index, it ->
            val bottomLayout = when (index) {
                0 -> binding.layProjectBottom1
                1 -> binding.layProjectBottom2
                2 -> binding.layProjectBottom3
                3 -> binding.layProjectBottom4
                else -> binding.layProjectBottom5
            }

            bottomLayout.apply {
                tvHomeBottomStudy.visibility = View.INVISIBLE
                tvHomeBottomProject.visibility = View.VISIBLE
                tvHomeBottomTitle.text = it.title
                tvHomeBottomDes.text = it.des
                Log.d("Child", "#ccc des = ${it.des}")
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
        homeViewModel.topItems.observe(viewLifecycleOwner) {
//            mAdapter.submitList(homeViewModel.bottomItems.value)
        }
    }

    private fun clickItem() = object : BottomHomeProjectAdapter.ItemClick {
        override fun onClick(view: View, position: Int) {
//            val item = mAdapter.currentList[position]

        }
    }
}