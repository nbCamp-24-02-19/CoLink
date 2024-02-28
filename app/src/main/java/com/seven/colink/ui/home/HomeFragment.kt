package com.seven.colink.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeBinding
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.dialog.setLevelDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainAdapter by lazy { HomeMainAdapter() }
    private lateinit var bottomAdapter : BottomViewPagerAdapter
    private val topAdapter by lazy { TopViewPagerAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        bottomAdapter = BottomViewPagerAdapter(this)
        binding.vpHome.adapter = bottomAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        val homeItem : MutableList<HomeAdapterItems> = mutableListOf(
            HomeAdapterItems.TopView(topAdapter),
            HomeAdapterItems.Header("그룹 추천")
        )

        with(binding.rvHome) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        mainAdapter.submitList(homeItem)
    }


    override fun onResume() {
        super.onResume()
        requireContext().setDialog(
            title = "안녕하세요",
            message = "'다이얼로그'가 잘 작성 되는지 '확인' 해보는 다이얼로그",
            image = R.drawable.img_dialog_project,
            confirmAction = { dialog -> dialog.dismiss()
                            requireContext().setDialog(
                                title = "반갑고",
                                message = "안녕하고",
                                confirmAction = {dialog -> dialog.dismiss()
                                                requireContext().setLevelDialog { Unit }.show()},
                                cancelAction = {dialog -> dialog.dismiss()}
                            ).show()},
            cancelAction = {dialog -> dialog.dismiss()}
        ).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}