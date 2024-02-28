package com.seven.colink.ui.sign.signup.type

enum class SignUpProfileViewType {
    CATEGORY,SKILL,LEVEL,INFO,BLOG,UNKNOWN
    ;

    companion object{
        fun from(ordinal: Int) = SignUpProfileViewType.values().find {
            it.ordinal == ordinal
        }?: UNKNOWN
    }
}