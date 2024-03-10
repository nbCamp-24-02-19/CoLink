package com.seven.colink.ui.post.register.post

import androidx.annotation.StringRes
import com.seven.colink.R

enum class PostErrorMessage(
    @StringRes val message: Int
) {
    TITLE_BLANK(R.string.input_title_message),
    DESCRIPTION_BLANK(R.string.input_description_message),
    EMPTY(R.string.sign_up_pass),
    PASS(R.string.sign_up_pass)
    ;
}