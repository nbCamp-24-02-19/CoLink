package com.seven.colink.ui.sign.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    private val viewModel: SignInViewModel by viewModels()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private fun googleInit() {
        val webClientId = "656455146700-4it82b9v7j6q8m0fv9hms81rr8gnccka.apps.googleusercontent.com"
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                    try {
                        val account = task.getResult(ApiException::class.java)
                        Log.d(Constants.LOG_IN_TAG,"firebaseAuthWithGoogle" + account.id)
                        account.id?.let { firebaseAuthWithGoogle(it) }
                    }catch (e: ApiException) {
                        Log.e(Constants.LOG_IN_TAG,"failed",e)
                    }
                }
            }else {
                Log.e(Constants.LOG_IN_TAG,"Error $result")
            }
        }
    }

    private fun firebaseAuthWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(Constants.LOG_IN_TAG,"success")
                    val user = firebaseAuth.currentUser
                    //이제 처리를 뷰모델에서 만들어야?

                }else {
                    Log.e(Constants.LOG_IN_TAG,"failure",task.exception)
                    //실패시 할 처리

                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
        googleInit()
    }

    override fun onStart() {
        viewModel.signInCheck()
        super.onStart()
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

        btSignInGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

}
