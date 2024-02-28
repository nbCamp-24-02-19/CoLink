package com.seven.colink.ui.sign.signup.valid

object SignUpValidExtension {

    fun String.includeAt() = this.contains("@")

    /**
     * 이메일 정규 표현식
     */
    fun String.validEmailServiceProvider() = Regex("[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matches(this)

    /**
     * 특수 문자 포함
     */
    fun String.includeSpecialCharacters() =
        Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+").containsMatchIn(this)

    /**
     * 대문자 포함
     */
    fun String.includeUpperCase() = Regex("[A-Z]").containsMatchIn(this)

    fun String.numAndEnglish() = Regex("^[0-9a-zA-Z]+$").matches(this)

}