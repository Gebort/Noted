package com.example.noted.domain.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

data class Date(
        val datestamp: Long,
        val dateUTC: Long,
        val dateStr: String,
        val timeStr: String,
        val dateLong: Long,
        val timeLong: Long,
        val dayOfWeek: Int
){
    companion object {
        fun fromUTC(dateUTC: Long): Date {
            val instant = Instant.ofEpochMilli(dateUTC)
            val dateSnap = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            val timeStr  = DateTimeFormatter.ofPattern("HH.mm").format(dateSnap)
            val dateStr  = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(dateSnap)
            val dateLong = LocalDate.of(dateSnap.year, dateSnap.month, dateSnap.dayOfMonth).toEpochDay()*24*60*60*1000
            val timeLong = dateSnap.hour * 60 * 60 * 1000 + dateSnap.minute * 60 * 1000L
            val datestamp = dateLong + timeLong
            val dayOfWeek = dateSnap.dayOfWeek.value
            return Date(datestamp, dateUTC, dateStr, timeStr, dateLong, timeLong, dayOfWeek)
        }
        fun fromLocal(dateLocal: Long): Date {
            val instant = Instant.ofEpochMilli(dateLocal)
            val dateSnap = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
            val timeStr  = DateTimeFormatter.ofPattern("HH.mm").format(dateSnap)
            val dateStr  = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(dateSnap)
            val dateUTC = dateLocal - TimeZone.getDefault().rawOffset - TimeZone.getDefault().dstSavings
            val dateLong = LocalDate.of(dateSnap.year, dateSnap.month, dateSnap.dayOfMonth).toEpochDay()*24*60*60*1000
            val timeLong = dateSnap.hour * 60 * 60 * 1000 + dateSnap.minute * 60 * 1000L
            val dayOfWeek = dateSnap.dayOfWeek.value
            return Date(dateLocal, dateUTC, dateStr, timeStr, dateLong, timeLong, dayOfWeek)
        }
    }
}
