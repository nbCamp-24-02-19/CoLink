package com.seven.colink.ui.home.child

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.ui.home.HomeViewModel
import com.seven.colink.ui.home.adapter.BottomHomeProjectAdapter
import com.seven.colink.ui.home.adapter.BottomHomeStudyAdapter

class HomeStudyFragment : Fragment() {

    private var _binding: FragmentHomeStudyBinding? = null
    private val binding get() = _binding!!
//    private val homeViewModel : HomeViewModel by viewModels()
    private val homeViewModel : HomeViewModel by activityViewModels()
    private val mAdapter by lazy { BottomHomeStudyAdapter() }

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
        bottomViews()
    }

    private fun bottomViews(){
        homeViewModel.getBottomItems(5)
        mAdapter.submitList(homeViewModel.bottomItems.value)
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
            mAdapter.submitList(homeViewModel.bottomItems.value)
        }
    }

    private fun clickItem() = object : BottomHomeStudyAdapter.ItemClick {
        override fun onClick(view: View, position: Int) {
            val item = mAdapter.currentList[position]

        }
    }
}