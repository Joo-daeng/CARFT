package com.carft.ui.format

import java.util.Calendar

private val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")

fun timeLabel(at: Long, now: Long = System.currentTimeMillis()): String {
    val diffMin = (now - at) / 60_000
    if (diffMin in 0..1) return "방금"
    if (diffMin in 2..59) return "${diffMin}분 전"
    val then = Calendar.getInstance().apply { timeInMillis = at }
    val today = Calendar.getInstance().apply { timeInMillis = now }
    val hm = "%02d:%02d".format(then.get(Calendar.HOUR_OF_DAY), then.get(Calendar.MINUTE))
    val days = dayIndex(today) - dayIndex(then)
    return when {
        days == 0L -> "오늘 $hm"
        days == 1L -> "어제 $hm"
        days < 7L -> "지난 ${weekdays[then.get(Calendar.DAY_OF_WEEK) - 1]} $hm"
        else -> "%d/%d %s".format(then.get(Calendar.MONTH) + 1, then.get(Calendar.DAY_OF_MONTH), hm)
    }
}

private fun dayIndex(c: Calendar): Long =
    (c.timeInMillis + c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / 86_400_000L
