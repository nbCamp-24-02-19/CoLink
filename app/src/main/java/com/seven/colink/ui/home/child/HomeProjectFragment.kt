package com.seven.colink.ui.home.child

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeProjectBinding
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.ui.home.HomeViewModel
import com.seven.colink.ui.home.adapter.BottomHomeProjectAdapter

class HomeProjectFragment : Fragment() {

    private var _binding: FragmentHomeProjectBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by viewModels()
    private val mAdapter by lazy { BottomHomeProjectAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProjectBinding.inflate(inflater,container,false)
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

    private fun bottomViews() {
        homeViewModel.getBottomItems(5)
        mAdapter.submitList(homeViewModel.bottomItems.value)
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner){
            mAdapter.submitList(homeViewModel.bottomItems.value)
        }
    }

    private fun clickItem() = object : BottomHomeProjectAdapter.ItemClick {
        override fun onClick(view: View, position: Int) {
            val item = mAdapter.currentList[position]

        }
    }
}