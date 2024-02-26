package com.seven.colink.util.convert

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.convertLocalDateTime(): String = run {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    this.format(formatter)
}