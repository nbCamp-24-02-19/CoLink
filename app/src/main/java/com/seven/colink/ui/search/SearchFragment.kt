package com.seven.colink.ui.search

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentSearchBinding
import com.seven.colink.ui.evaluation.EvaluationActivity
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.sign.signin.SignInActivity
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

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

        binding.ivSearchButton.setOnClickListener {
//            searchViewModel.doSearch(binding.etSearchSearch.text.toString())
//            hideKeyboard()
            startActivity(
                EvaluationActivity.newIntentEval(
                    requireContext(),
                    GroupType.PROJECT,
                    "ca4d315f-bbf8-420a-b997-a47b3f9a0fbb"
                )
            )
        }


        val query = binding.etSearchSearch.text.toString()

        // 프로젝트 필터버튼
        binding.tvSearchProject.setOnClickListener {
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
                getProjectColor(binding.tvSearchProject)
                if (project && !study) {
                    searchViewModel.setProjectFilter(query)
                } else {
                    searchViewModel.setGroupBoth(query)
                }
            }
        }

        // 스터디 필터버튼
        binding.tvSearchStudy.setOnClickListener {
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
                getStudyColor(binding.tvSearchStudy)
                if (study && !project) {
                    searchViewModel.setStudyFilter(query)
                } else {
                    searchViewModel.setGroupBoth(query)
                }
            }
        }

        // 모집완료 필터버튼
        binding.tvSearchRecruitEnd.setOnClickListener {
            if (recruitEnd) {
                recruitEnd = false
                offColor(binding.tvSearchRecruitEnd)
                if (!recruitEnd && recruit) {
                    searchViewModel.setRecruitFilter(query)
                } else {
                    searchViewModel.setRecruitNone(query)
                }
            } else {
                recruitEnd = true
                getRecruitEndColor(binding.tvSearchRecruitEnd)
                if (recruitEnd && !recruit) {
                    searchViewModel.setRecruitEndFilter(query)
                } else {
                    searchViewModel.setRecruitBoth(query)
                }
            }
        }

        // 모집중 필터버튼
        binding.tvSearchRecruit.setOnClickListener {
            if (recruit) {
                recruit = false
                offColor(binding.tvSearchRecruit)
                if (!recruit && recruitEnd) {
                    searchViewModel.setRecruitEndFilter(query)
                } else {
                    searchViewModel.setRecruitNone(query)
                }
            } else {
                recruit = true
                getRecruitColor(binding.tvSearchRecruit)
                if (recruit && !recruitEnd) {
                    searchViewModel.setRecruitFilter(query)
                } else {
                    searchViewModel.setRecruitBoth(query)
                }
            }
        }

        showProgressOverlay()
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
        Log.d("Search", "postList = $postList}")
    }

    private fun setObserve() {
        searchViewModel.searchModel.observe(viewLifecycleOwner) {
            searchAdapter.mItems.clear()
            searchAdapter.mItems.addAll(it)
            searchAdapter.notifyDataSetChanged()
            hideProgressOverlay()
        }
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
        text.setTextColor(Color.parseColor("#717171"))
    }

    private fun getProjectColor(text: TextView) {
        text.setTextColor(Color.parseColor("#64B5F6"))
    }

    private fun getStudyColor(text: TextView) {
        text.setTextColor(Color.parseColor("#EB8447"))
    }

    private fun getRecruitColor(text: TextView) {
        text.setTextColor(Color.parseColor("#17B397"))
    }

    private fun getRecruitEndColor(text: TextView) {
        text.setTextColor(Color.parseColor("#2F4858"))
    }


}