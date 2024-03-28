package com.seven.colink.ui.sign.signup.type

import androidx.annotation.StringRes
import com.seven.colink.R

enum class SignUpUIState(
    @StringRes val title: Int,
    @StringRes val subTitle: Int,
    @StringRes val valid: Int,
) {
    NAME(
        title = R.string.sign_up_input_name,
        subTitle = R.string.sign_up_name,
        valid = R.string.sign_up_valid_name
    ),
    EMAIL(
        title = R.string.sign_up_input_email,
        subTitle = R.string.sign_up_email,
        valid = R.string.sign_up_valid_email
    ),
    PASSWORD(
        title = R.string.sign_up_input_password,
        subTitle = R.string.sign_up_password,
        valid = R.string.sign_up_valid_password
    ),
    PROFILE(
        title = R.string.sign_up_input_id,
        subTitle = R.string.sign_up_id,
        valid = R.string.sign_up_valid_name
    ),
}