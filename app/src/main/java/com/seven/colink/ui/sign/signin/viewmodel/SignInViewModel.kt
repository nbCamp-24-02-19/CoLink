package com.seven.colink.ui.sign.signin.viewmodel

import androidx.lifecycle.ViewModel
import com.seven.colink.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
):ViewModel() {

}