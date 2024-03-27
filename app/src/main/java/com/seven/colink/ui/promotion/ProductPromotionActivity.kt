package com.seven.colink.ui.promotion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.seven.colink.R
import com.seven.colink.databinding.ActivityProductPromotionBinding
import com.seven.colink.ui.promotion.ui.ProductPromotionEditFragment
import com.seven.colink.ui.promotion.ui.ProductPromotionFragment
import com.seven.colink.ui.promotion.viewmodel.ProductPromotionSharedViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductPromotionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityProductPromotionBinding.inflate(layoutInflater) }
    private val viewModel : ProductPromotionSharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initObserve()
    }

    private fun initView() = with(binding) {
        val receiveKey = intent.getStringExtra(Constants.EXTRA_ENTITY_KEY)
        if (receiveKey != null) {
            if (receiveKey.startsWith("PRD_")){
                val frag = ProductPromotionFragment()
                val bundle = Bundle()
                bundle.putString(Constants.EXTRA_ENTITY_KEY,receiveKey)
                frag.arguments = bundle
                setFragment(frag)
            }else{
                val frag = ProductPromotionEditFragment()
                val bundle = Bundle()
                bundle.putString(Constants.EXTRA_ENTITY_KEY,receiveKey)
                frag.arguments = bundle
                setFragment(frag)
            }
        }
    }

    private fun initObserve() = with(viewModel) {
        clickSnackBar.observe(this@ProductPromotionActivity) {
            when (it) {
                Constants.SAVE_SUCCESS -> {binding.root.setSnackBar(SnackType.Success,getString(R.string.product_success)).show()}
                Constants.SAVE_FAIL -> {binding.root.setSnackBar(SnackType.Error,getString(R.string.product_fail)).show()}
                else -> Unit
            }
        }
    }

    private fun setFragment(frag : Fragment) {
        val tran = supportFragmentManager.beginTransaction()
        tran.replace(R.id.frame_product_promotion, frag)
        tran.commit()
    }
}