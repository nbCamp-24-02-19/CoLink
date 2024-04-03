package com.seven.colink.ui.home.child

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentHomeStudyBinding
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeStudyFragment : Fragment() {

    private var _binding: FragmentHomeStudyBinding? = null
    private val binding get() = _binding!!
    private val homeChildViewModel: HomeChildViewModel by viewModels()
    private var loading = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeChildViewModel.getBottomItems(5, GroupType.STUDY)
        initViews()
        setObserve()
    }

    private fun initViews() {
        bottomViewsData()
    }

    private fun bottomViewsData() {
        homeChildViewModel.bottomItems.value?.forEachIndexed { index, bottom ->
            val bottomLayout = when (index) {
                0 -> binding.layStudyBottom1
                1 -> binding.layStudyBottom2
                2 -> binding.layStudyBottom3
                3 -> binding.layStudyBottom4
                else -> binding.layStudyBottom5
            }
            try {
                bottomLayout.apply {
                    tvHomeBottomStudy.visibility = View.VISIBLE
                    tvHomeBottomProject.visibility = View.INVISIBLE
                    tvHomeBottomTitle.text = bottom.title
                    tvHomeBottomDes.text = bottom.des
                    tvHomeBottomKind.text =
                        bottom.kind?.map { "# " + it }?.joinToString("   ", "", "")
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
                            val entity = key?.let { homeChildViewModel.getPost(it) }
                            if (key != null && entity != null) {
                                val intent = PostActivity.newIntent(
                                    context = requireContext(), key = key
                                )
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.error_next_time),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e("HomeStudyFragment", "Error during receive data", e)
            }
            binding.layStudyBottom5.viewHomeDivide.visibility = View.INVISIBLE
        }
    }

    private fun setObserve() {
        homeChildViewModel.bottomItems.observe(viewLifecycleOwner) {
            bottomViewsData()
        }

        homeChildViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgressOverlay()
                binding.cvStudy.visibility = View.INVISIBLE
            }else {
                hideProgressOverlay()
                binding.cvStudy.visibility = View.VISIBLE
            }
            loading = !isLoading
        }
    }

    override fun onResume() {
        super.onResume()
        initViews()
        setObserve()
    }

    override fun onStart() {
        super.onStart()
        initViews()
        setObserve()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}