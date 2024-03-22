package com.seven.colink.ui.promotion.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentProductPromotionBinding
import com.seven.colink.ui.promotion.adapter.ProductPromotionViewAdapter
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.ui.promotion.viewmodel.ProductPromotionViewViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductPromotionFragment : Fragment() {

    private var _binding : FragmentProductPromotionBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var viewAdapter : ProductPromotionViewAdapter
    private val promotionViewModel : ProductPromotionViewViewModel by viewModels()
    private var key : String? = null
//    private lateinit var viewList : MutableList<ProductPromotionItems>
    private var viewList = mutableListOf(
        ProductPromotionItems.Img(""),
        ProductPromotionItems.Title("","",""),
        ProductPromotionItems.MiddleImg(""),
        ProductPromotionItems.Link("","",""),
        ProductPromotionItems.ProjectHeader(""),
        ProductPromotionItems.ProjectLeaderHeader(""),
        ProductPromotionItems.ProjectLeaderItem(null),
        ProductPromotionItems.ProjectMemberHeader(""),
        ProductPromotionItems.ProjectMember(null)
    )
    private var loading = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(Constants.EXTRA_ENTITY_KEY)
            Log.d("FragView","#aaa arguments key = $key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductPromotionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserve()
    }

    private fun initView() {
        key?.let { promotionViewModel.initProduct(it) }
        Log.d("FragView","#aaa key = $key")
        viewAdapter = ProductPromotionViewAdapter(mContext)
        binding.rvPromotion.adapter = viewAdapter
        binding.rvPromotion.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setObserve() {
        promotionViewModel.product.observe(viewLifecycleOwner) {
            viewList = promotionViewModel.getViewList()
            Log.d("FragView","#aaa observe = $viewList")
            viewAdapter.submitList(viewList)
            viewAdapter.notifyDataSetChanged()
//            promotionViewModel.getViewList().let { items ->
//                viewList = mutableListOf()
//                viewList.clear()
//                viewList.addAll(items)
//                viewAdapter = ProductPromotionViewAdapter(mContext)
//                binding.rvPromotion.adapter = viewAdapter
//                binding.rvPromotion.layoutManager = LinearLayoutManager(requireContext())
//                viewAdapter.submitList(items)
//                viewAdapter.notifyDataSetChanged()
//            }
        }

        promotionViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showProgressOverlay()
                binding.rvPromotion.visibility = View.INVISIBLE
            }else {
                hideProgressOverlay()
                binding.rvPromotion.visibility = View.VISIBLE
            }
            loading = !isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}