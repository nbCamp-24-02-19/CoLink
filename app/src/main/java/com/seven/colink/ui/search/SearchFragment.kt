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
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import com.seven.colink.ui.evaluation.EvaluationActivity
import com.seven.colink.ui.post.PostActivity
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fbSearchPost.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            startActivity(intent)
        }

        binding.ivSearchButton.setOnClickListener {
            searchViewModel.doSearch(binding.etSearchSearch.text.toString())
            hideKeyboard()
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
                getGroupColor(binding.tvSearchProject)
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
                if(!study && project){
                    searchViewModel.setProjectFilter(query)
                } else {
                    searchViewModel.setGroupNone(query)
                }
            } else {
                study = true
                getGroupColor(binding.tvSearchStudy)
                if (study && !project){
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
                if (!recruitEnd && recruit){
                    searchViewModel.setRecruitFilter(query)
                } else {
                    searchViewModel.setRecruitNone(query)
                }
            } else {
                recruitEnd = true
                getRecruitColor(binding.tvSearchRecruitEnd)
                if (recruitEnd && !recruit){
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
                if (!recruit && recruitEnd){
                    searchViewModel.setRecruitEndFilter(query)
                } else {
                    searchViewModel.setRecruitNone(query)
                }
            } else {
                recruit = true
                getRecruitColor(binding.tvSearchRecruit)
                if (recruit && !recruitEnd){
                    searchViewModel.setRecruitFilter(query)
                } else {
                    searchViewModel.setRecruitBoth(query)
                }
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
        Log.d("Search", "postList = $postList}")
    }

    private fun setObserve() {
        searchViewModel.searchModel.observe(viewLifecycleOwner) {
            searchAdapter.mItems.clear()
            searchAdapter.mItems.addAll(it)
            searchAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goDetail() {
        searchAdapter.itemClick = object : SearchAdapter.ItemClick {
            override fun onClick(item: SearchModel, position: Int) {
                // PostActivity -> 상세 페이지로 바꿔야 함
                val intent = Intent(requireContext(), PostActivity::class.java)
                intent.putExtra("DetailPage", item.key)
                startActivity(intent)
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun offColor(text: TextView){
        text.setTextColor(Color.parseColor("#717171"))
    }

    private fun getGroupColor(text: TextView){
        text.setTextColor(Color.parseColor("#64B5F6"))
    }
    private fun getRecruitColor(text: TextView){
        text.setTextColor(Color.parseColor("#17B397"))
    }


}