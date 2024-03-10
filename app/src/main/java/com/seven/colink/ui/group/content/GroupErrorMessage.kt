package com.seven.colink.ui.group.content

import androidx.annotation.StringRes
import com.seven.colink.R
import com.seven.colink.util.Constants

enum class GroupErrorMessage(
    @StringRes val message1: Int,
    @StringRes val message2: Int? = null
) {
    // 태그
    TAG_MAX_COUNT(R.string.message_max_number, Constants.LIMITED_TAG_COUNT),
    TAG_ALREADY_EXIST(R.string.message_already_exists),
    PASS(R.string.sign_up_pass)
}