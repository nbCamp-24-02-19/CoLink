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
import com.seven.colink.databinding.FragmentHomeProjectBinding
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeProjectFragment : Fragment() {

    private var _binding: FragmentHomeProjectBinding? = null
    private val binding get() = _binding!!
    private val homeChildViewModel: HomeChildViewModel by viewModels()
    private var loading = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeChildViewModel.getBottomItems(5, GroupType.PROJECT)
        initViews()
        setObserve()
    }

    private fun initViews() {
        bottomViewsData()
    }

    private fun bottomViewsData() {
        homeChildViewModel.bottomItems.value?.forEachIndexed { index, bottom ->
            val bottomLayout = when (index) {
                0 -> binding.layProjectBottom1
                1 -> binding.layProjectBottom2
                2 -> binding.layProjectBottom3
                3 -> binding.layProjectBottom4
                else -> binding.layProjectBottom5
            }

            try {
                bottomLayout.apply {
                    tvHomeBottomStudy.visibility = View.INVISIBLE
                    tvHomeBottomProject.visibility = View.VISIBLE
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
                                    "다음에 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeProjectFragment", "Error during receive data", e)
            }

        }
    }

    private fun setObserve() {
        homeChildViewModel.bottomItems.observe(viewLifecycleOwner) {
            bottomViewsData()
        }

        homeChildViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgressOverlay()
                binding.cvProject.visibility = View.INVISIBLE
            } else {
                hideProgressOverlay()
                binding.cvProject.visibility = View.VISIBLE
            }
            loading = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}