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
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.home.HomeViewModel
import com.seven.colink.ui.post.content.PostContentActivity
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import kotlinx.coroutines.launch

class HomeStudyFragment : Fragment() {

    private var _binding: FragmentHomeStudyBinding? = null
    private val binding get() = _binding!!

    //    private val homeViewModel : HomeViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
//    private val mAdapter by lazy { BottomHomeStudyAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getBottomItems(5)
        initViews()
        setObserve()
    }

    private fun initViews() {
        bottomViewsData()
    }

    private fun bottomViewsData() {
//        homeViewModel.getBottomItems(5)
//        mAdapter.submitList(homeViewModel.bottomItems.value)

        homeViewModel._bottomItems.value?.forEachIndexed { index, bottom ->
            val bottomLayout = when (index) {
                0 -> binding.layStudyBottom1
                1 -> binding.layStudyBottom2
                2 -> binding.layStudyBottom3
                3 -> binding.layStudyBottom4
                else -> binding.layStudyBottom5
            }

            bottomLayout.apply {
//                if (bottom.typeId == GroupType.STUDY) {
//                    while (true){
//                        if (homeViewModel._bottomItems.value?.size!! < 5){
//                            homeViewModel.getBottomItems(5)
//                            if (homeViewModel._bottomItems.value?.size!! > 5){
//                                val currentList = homeViewModel._bottomItems.value?.toMutableList()
//                                currentList?.let {
//                                    it.removeAt(it.size -1)
//                                    homeViewModel._bottomItems.value = it
//                                }
//                            }
//                        }else if (homeViewModel._bottomItems.value?.size!! == 5) {
//                            break
//                        }
//                    }

                     if (homeViewModel._bottomItems.value?.size!! == 5) {
                            tvHomeBottomStudy.visibility = View.VISIBLE
                            tvHomeBottomProject.visibility = View.INVISIBLE
                            tvHomeBottomTitle.text = bottom.title
                         Log.d("Study","#eee study = $bottom")
                            Log.d("Study","#eee 3번째 들어오는거 맞아? ${bottom.title}")

                            tvHomeBottomDes.text = bottom.des
                            tvHomeBottomKind.text = bottom.kind?.toString()
                            viewHomeBottomDivider.visibility = View.INVISIBLE
                            tvHomeBottomLv.visibility = View.INVISIBLE
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
                                    if (key != null) {
                                        val intent = PostContentActivity.newIntent(requireContext(),key)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(requireContext(), "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                     }
//                }
            }
        }
    }

    private fun setObserve() {
        homeViewModel.topItems.observe(viewLifecycleOwner) {
//            mAdapter.submitList(homeViewModel.bottomItems.value)
            bottomViewsData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}