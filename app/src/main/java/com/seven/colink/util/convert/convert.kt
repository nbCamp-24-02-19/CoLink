package com.seven.colink.util.convert

import com.seven.colink.util.model.UrlMetaData
import com.seven.colink.util.status.ScheduleDateType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun String.containsUrl() =
    Regex("(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*(),]|%[0-9a-fA-F][0-9a-fA-F])+)").containsMatchIn(
        this
    )

fun String.extractUrl(): String {
    val urlPattern =
        Regex("(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*(),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)")
    return urlPattern.findAll(this).map { it.value }.toList().first()
}

suspend fun fetchUrlMetaData(url: String) = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            val htmlString = response.body?.string()

            htmlString?.let {
                val doc = Jsoup.parse(it)
                val title = doc.select("meta[property=og:title]").attr("content")
                val description = doc.select("meta[property=og:description]").attr("content")
                var imageUrl = doc.select("meta[property=og:image]").attr("content")

                if (imageUrl.containsUrl().not()) imageUrl = "https:$imageUrl"
                return@withContext UrlMetaData(title, description, imageUrl, URL(url).host, url)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext null
}

fun LocalDateTime.convertLocalDateTime(): String = run {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    this.format(formatter)
}

fun String.convertTime(): String {
    val localDateTime =
        LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    val minutes = ChronoUnit.MINUTES.between(localDateTime, now)

    return when {
        minutes < 1 -> "방금전"
        minutes < 60 * 24 -> localDateTime.format(DateTimeFormatter.ofPattern("a hh:mm"))
        minutes < 60 * 24 * 60 -> localDateTime.format(DateTimeFormatter.ofPattern("M월 d일"))
        else -> localDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }
}

fun String.convertToDaysAgo(): String {
    val localDateTime =
        LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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
    if (this.isBlank())
        return ""

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = format.parse(this)
    val now = Date()
    val diff = now.time - (date?.time ?: 0)
    val days = diff / (24 * 60 * 60 * 1000)
    return "+ ${days}일째"
}

fun getDateByState(scheduleDateType: ScheduleDateType): String {
    val now = LocalDateTime.now()

    val adjustedTime = when (scheduleDateType) {
        ScheduleDateType.CURRENT -> now.plusHours(1)
        ScheduleDateType.AFTER_TWO_HOUR -> now.plusHours(2)
    }

    return formatDate(adjustedTime)
}

fun formatDate(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:00")
    return date.format(formatter)
}