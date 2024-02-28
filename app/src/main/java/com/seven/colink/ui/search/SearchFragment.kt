package com.seven.colink.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.FragmentSearchBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.ui.evaluation.EvaluationActivity

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchAdapter
    private var postList = mutableListOf<PostEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val searchViewModel =
//            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        searchViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        // (다면평가 확인용) 팹버튼 클릭 시 다면평가로 가게 해둠
        binding.fbSearchPost.setOnClickListener {
            val intent = Intent(requireContext(),EvaluationActivity::class.java)
            startActivity(intent)
        }
        initRecyclerView()

        return root
    }

    private fun initRecyclerView(){
        searchAdapter = SearchAdapter(postList)
        binding.rvSearchRecyclerView.adapter = searchAdapter
        binding.rvSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d("Search","postList = ${postList}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}