package com.seven.colink.ui.search

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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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