package com.seven.colink.ui.promotion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentProductPromotionEditBinding
import com.seven.colink.domain.entity.ProductEntity
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
        clickComplete()
        clickBackButton()
    }

    private fun initView() {
        viewList.clear()
        viewList.addAll(listOf(
            ProductPromotionItems.Img(null),
            ProductPromotionItems.Title("","",""),
            ProductPromotionItems.MiddleImg(null),
            ProductPromotionItems.Link("","",""),
            ProductPromotionItems.ProjectHeader(""),
            ProductPromotionItems.ProjectLeaderHeader(""),
            ProductPromotionItems.ProjectLeaderItem(null),
            ProductPromotionItems.ProjectMemberHeader(""),
            ProductPromotionItems.ProjectMember(null)
        ))
        editAdapter = ProductPromotionEditAdapter(binding.rvPromotionEdit,viewList)
        binding.rvPromotionEdit.adapter = editAdapter
        binding.rvPromotionEdit.layoutManager = LinearLayoutManager(requireContext())
        setObserve()
    }

    private fun setObserve(){
        key?.let { editViewModel.getMemberDetail(it) }
        editViewModel.product.observe(viewLifecycleOwner) {
            key?.let { key -> editViewModel.init(key) }
        }

        editViewModel.setLeader.observe(viewLifecycleOwner) { leader ->
            editAdapter.setLeader(leader)
//            editViewModel.entity = ProductEntity(authId = leader.userInfo?.getOrNull()?.uid)
        }

        editViewModel.setMember.observe(viewLifecycleOwner) { memberItem ->
            editAdapter.setMember(memberItem)
        }
    }

    private fun clickComplete(){
        binding.tvPromotionEditComplete.visibility = View.VISIBLE
        binding.tvPromotionEditComplete.setTextColor(ContextCompat.getColor(requireContext(),R.color.forth_color))
        binding.tvPromotionEditComplete.text = "완료"
        binding.tvPromotionEditComplete.setOnClickListener {
            saveDataToViewModel()
        }
    }

    private fun saveDataToViewModel(){
        val mainImg = editAdapter.getMainImageView(0)?.toString()
        val titleEdit = editAdapter.getTitleEditText(1)?.let {
            if (it.text.isNotEmpty()) it.text.toString() else ""
        }
        val desEdit = editAdapter.getDesEditText(1)?.let {
            if (it.text.isNotEmpty()) it.text.toString() else ""
        }
        val middleImg = editAdapter.getMiddleImageView(2)?.toString()
        val webEdit = editAdapter.getWebLink(3)?.text.toString()
        val aosEdit = editAdapter.getAosLink(3)?.text.toString()
        val iosEdit = editAdapter.getIosLink(3)?.text.toString()

        with(editViewModel) {
            saveTitleAndDes(titleEdit,desEdit)
            Log.d("Frag","#bbb title = $titleEdit")
            Log.d("Frag","#bbb des = $desEdit")
            saveImgUrl(mainImg.toString(),middleImg.toString())
//            saveDes(desEdit)
//            Log.d("Frag","#bbb title = $titleEdit")
//            Log.d("Frag","#bbb des = $desEdit")
//            saveTitle(titleEdit)
//            saveDesImg(middleImg)
//            saveMainImg(mainImg)
            Log.d("Frag","#bbb main = $mainImg")
            Log.d("Frag","#bbb middle = $middleImg")
            saveLink(webEdit,aosEdit,iosEdit)
            Log.d("Frag","#bbb web = $webEdit")
            Log.d("Frag","#bbb aos = $aosEdit")
            Log.d("Frag","#bbb ios = $iosEdit")
            registerProduct()
        }
    }

    private fun clickBackButton() {
        binding.ivPromotionEditFinish.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}