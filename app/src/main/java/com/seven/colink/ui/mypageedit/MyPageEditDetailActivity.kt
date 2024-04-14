package com.seven.colink.ui.mypageedit

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ActivityMyPageEditDetailBinding
import com.seven.colink.ui.main.MainActivity
import com.seven.colink.ui.mypageedit.model.MyPageEditModel
import com.seven.colink.ui.mypageedit.viewmodel.MyPageEditDetailViewModel
import com.seven.colink.ui.sign.signup.SignUpActivity
import com.seven.colink.ui.sign.signup.type.SignUpEntryType
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.SnackType
import com.seven.colink.util.status.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageEditDetailActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMyPageEditDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: MyPageEditDetailViewModel by viewModels()

    private val galleryResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateProfileImg(result.data?.data ?: return@registerForActivityResult)
            }
        }

    private val buttons by lazy {
        with(binding){
            listOf(
                ivMypageDetailBack,
                ivMypageEditName,
                ivMypageEditProfile,
                ivMypageDetailBack,
//                ctMypageEdit2,
                ctMypageEdit1,
                ctMypageEdit3,
                btMypageSave,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel) {
        loadUserDetails()
        lifecycleScope.launch {
            userDetail.collect { state ->
                when (state) {
                    is UiState.Loading -> showProgressOverlay()
                    is UiState.Success -> {
                        hideProgressOverlay()
                        setUi(state.data)
                    }

                    is UiState.Error -> {
                        hideProgressOverlay()
                        binding.root.setSnackBar(SnackType.Error, "저장을 실패하였습니다. ${state.throwable}").show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            uploadState.collect { state ->
                with(binding.ivMypageEditProfile) {
                    when (state) {
                        SnackType.Success -> {
                            setSnackBar(state, "성공적으로 저장되었습니다").show()
                            finish()
                        }
                        SnackType.Notice -> setSnackBar(state, "닉네임은 2글자 이상 이어야 합니다")
                        SnackType.Error -> {
                            setSnackBar(state, "갱신 실패하였습니다.").show()
                        }
                    }
                    buttons.forEach { it.isEnabled = true }
                }
            }
        }

        lifecycleScope.launch {
            deleteEvent.collect {
                if (it == DataResultStatus.SUCCESS) {
                    Toast.makeText(this@MyPageEditDetailActivity, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MyPageEditDetailActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    binding.root.setSnackBar(SnackType.Error, it.message)
                }
            }
        }
    }

    private fun setUi(model: MyPageEditModel) = with(binding) {
        ivMypageEditProfile.load(model.selectUrl ?: model.profileUrl)
        if (tvMypageEditName.text.isNullOrBlank()) tvMypageEditName.setText(
            model.name ?: return@with
        )
    }

    private fun initView() {
        setButton()
        focusListener()
        setupLayoutTouchListener()
    }

    private fun focusListener() = with(binding) {
        tvMypageEditName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                tvMypageEditName.isEnabled = false

                tvMypageEditName.setOnClickListener {
                    editName()
                }
            }
        }
    }

    private fun setButton() = with(binding) {
        ivMypageDetailBack.setOnClickListener {
            finish()
        }

        ivMypageEditProfile.setOnClickListener {
            openGallery(galleryResultLauncher)
        }

        ivMypageEditName.setOnClickListener {
            editName()
        }

        ctMypageEdit1.setOnClickListener {
            startActivity(
                SignUpActivity.newIntent(
                context = this@MyPageEditDetailActivity,
                entryType = SignUpEntryType.UPDATE_PROFILE
            ))
        }

        ctMypageEdit3.setOnClickListener {
            setDialog(
                title = "회원 탈퇴",
                message = "탈퇴시 계정을 복구 할 수 없습니다. \n 정말 탈퇴 하시겠습니까?",
                confirmAction = { viewModel.deleteUser() },
                cancelAction = { it.dismiss() }
                ).show()
        }

        btMypageSave.setOnClickListener {
            lifecycleScope.launch {
                buttons.forEach { it.isEnabled = false }
                viewModel.update(
                    tvMypageEditName.text.toString()
                )
            }
        }
    }

    private fun openGallery(galleryResultLauncher: ActivityResultLauncher<Intent>) {
        galleryResultLauncher.launch(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        )
    }

    private fun editName() = with(binding) {
        tvMypageEditName.isEnabled = true
        tvMypageEditName.requestFocus()

        tvMypageEditName.setSelection(tvMypageEditName.text.length)

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(tvMypageEditName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupLayoutTouchListener() = with(binding){
        root.setOnTouchListener { v, event ->
            if (tvMypageEditName.isFocused) {
                tvMypageEditName.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            false
        }
    }
}