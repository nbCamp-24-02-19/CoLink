package com.seven.colink.ui.sign.signup.type

import androidx.annotation.StringRes
import com.seven.colink.R

enum class SignUpUIState(
    @StringRes val title: Int,
    @StringRes val subTitle: Int,
) {
    ID(
        title = R.string.sign_up_input_id,
        subTitle = R.string.sign_up_id,
    ),
    NAME(
        title = R.string.sign_up_input_name,
        subTitle = R.string.sign_up_name,
    ),
    EMAIL(
        title = R.string.sign_up_input_email,
        subTitle = R.string.sign_up_email,
    ),
    PASSWORD(
        title = R.string.sign_up_input_password,
        subTitle = R.string.sign_up_password,
    ),
    PROFILE(
        title = R.string.sign_up_input_id,
        subTitle = R.string.sign_up_id,
    )
}