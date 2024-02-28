package com.seven.colink.ui.sign.signup.valid

import androidx.annotation.StringRes
import com.seven.colink.R

enum class SignUpErrorMessage(
    @StringRes val message: Int
) {

    NAME(R.string.sign_up_name_error),
    EMAIL(R.string.sign_up_email_error),
    DUPLICATE_EMAIL(R.string.sign_up_email_duplication_error),
    PASSWORD(R.string.sign_up_password_error),
    PASSWORD_PASSWORD(R.string.sign_up_confirm_error),
    SPECIALTY(R.string.sign_up_specialty),
    SKILL(R.string.sign_up_skill),
    LEVEL(R.string.sign_up_level),

    DUMMY(R.string.sign_up_pass),
    PASS(R.string.sign_up_pass)
    ;
}