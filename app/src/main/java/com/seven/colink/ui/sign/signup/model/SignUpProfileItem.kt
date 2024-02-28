package com.seven.colink.ui.sign.signup.model

sealed interface SignUpProfileItem {
    data object Category: SignUpProfileItem

    data object Skill: SignUpProfileItem

    data object Level: SignUpProfileItem

    data object Info: SignUpProfileItem

    data object Blog: SignUpProfileItem
}
