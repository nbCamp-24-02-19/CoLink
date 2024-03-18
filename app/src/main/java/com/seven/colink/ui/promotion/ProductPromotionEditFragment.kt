package com.seven.colink.ui.promotion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.FragmentProductPromotionEditBinding
import com.seven.colink.ui.promotion.adapter.ProductPromotionEditAdapter
import com.seven.colink.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductPromotionEditFragment : Fragment() {

    private var _binding : FragmentProductPromotionEditBinding? = null
    private val binding get() = _binding!!
//    private lateinit var mContext: Context
//    private val editAdapter by lazy { ProductPromotionEditAdapter() }
    private lateinit var editAdapter : ProductPromotionEditAdapter
    private val editViewModel : ProductPromotionEditViewModel by viewModels()
    private var key : String? = null
    private var viewList = mutableListOf(
        ProductPromotionItems.Img(null),
        ProductPromotionItems.Title("","",""),
        ProductPromotionItems.MiddleImg(null),
        ProductPromotionItems.Link("","",""),
        ProductPromotionItems.ProjectHeader("dd"),
        ProductPromotionItems.ProjectLeaderHeader(""),
        ProductPromotionItems.ProjectLeaderItem(null),
        ProductPromotionItems.ProjectMemberHeader(""),
        ProductPromotionItems.ProjectMember(null)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(Constants.EXTRA_ENTITY_KEY)
            Log.d("Edit","key = $key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductPromotionEditBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Edit","Edit으로 뜨니?")
        initView()
    }

    private fun initView() {
        viewList.clear()
        viewList.addAll(listOf(
            ProductPromotionItems.Img(null),
            ProductPromotionItems.Title(editViewModel.entity?.title,editViewModel.entity?.registeredDate,editViewModel.entity?.description),
            ProductPromotionItems.MiddleImg(null),
            ProductPromotionItems.Link("","",""),
            ProductPromotionItems.ProjectHeader("dd"),
            ProductPromotionItems.ProjectLeaderHeader(""),
            ProductPromotionItems.ProjectLeaderItem(null),
            ProductPromotionItems.ProjectMemberHeader(""),
            ProductPromotionItems.ProjectMember(null)
        ))
        editAdapter = ProductPromotionEditAdapter(viewList)
        binding.rvPromotionEdit.adapter = editAdapter
        binding.rvPromotionEdit.layoutManager = LinearLayoutManager(requireContext())
        key?.let { editViewModel.init(it) }
        setObserve()
    }

    private fun setObserve(){
        key?.let { editViewModel.getMemberDetail(it) }
        editViewModel.product.observe(viewLifecycleOwner) {
            key?.let { key -> editViewModel.init(key) }
        }

        editViewModel.setLeader.observe(viewLifecycleOwner) { leader ->
            editAdapter.setLeader(leader)
        }

        editViewModel.setMember.observe(viewLifecycleOwner) { memberItem ->
            editAdapter.setMember(memberItem)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}