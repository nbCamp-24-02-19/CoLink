package com.seven.colink.ui.post

sealed class AddTagResult {
    data object Success : AddTagResult()
    data object MaxNumberExceeded : AddTagResult()
    data object TagAlreadyExists : AddTagResult()
}