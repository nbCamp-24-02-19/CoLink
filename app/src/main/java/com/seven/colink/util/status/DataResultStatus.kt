package com.seven.colink.util.status

enum class DataResultStatus(
    var message: String?
) {
    SUCCESS(
        message = "success"
    ), FAIL (
        message = "Unknown error"
    )
}