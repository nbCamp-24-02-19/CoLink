package com.seven.colink.ui.sign.signup.type

enum class SignUpEntryType {
    CREATE,
    UPDATE_PROFILE,
    UPDATE_PASSWORD
    ;

    companion object {

        fun getEntryType(ordinal: Int?): SignUpEntryType =
            SignUpEntryType.values().firstOrNull {
                it.ordinal == ordinal
            } ?: CREATE
    }
}