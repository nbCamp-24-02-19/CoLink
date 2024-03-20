package com.seven.colink.ui.sign.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.seven.colink.R
import com.seven.colink.databinding.ActivitySignUpBinding
import com.seven.colink.ui.sign.signup.adater.SignUpProfileAdapter
import com.seven.colink.ui.sign.signup.adater.SkillAdapter
import com.seven.colink.ui.sign.signup.type.SignUpEntryType
import com.seven.colink.ui.sign.signup.type.SignUpUIState
import com.seven.colink.ui.sign.signup.valid.SignUpErrorMessage
import com.seven.colink.ui.sign.signup.viewmodel.SignUpViewModel
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_ENTRY_TYPE = "extra_entry_type"
        const val EXTRA_USER_ENTITY = "extra_user_entity"

        fun newIntent(
            context: Context,
            entryType: SignUpEntryType,
        ) = Intent(
            context, SignUpActivity()::class.java
        ).apply {
            putExtra(EXTRA_ENTRY_TYPE, entryType)
        }
    }

    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private val viewModel: SignUpViewModel by viewModels()

    private val adapter: SignUpProfileAdapter by lazy {
        SignUpProfileAdapter(
            skillAdapter = skillAdapter,
            onPlusSkill = {
                          viewModel.addSkill(it)
            },
            onClickEnd = this::onClickEnd,
        )
    }

    private val skillAdapter: SkillAdapter by lazy {
        SkillAdapter(
            onClickItem = { text ->
                viewModel.removeSkill(text)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViewModel()
    }
    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            combine(userModel, uiStatus){ userModel, uiStatus ->
                Pair(userModel, uiStatus)
            }.collect { (userModel, uiStatus) ->
                with(binding) {
                    if (uiStatus == SignUpUIState.NAME) etSignUpEdit1.setText(userModel.name)
                    else etSignUpEdit1.setText(userModel.password)
                    val parts = userModel.email?.split("@")
                    etSignUpEmailId.setText(parts?.get(0) ?: "")
                    etSignUpEmailService.setText(parts?.get(1) ?: "")
                    etSignUpPasswordCheck.setText(userModel.password)
                }
            }
        }

        lifecycleScope.launch {
            entryType.collect {
                when (it) {
                    SignUpEntryType.CREATE -> updateUiState(SignUpUIState.NAME)
                    SignUpEntryType.UPDATE_PROFILE -> updateUiState(SignUpUIState.PROFILE)
                    SignUpEntryType.UPDATE_PASSWORD -> updateUiState(SignUpUIState.PASSWORD)
                }
            }
        }

        lifecycleScope.launch {
            uiStatus.collect { uiStatus ->
                setUi(uiStatus)
                this@SignUpActivity.hideProgressOverlay()
            }
        }

        lifecycleScope.launch {
            errorMessage.collect {
                with(binding) {
                    when (it) {
                        SignUpErrorMessage.EMAIL, SignUpErrorMessage.DUPLICATE_EMAIL -> {
                            tvSignUpSubtitle.setText(it.message)
                            tvSignUpSubtitle.setTextColor(this@SignUpActivity.getColor(R.color.red))
                            tilSignUpEmailId.boxStrokeColor =
                                ContextCompat.getColor(this@SignUpActivity, R.color.red)
                            tilSignUpEmailService.boxStrokeColor =
                                ContextCompat.getColor(this@SignUpActivity, R.color.red)
                        }

                        SignUpErrorMessage.PASSWORD_PASSWORD -> {
                            tvSignUpPasswordCheck.setText(it.message)
                            tvSignUpPasswordCheck.setTextColor(this@SignUpActivity.getColor(R.color.red))
                            tilSignUpPasswordCheck.boxStrokeColor =
                                ContextCompat.getColor(this@SignUpActivity, R.color.red)
                        }

                        SignUpErrorMessage.PASS -> {
                            etSignUpEdit1.editableText?.clear()
                        }

                        SignUpErrorMessage.SPECIALTY, SignUpErrorMessage.SKILL, SignUpErrorMessage.LEVEL -> {
                            Toast.makeText(
                                this@SignUpActivity,
                                "${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        SignUpErrorMessage.DUMMY -> Unit

                        else -> {
                            tvSignUpSubtitle.setText(it.message)
                            tvSignUpSubtitle.setTextColor(this@SignUpActivity.getColor(R.color.red))
                            tilSignUpEdit1.boxStrokeColor =
                                ContextCompat.getColor(this@SignUpActivity, R.color.red)
                        }
                    }
                }
                this@SignUpActivity.hideProgressOverlay()
            }
        }

        lifecycleScope.launch {
            viewModel.registrationResult.collect{
                this@SignUpActivity.hideProgressOverlay()
                if (it == "등록 성공") {
                    binding.root.setSnackBar(SnackType.Success, it)
                    finish()
                } else {
                    binding.root.setSnackBar(SnackType.Error, it)
                }
            }
        }

        lifecycleScope.launch {
            profileItem.collect {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.skills.collect{
                skillAdapter.submitList(it)
            }
        }
    }
    private fun setUi(
        state: SignUpUIState,
    ) = with(binding) {
        configureVisibility(state)
        setButton(state)
        setTextChangeListener(state)

        tvSignUpTitle.setText(state.title)
        tvSignUpSubtitle.setText(state.subTitle)
        tvSignUpValid.setText(state.valid)
        tvSignUpSubtitle.setTextColor(this@SignUpActivity.getColor(R.color.black))
        tilSignUpEdit1.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.black)
    }

    private fun configureVisibility(state: SignUpUIState) = with(binding) {
        val isEmail = state == SignUpUIState.EMAIL
        val isPassword = state == SignUpUIState.PASSWORD
        val isProfile = state == SignUpUIState.PROFILE

        tilSignUpEdit1.isVisible = !isEmail
        llSignUpEmail.isVisible = isEmail

        if (isPassword) {
            tilSignUpEdit1.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            etSignUpEdit1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        tvSignUpPasswordCheck.isVisible = isPassword
        tilSignUpPasswordCheck.isVisible = isPassword
        clSignUpVisible.isVisible = !isProfile
        rcSignUpList.isVisible = isProfile
        if (isProfile) setupProfileList()
    }

    private fun setupProfileList() = with(binding.rcSignUpList) {
        layoutManager = LinearLayoutManager(context)
        adapter = this@SignUpActivity.adapter
    }
    private fun setButton(state: SignUpUIState) = with(binding){
        btSignUpBtn.setOnClickListener {
            this@SignUpActivity.showProgressOverlay()
            it.isEnabled = false
            when(state) {
                SignUpUIState.EMAIL -> viewModel.checkValid(state, etSignUpEmailId.text.toString(), etSignUpEmailService.text.toString())
                SignUpUIState.PASSWORD -> viewModel.checkValid(state, etSignUpEdit1.text.toString(), etSignUpPasswordCheck.text.toString())
                else -> viewModel.checkValid(state, etSignUpEdit1.text.toString())
            }
        }

        ivSignUpBack.setOnClickListener {
            when(state) {
                SignUpUIState.NAME -> finish()
                else -> viewModel.backState(state)
            }
        }
    }
    private fun setTextChangeListener(state: SignUpUIState) = with(binding){
        etSignUpEdit1.addTextChangedListener {
            if (state == SignUpUIState.NAME) btSignUpBtn.isEnabled = (it?.length ?: 0) >= 1
            else btSignUpBtn.isEnabled = (it?.length ?: 0) >= 6
            tvSignUpTitle.setText(state.title)
            tvSignUpSubtitle.setText(state.subTitle)
            tvSignUpSubtitle.setTextColor(this@SignUpActivity.getColor(R.color.black))
            tilSignUpEdit1.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.main_color)
        }

        etSignUpPasswordCheck.addTextChangedListener {
            btSignUpBtn.isEnabled = (it?.length ?: 0) >= 6
            tvSignUpTitle.setText(state.title)
            tvSignUpSubtitle.setText(state.subTitle)
            tvSignUpPasswordCheck.setText(R.string.sign_up_password_check)
            tvSignUpPasswordCheck.setTextColor(this@SignUpActivity.getColor(R.color.black))
            tilSignUpPasswordCheck.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.main_color)
        }

        etSignUpEmailId.addTextChangedListener {
            val isEmailIdValid = (it?.length ?: 0) >= 4
            val isEmailServiceValid = (etSignUpEmailService.editableText?.length ?: 0) >= 4
            btSignUpBtn.isEnabled = isEmailIdValid && isEmailServiceValid
            tvSignUpTitle.setText(state.title)
            tvSignUpSubtitle.setText(state.subTitle)
            tvSignUpSubtitle.setTextColor(this@SignUpActivity.getColor(R.color.black))
            tilSignUpEmailId.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.main_color)
        }

        etSignUpEmailService.addTextChangedListener {
            val isEmailIdValid = (it?.length ?: 0) >= 4
            val isEmailServiceValid = (etSignUpEmailId.editableText?.length ?: 0) >= 4
            btSignUpBtn.isEnabled = isEmailIdValid && isEmailServiceValid
            tvSignUpTitle.setText(state.title)
            tvSignUpSubtitle.setText(state.subTitle)
            tvSignUpSubtitle.setTextColor(this@SignUpActivity.getColor(R.color.black))
            tilSignUpEmailService.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.main_color)
        }
    }

    private fun onClickEnd(map: Map<String, Any?>) {
        this.showProgressOverlay()
        viewModel.checkValid(map)
    }
}
