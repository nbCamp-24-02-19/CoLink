package com.seven.colink.util.status

sealed interface SnackType {
    data object Success: SnackType
    data object Error: SnackType
    data object Notice: SnackType
}