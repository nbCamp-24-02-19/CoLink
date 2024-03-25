package com.seven.colink.ui.promotion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.seven.colink.R
import com.seven.colink.databinding.ActivityProductPromotionBinding
import com.seven.colink.ui.promotion.ui.ProductPromotionEditFragment
import com.seven.colink.ui.promotion.ui.ProductPromotionFragment
import com.seven.colink.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductPromotionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityProductPromotionBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
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

    private fun setFragment(frag : Fragment) {
        val tran = supportFragmentManager.beginTransaction()
        tran.replace(R.id.frame_product_promotion, frag)
        tran.addToBackStack("")
        tran.commit()
    }
}