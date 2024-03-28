package com.seven.colink.ui.promotion.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentProductPromotionBinding
import com.seven.colink.ui.promotion.adapter.ProductPromotionViewAdapter
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.ui.promotion.viewmodel.ProductPromotionSharedViewModel
import com.seven.colink.ui.promotion.viewmodel.ProductPromotionViewViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductPromotionFragment : Fragment() {

    private var _binding : FragmentProductPromotionBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var viewAdapter : ProductPromotionViewAdapter
    private val promotionViewModel : ProductPromotionViewViewModel by viewModels()
    private val sharedViewModel : ProductPromotionSharedViewModel by activityViewModels()
    private var key : String? = null
    private var viewList = mutableListOf(
        ProductPromotionItems.Img(""),
        ProductPromotionItems.Title("","","","","","",""),
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
        clickBackButton()
    }

    private fun initView() {
        key?.let { promotionViewModel.initProduct(it) }
        viewAdapter = ProductPromotionViewAdapter(mContext)
        binding.rvPromotion.adapter = viewAdapter
        binding.rvPromotion.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun clickBackButton() {
        binding.ivPromotionFinish.setOnClickListener {
            activity?.finish()
        }
    }

    private fun setObserve() {
        lifecycleScope.launch {
            sharedViewModel.key.collect { k ->
                if (k != null) {
                    sharedViewModel.setKey(k)
                    promotionViewModel.initProduct(k)
                }
            }
        }

        promotionViewModel.key.observe(viewLifecycleOwner) { k ->
            if (k != null) {
                sharedViewModel.setKey(k)
            }
        }

        promotionViewModel.product.observe(viewLifecycleOwner) {
            viewList = promotionViewModel.getViewList()
            viewAdapter.submitList(viewList)
            viewAdapter.notifyDataSetChanged()
            promotionViewModel.getIdCompareAuthId()
        }

        promotionViewModel.result.observe(viewLifecycleOwner) { result ->
            if (result == DataResultStatus.SUCCESS) {
                setButton()
            }else {
                binding.tvPromotionEdit.visibility = View.INVISIBLE
            }
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

    private fun setButton() {
        binding.tvPromotionEdit.visibility = View.VISIBLE
        binding.tvPromotionEdit.setTextColor(ContextCompat.getColor(requireContext(),R.color.forth_color))
        binding.tvPromotionEdit.text = getText(R.string.edit)

        binding.tvPromotionEdit.setOnClickListener {
            promotionViewModel.setProductKey()
            val frag = ProductPromotionEditFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val trans = fragmentManager.beginTransaction()
            trans.replace(R.id.frame_product_promotion,frag)
            trans.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}