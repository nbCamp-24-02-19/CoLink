package com.seven.colink.ui.sign.signin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.seven.colink.databinding.ActivitySignInBinding
import com.seven.colink.ui.main.MainActivity
import com.seven.colink.ui.sign.signin.viewmodel.SignInViewModel
import com.seven.colink.ui.sign.signup.SignUpActivity
import com.seven.colink.util.convert.convertError
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    private val viewModel: SignInViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            entryType.collect {
                if (it) {
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            result.collect{
                when(it) {
                    DataResultStatus.SUCCESS -> binding.tvSignError.isVisible = false
                    DataResultStatus.FAIL -> {
                        with(binding.tvSignError) {
                            isVisible = true
                            text = it.message.convertError()
                        }
                    }
                }
                this@SignInActivity.hideProgressOverlay()
                viewModel.isSignIn(it)
            }
        }
    }

    private fun initView() {
        setButton()
    }

    private fun setButton() = with(binding) {
        ivSignInBack.setOnClickListener {
            finish()
        }
        btSignInLogin.setOnClickListener {
            showProgressOverlay()
            lifecycleScope.launch {
                viewModel.signIn(etSignInId.text.toString(), etSignInPassword.text.toString())
            }
        }

        btSignInRegister.setOnClickListener {
            startActivity(
                Intent(
                    this@SignInActivity,
                    SignUpActivity::class.java
                )
            )
        }
    }
}
