package com.haze.servicesprovider.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    fun formatIsoToReadable(isoDateString: String?): String {
        if (isoDateString.isNullOrBlank()) return "N/A"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            // Strip possible subsecond / timezone offsets for safe parsing
            val sanitized = isoDateString.substringBefore(".").substringBefore("+").substringBefore("Z")
            val date = parser.parse(sanitized) ?: return isoDateString
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            formatter.format(date)
        } catch (_: Exception) {
            isoDateString.take(10)
        }
    }

    fun formatIsoToDateOnly(isoDateString: String?): String {
        if (isoDateString.isNullOrBlank()) return "N/A"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(isoDateString.take(10)) ?: return isoDateString
            val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            formatter.format(date)
        } catch (_: Exception) {
            isoDateString.take(10)
        }
    }

    fun getCurrentIsoTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date())
    }
}
