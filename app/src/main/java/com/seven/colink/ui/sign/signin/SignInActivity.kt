package com.seven.colink.ui.sign.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seven.colink.R
import com.seven.colink.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}