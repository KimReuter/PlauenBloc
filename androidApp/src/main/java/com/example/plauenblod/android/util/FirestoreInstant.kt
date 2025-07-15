package com.example.plauenblod.android.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class FirestoreInstant(
    val seconds: Long = 0,
    val nanoseconds: Int = 0
) {
    fun toInstant(): Instant = Instant.fromEpochSeconds(seconds, nanoseconds.toLong())

    companion object {
        fun fromInstant(instant: Instant): FirestoreInstant {
            return FirestoreInstant(
                seconds = instant.epochSeconds,
                nanoseconds = (instant.nanosecondsOfSecond)
            )
        }

        fun fromMap(map: Map<String, Any?>?): FirestoreInstant? {
            val seconds = (map?.get("seconds") as? Number)?.toLong() ?: return null
            val nanoseconds = (map["nanoseconds"] as? Number)?.toInt() ?: 0
            return FirestoreInstant(seconds, nanoseconds)
        }
    }
}

fun FirestoreInstant.toRelativeTimeString(): String {
    val postTime: Instant = this.toInstant()
    val now = Clock.System.now()
    val duration = now - postTime

    return when {
        duration.inWholeSeconds < 60 -> "gerade eben"
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes} Minuten her"
        duration.inWholeHours < 24 -> "${duration.inWholeHours} Stunden her"
        duration.inWholeDays < 7 -> "${duration.inWholeDays} Tage her"
        else -> {
            val localDate = postTime.toLocalDateTime(TimeZone.currentSystemDefault())
            "${localDate.date.dayOfMonth}.${localDate.date.monthNumber}.${localDate.date.year}"
        }
    }
}