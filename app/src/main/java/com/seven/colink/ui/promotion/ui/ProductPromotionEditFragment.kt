package com.seven.colink.ui.promotion.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.FragmentProductPromotionEditBinding
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.ui.promotion.adapter.ProductPromotionEditAdapter
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.ui.promotion.viewmodel.ProductPromotionEditViewModel
import com.seven.colink.ui.promotion.viewmodel.ProductPromotionSharedViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProductPromotionEditFragment : Fragment() {

    private var _binding : FragmentProductPromotionEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editAdapter : ProductPromotionEditAdapter
    private lateinit var getResultMainImg : ActivityResultLauncher<Intent>
    private lateinit var getResultDesImg : ActivityResultLauncher<Intent>
    private lateinit var mContext: Context
    private val editViewModel : ProductPromotionEditViewModel by viewModels()
    private val sharedViewModel : ProductPromotionSharedViewModel by activityViewModels()
    private var key : String? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var viewList = mutableListOf(
        ProductPromotionItems.Img(null),
        ProductPromotionItems.Title("","","","","","",""),
        ProductPromotionItems.MiddleImg(null),
        ProductPromotionItems.Link("","",""),
        ProductPromotionItems.ProjectHeader("dd"),
        ProductPromotionItems.ProjectLeaderHeader(""),
        ProductPromotionItems.ProjectLeaderItem(null),
        ProductPromotionItems.ProjectMemberHeader(""),
        ProductPromotionItems.ProjectMember(null)
    )

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
        _binding = FragmentProductPromotionEditBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        clickComplete()
        clickBackButton()
        initLauncher()
    }

    private fun initView() {
        editAdapter = ProductPromotionEditAdapter(mContext,binding.rvPromotionEdit,viewList)
        binding.rvPromotionEdit.adapter = editAdapter
        binding.rvPromotionEdit.layoutManager = LinearLayoutManager(requireContext())
        setButton()
        setObserve()
    }

    private fun initLauncher() {
        getResultMainImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectUri: Uri? = data?.data
                editAdapter.updateImage(selectUri)
            }
        }
        getResultDesImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectUri: Uri? = data?.data
                editAdapter.updateDesImage(selectUri)
            }
        }
        editAdapter.initResult(getResultMainImg,getResultDesImg)
    }

    private fun setObserve(){
        lifecycleScope.launch {
            sharedViewModel.key.collect { k ->
                if (k != null) {
                    editViewModel.init(k)
                }
            }
        }
        editViewModel.product.observe(viewLifecycleOwner) {
            if (key != null && key?.isNotEmpty() == true) {
                key?.let { key -> editViewModel.init(key) }
            }
            updateViewList(editViewModel.getViewList())
            editAdapter.notifyDataSetChanged()
        }

        editViewModel.setLeader.observe(viewLifecycleOwner) { leader ->
            editAdapter.setLeader(leader)
        }

        editViewModel.setMember.observe(viewLifecycleOwner) { memberItem ->
            editAdapter.setMember(memberItem)
        }

        editViewModel.result.observe(viewLifecycleOwner) { result ->
            if (result == DataResultStatus.SUCCESS) {
                sharedViewModel.clickEventSnackBar(Constants.SAVE_SUCCESS)
                val frag = ProductPromotionFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val trans = fragmentManager.beginTransaction()
                trans.replace(R.id.frame_product_promotion,frag)
                trans.commit()
            }else {
                sharedViewModel.clickEventSnackBar(Constants.SAVE_FAIL)
            }
        }
    }

    private fun updateViewList(newList : List<ProductPromotionItems>) {
        val existItem = newList.filter { newItem ->
            viewList.any { existItem ->
                existItem::class == newItem::class
            }
        }
        existItem.forEach { newItem ->
            viewList.indexOfFirst { existItem ->
                existItem::class == newItem::class
            }.takeIf { index -> index != -1 }?.let { index ->
                viewList[index] = newItem
            }
        }
    }

    private fun setButton() {
        binding.tvPromotionEditComplete.visibility = View.VISIBLE
        binding.tvPromotionEditComplete.setTextColor(ContextCompat.getColor(requireContext(),R.color.forth_color))
        binding.tvPromotionEditComplete.text = getText(R.string.bt_complete)
    }

    private fun clickComplete(){
        binding.tvPromotionEditComplete.setOnClickListener {
            editAdapter.editTextViewAllFocusOut()
            saveDataToViewModel()
        }
    }

    private fun saveDataToViewModel(){
        coroutineScope.launch {
            val tempData = editAdapter.getTempData()

            if (tempData.title != null && tempData.des != null && tempData.team != null) {
                val mainImg = withContext(Dispatchers.Main) {
                    val imgItem = editAdapter.getTempData()
                    imgItem.selectMainImgUri?.let { editViewModel.uploadImage(it) }
                }

                val desImg = withContext(Dispatchers.Main) {
                    val imgItem = editAdapter.getTempData()
                    imgItem.selectMiddleImgUri?.let { editViewModel.uploadImage(it) }
                }

                if (mainImg != null) {
                    editAdapter.tempEntity.mainImg = mainImg
                }
                if (desImg != null) {
                    editAdapter.tempEntity.desImg = desImg
                }

                if (!tempData.mainImg.isNullOrEmpty()){
                    val entity = ProductEntity(
                        title = tempData.title,
                        imageUrl = tempData.mainImg,
                        projectId = tempData.team,
                        description = tempData.des,
                        desImg = tempData.desImg,
                        referenceUrl = tempData.web,
                        aosUrl = tempData.aos,
                        iosUrl = tempData.ios
                    )

                    editViewModel.key.observe(viewLifecycleOwner) { k ->
                        sharedViewModel.setKey(k)
                    }

                    with(editViewModel) {
                        saveEntity(entity)
                        registerProduct()
                    }
                }else{
                    binding.tvPromotionEditComplete.setSnackBar(SnackType.Error,"메인 이미지는 필수로 들어가야 합니다.").show()
                }
            }else {
                binding.tvPromotionEditComplete.setSnackBar(SnackType.Error,"제목, 소개글, 팀 이름은 필수로 들어가야 합니다.").show()
            }
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