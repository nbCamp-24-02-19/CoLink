package com.seven.colink.ui.post.register.post.model

import androidx.annotation.StringRes
import com.seven.colink.R
import com.seven.colink.util.Constants

enum class PostErrorMessage(
    @StringRes val message1: Int,
    @StringRes val message2: Int? = null
) {
    // 태그
    TAG_MAX_COUNT(R.string.message_max_number, Constants.LIMITED_TAG_COUNT),
    TAG_ALREADY_EXIST(R.string.message_already_exists),
    // 텍스트
    TITLE_BLANK(R.string.input_title_message),
    DESCRIPTION_BLANK(R.string.input_description_message),
    PRECAUTIONS_BLANK(R.string.description_precautions),
    ALREADY_SUPPORT(R.string.already_supported),
    SUCCESS_SUPPORT(R.string.successful_support),
    EXPECTED_SCHEDULE_BLANK(R.string.estimated_schedule),


    EMPTY(R.string.sign_up_pass),
    PASS(R.string.sign_up_pass)
    ;
}