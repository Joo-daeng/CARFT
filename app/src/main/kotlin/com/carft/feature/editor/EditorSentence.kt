package com.carft.feature.editor

import com.carft.domain.model.ActionBlock
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.TriggerBlock
import com.carft.domain.model.clockText
import com.carft.ui.model.seatLabel
import com.carft.ui.model.toChip

fun TriggerBlock.sentence(): String = when (this) {
    is TriggerBlock.SeatBeltBuckled -> "${seatLabel(seat)}에서 안전벨트를 매는 순간"
    is TriggerBlock.SeatOccupied -> "${seatLabel(seat)}에 사람이 앉는 순간"
    is TriggerBlock.WiperOn -> "와이퍼가 켜지는 순간"
    is TriggerBlock.TimeOfDay -> "매일 ${clockText(hour, minute)}이 되는 순간"
    is TriggerBlock.TemperatureThreshold -> "실내가 ${threshold.toInt()}°C 이하로 떨어지는 순간"
    is TriggerBlock.Manual -> "내가 버튼을 누를 때"
}

fun ConditionBlock.sentence(): String = when (this) {
    is ConditionBlock.TemperatureRange -> if (maxTemp >= 50f) "실내가 ${minTemp.toInt()}°C 이상일 때" else "실내가 ${maxTemp.toInt()}°C 이하일 때"
    is ConditionBlock.IsSeated -> "${seatLabel(seat)}에 사람이 있을 때"
    is ConditionBlock.IsDay -> "아침 ${hour}시일 때"
    is ConditionBlock.Weekday -> when (days) {
        "weekend" -> "토요일이나 일요일일 때"
        "everyday" -> "요일에 관계없이"
        else -> "월요일부터 금요일까지일 때"
    }
    is ConditionBlock.TimeSlot -> "${slotLabel(slot)} 시간대일 때"
    is ConditionBlock.WiperOff -> "와이퍼가 꺼져 있을 때"
    else -> "${toChip().text}일 때"
}

fun ActionBlock.sentence(): String = when (this) {
    is ActionBlock.SetHvacTemperature -> "공조를 켜고 ${formatTemp(targetTemp)}°C로 맞춰요"
    is ActionBlock.SetSeatHeat -> "${seatLabel(seat)} 열선을 ${level}단으로 켜요"
    is ActionBlock.SetAmbientLight -> "앰비언트 조명을 켜요"
    is ActionBlock.PlayTextToSpeech -> "음성으로 안내해요"
    is ActionBlock.SteeringHeat -> "핸들 열선을 켜요"
    is ActionBlock.CabinLight -> "실내등을 켜요"
    is ActionBlock.NaviDestination -> "내비 목적지를 설정해요"
    else -> "${toChip().text}을 켜요"
}

fun formatTemp(t: Float): String = if (t % 1f == 0f) t.toInt().toString() else t.toString()

val TIME_SLOTS = listOf(
    Triple("morning", "아침", "06:00–10:00"),
    Triple("day", "낮", "10:00–18:00"),
    Triple("evening", "저녁", "18:00–22:00"),
    Triple("night", "밤", "22:00–다음 날 06:00")
)

val WEEKDAYS = listOf(
    Triple("weekday", "평일", "월요일 ~ 금요일"),
    Triple("weekend", "주말", "토요일 · 일요일"),
    Triple("everyday", "매일", "요일에 관계없이")
)

fun slotLabel(slot: String): String = TIME_SLOTS.firstOrNull { it.first == slot }?.second ?: "아침"
