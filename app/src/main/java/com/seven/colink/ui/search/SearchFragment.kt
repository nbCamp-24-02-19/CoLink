package com.seven.colink.ui.search

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.seven.colink.R
import com.seven.colink.R.color.main_color
import com.seven.colink.R.color.white
import com.seven.colink.databinding.FragmentSearchBinding
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.util.convert.onThrottleClick
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter

    private var postList = mutableListOf<SearchModel>()

    private lateinit var searchViewModel: SearchViewModel

    private var project = true

    private var study = true

    private var recruit = true

    private var recruitEnd = true

    private val groupTypeOptions: List<String>
        get() = listOf(
            getString(R.string.project_kor),
            getString(R.string.study_kor)
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchViewModel.checkLogin.observe(viewLifecycleOwner) {
            binding.fbSearchPost.setOnClickListener {
                searchViewModel.getCurrentUser()
                if (searchViewModel.checkLogin.value == true) {
                    groupTypeOptions.setDialog(
                        requireContext(),
                        getString(R.string.group_type_options)
                    ) { selectedOption ->
                        when (selectedOption) {
                            getString(R.string.project_kor) -> {
                                startActivity(
                                    PostActivity.newIntent(
                                        requireActivity(),
                                        GroupType.PROJECT
                                    )
                                )
                            }

                            getString(R.string.study_kor) -> {
                                startActivity(
                                    PostActivity.newIntent(
                                        requireActivity(),
                                        GroupType.STUDY
                                    )
                                )
                            }

                            else -> Unit
                        }
                    }.show()
                } else {
                    requireContext().setDialog(
                        title = "로그인 필요",
                        message = "서비스를 이용하기 위해서는 로그인이 필요합니다. \n로그인 페이지로 이동하시겠습니까?",
                        confirmAction = {
                            val intent = Intent(requireContext(), SignInActivity::class.java)
                            startActivity(intent)
                            it.dismiss()
                        },
                        cancelAction = { it.dismiss() }
                    ).show()
                }
            }
        }

        binding.ivSearchButton.onThrottleClick({
            searchViewModel.doSearch(binding.etSearchSearch.text.toString())
            hideKeyboard()
        }, 1000)


        // 프로젝트 필터버튼
        binding.tvSearchProject.onThrottleClick {
            binding.etSearchSearch.text.toString().let { query ->
                if (project) {
                    project = false
                    offColor(binding.tvSearchProject)
                    if (!project && study) {
                        searchViewModel.setStudyFilter(query)
                    } else {
                        searchViewModel.setGroupNone(query)
                    }
                } else {
                    project = true
                    onColor(binding.tvSearchProject)
                    if (project && !study) {
                        searchViewModel.setProjectFilter(query)
                    } else {
                        searchViewModel.setGroupBoth(query)
                    }
                }
            }
        }

        // 스터디 필터버튼
        binding.tvSearchStudy.onThrottleClick {
            binding.etSearchSearch.text.toString().let { query ->
                if (study) {
                    study = false
                    offColor(binding.tvSearchStudy)
                    if (!study && project) {
                        searchViewModel.setProjectFilter(query)
                    } else {
                        searchViewModel.setGroupNone(query)
                    }
                } else {
                    study = true
                    onColor(binding.tvSearchStudy)
                    if (study && !project) {
                        searchViewModel.setStudyFilter(query)
                    } else {
                        searchViewModel.setGroupBoth(query)
                    }
                }
            }
        }

        // 모집완료 필터버튼
        binding.tvSearchRecruitEnd.onThrottleClick {
            binding.etSearchSearch.text.toString().let { query ->
                if (recruitEnd) {
                    recruitEnd = false
                    offColor(binding.tvSearchRecruitEnd)
                    if (!recruitEnd && recruit) {
                        searchViewModel.setRecruitFilter(query)
                        Log.d("123", "setRecruitFilter")
                    } else {
                        searchViewModel.setRecruitNone(query)
//                        searchViewModel.setRecruitEndFilter(query)
                        Log.d("123", "setRecruitNone")
                    }
                } else {
                    recruitEnd = true
                    onColor(binding.tvSearchRecruitEnd)
                    if (recruitEnd && !recruit) {
                        searchViewModel.setRecruitEndFilter(query)
                        Log.d("123", "setRecruitEndFilter")
                    } else {
                        searchViewModel.setRecruitBoth(query)
                        Log.d("123", "setRecruitBoth")
                    }
                }
                Log.d("123", "123 recruit, recruitEnd = $recruit, $recruitEnd")
            }
        }

        // 모집중 필터버튼
        binding.tvSearchRecruit.onThrottleClick {
            binding.etSearchSearch.text.toString().let { query ->
                if (recruit) {
                    recruit = false
                    offColor(binding.tvSearchRecruit)
                    if (!recruit && recruitEnd) {
                        searchViewModel.setRecruitEndFilter(query)
                        Log.d("123", "setRecruitEndFilter")
                    } else {
                        searchViewModel.setRecruitNone(query)
                        Log.d("123", "setRecruitNone")
                    }
                } else {
                    recruit = true
                    onColor(binding.tvSearchRecruit)
                    if (recruit && !recruitEnd) {
                        searchViewModel.setRecruitFilter(query)
                        Log.d("123", "setRecruitFilter")
                    } else {
                        searchViewModel.setRecruitBoth(query)
                        Log.d("123", "setRecruitBoth")
                    }
                }
                Log.d("123", "456 recruit, recruitEnd = $recruit, $recruitEnd")
            }
        }

        initRecyclerView()
        setObserve()
        goDetail()

        return root
    }

    private fun initRecyclerView() {
        searchAdapter = SearchAdapter(postList)
        binding.rvSearchRecyclerView.adapter = searchAdapter
        binding.rvSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchRecyclerView.itemAnimator = null
    }

    private fun setObserve() {
        searchViewModel.searchModel.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    if (study.not() && project.not()) {
                        showEmpty()
                    } else if (recruit.not() && recruitEnd.not()) {
                        showEmpty()
                    } else {
                        showProgressOverlay()
                    }
                }

                is UiState.Success -> {
                    hideProgressOverlay()
                    binding.clSearchEmpty.isVisible = state.data.isNullOrEmpty()
                    binding.rvSearchRecyclerView.visibility = View.VISIBLE
                    searchAdapter.mItems.clear()
                    searchAdapter.mItems.addAll(state.data)
                    searchAdapter.notifyDataSetChanged()
                }

                is UiState.Error -> {
                    hideProgressOverlay()
                    Toast.makeText(requireContext(), "${state.throwable}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setSearchAppbar()
        lifecycleScope.launch {
            searchViewModel.doSearch("")
        }
    }

    override fun onPause() {
        super.onPause()
        returnAppbar()
    }

    private fun setSearchAppbar() {
        activity?.findViewById<ImageView>(R.id.iv_main_toolbar_image)
            ?.setImageResource(R.drawable.logo_co_link_search)
        activity?.findViewById<AppBarLayout>(R.id.al_main_appbar)?.elevation = 0f
        activity?.findViewById<AppBarLayout>(R.id.al_main_appbar)?.setBackgroundResource(main_color)
    }

    private fun returnAppbar() {
        activity?.findViewById<ImageView>(R.id.iv_main_toolbar_image)
            ?.setImageResource(R.drawable.logo_co_link)
        activity?.findViewById<AppBarLayout>(R.id.al_main_appbar)?.elevation = 5f
        activity?.findViewById<AppBarLayout>(R.id.al_main_appbar)?.setBackgroundResource(white)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        hideProgressOverlay()
    }

    private fun goDetail() {
        searchAdapter.itemClick = object : SearchAdapter.ItemClick {
            override fun onClick(item: SearchModel, position: Int) {
                lifecycleScope.launch {
                    startActivity(
                        PostActivity.newIntent(
                            context = requireActivity(),
                            key = item.key
                        )
                    )
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun offColor(text: TextView) {
        text.setTextColor(Color.parseColor("#20816F"))
    }

    private fun onColor(text: TextView) {
        text.setTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun showEmpty() {
        searchAdapter.mItems.clear()
        binding.rvSearchRecyclerView.visibility = View.INVISIBLE
        binding.clSearchEmpty.visibility = View.VISIBLE
    }
}