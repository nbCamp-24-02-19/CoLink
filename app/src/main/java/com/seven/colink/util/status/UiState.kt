package com.seven.colink.util.status

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Exception) : UiState<Nothing>()
}