package com.seven.colink.ui.sign.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seven.colink.R
import com.seven.colink.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}