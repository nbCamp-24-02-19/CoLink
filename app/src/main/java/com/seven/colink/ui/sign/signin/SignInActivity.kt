package com.seven.colink.ui.sign.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.seven.colink.BuildConfig
import com.seven.colink.databinding.ActivitySignInBinding
import com.seven.colink.ui.main.MainActivity
import com.seven.colink.ui.sign.signin.viewmodel.SignInViewModel
import com.seven.colink.ui.sign.signup.SignUpActivity
import com.seven.colink.util.Constants
import com.seven.colink.util.convert.convertError
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    companion object{
        const val TAG = "SignInActivity"
    }
    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    private val viewModel: SignInViewModel by viewModels()

    private val signInGoogleRequest = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.GOOGLE_SIGN)
        .requestEmail()
        .build()

    private val mGoogleSignInClient by lazy {
        GoogleSignIn.getClient(this, signInGoogleRequest)
    }

    private val signInResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    override fun onStart() {
        viewModel.signInCheck()
        super.onStart()
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            entryType.collect {
                if (it) {
                    Toast.makeText(this@SignInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
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

        lifecycleScope.launch {
            updateEvent.collect {
                startActivity(SignUpActivity.newIntent(this@SignInActivity, it))
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

//        btSignInKakao.setOnClickListener {
//            viewModel.getTokenByKakao()
////            kakaoLogin()
//        }

        btSignInGoogle.setOnClickListener {
            lifecycleScope.launch {
                signInResultLauncher.launch(mGoogleSignInClient.signInIntent)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.sendTokenByGoogle(account?.idToken)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

/*    private fun kakaoLogin() =
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    } else {
                        loginWithKaKaoAccount(this)
                    }
                } else if (token != null) {
                    viewModel.getTokenByKakao(token.accessToken)
                }
            }
        } else {
            loginWithKaKaoAccount(this)
        }

    private fun loginWithKaKaoAccount(context: Context) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token: OAuthToken?, error: Throwable? ->
            if (token != null) {
                viewModel.getTokenByKakao(token.accessToken)
            }
            if (error != null) {
                Log.d(TAG, "$error")
            }
        }
    }*/

}
