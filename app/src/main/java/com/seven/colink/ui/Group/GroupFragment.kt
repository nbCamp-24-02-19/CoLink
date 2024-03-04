package com.seven.colink.ui.Group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentGroupBinding
import com.seven.colink.ui.post.PostActivity
import com.seven.colink.ui.search.SearchFragment

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val groupViewModel =
            ViewModelProvider(this).get(GroupViewModel::class.java)

        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        goDetail()
        goSearch()
    }

    private fun initView() {

        val dataList = mutableListOf(
            GroupData.GroupTitle("참여중인 그룹"),
            GroupData.GroupList("CoLink",142,"히히..","# 안드로이드"),
            GroupData.GroupAdd("새그룹 추가하기", "지원한 그룹"),
            GroupData.GroupWant("Project","타이틀입니다","설명입니다","작성자","Lv4", R.mipmap.ic_launcher)
        )

        groupAdapter = GroupAdapter(dataList)
        binding.rvGroupRecyclerView.adapter = groupAdapter
        binding.rvGroupRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun goDetail(){
        groupAdapter.joinItemClick = object : GroupAdapter.JoinItemClick {
            override fun onClick(item: GroupData.GroupList, position: Int) {
                // PostActivity -> 상세 페이지로 바꿔야 함
                val intent = Intent(requireContext(), PostActivity::class.java)
//                intent.putExtra("DetailPage", item())
                startActivity(intent)
            }
        }
    }

    private fun goSearch(){
        groupAdapter.addItemClick = object : GroupAdapter.AddItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), SearchFragment::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}