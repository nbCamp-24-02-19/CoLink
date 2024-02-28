package com.seven.colink.ui.sign.signup.valid

import androidx.annotation.StringRes
import com.seven.colink.R

enum class SignUpErrorMessage(
    @StringRes val message: Int
) {

    ID(R.string.sign_up_id_error),
    NAME(R.string.sign_up_name_error),
    EMAIL(R.string.sign_up_email_error),
    PASSWORD(R.string.sign_up_password_error),
    PASSWORD_PASSWORD(R.string.sign_up_confirm_error),

    DUMMY(R.string.sign_up_pass),
    PASS(R.string.sign_up_pass)
    ;
}