package com.seven.colink.ui.promotion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seven.colink.databinding.ActivityProductPromotionBinding

class ProductPromotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductPromotionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}