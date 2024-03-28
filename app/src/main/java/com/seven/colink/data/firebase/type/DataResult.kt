package com.seven.colink.data.firebase.type

sealed class DataResult<out T> {
    data class Success<T>(val data: T): DataResult<T>()
    data class Error(val error: Exception): DataResult<Nothing>()
}
