package com.seven.colink.util.convert

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun LocalDateTime.convertLocalDateTime(): String = run {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    this.format(formatter)
}

fun String.convertTime(): String {
    val localDateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val minutes = ChronoUnit.MINUTES.between(localDateTime, now)

    return when {
        minutes < 1 -> "방금전"
        minutes < 60 * 24 -> localDateTime.format(DateTimeFormatter.ofPattern("a hh:mm"))
        minutes < 60 * 24 * 60-> localDateTime.format(DateTimeFormatter.ofPattern("M월 d일"))
        else -> localDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }
}
fun String.convertToDaysAgo(): String {
    val localDateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val minutes = ChronoUnit.MINUTES.between(localDateTime, now)
    val hours = ChronoUnit.HOURS.between(localDateTime, now)
    val days = ChronoUnit.DAYS.between(localDateTime, now)

    return when {
        minutes.toInt() == 0 -> "방금전"
        minutes < 60 -> minutes.toString() + "분 전"
        hours < 24 -> hours.toString() + "시간 전"
        days < 7 -> localDateTime.format(DateTimeFormatter.ofPattern("M월 d일"))
        else -> localDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }
}

fun String.convertCalculateDays(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = format.parse(this)
    val now = Date()
    val diff = now.time - (date?.time ?: 0)
    val days = diff / (24 * 60 * 60 * 1000)
    return "+ ${days}일째"
}