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
    else -> "${toChip().text}일 때"
}

fun ActionBlock.sentence(): String = when (this) {
    is ActionBlock.SetHvacTemperature -> "공조를 켜고 ${formatTemp(targetTemp)}°C로 맞춰요"
    is ActionBlock.SetSeatHeat -> "${seatLabel(seat)} 열선을 ${level}단으로 켜요"
    is ActionBlock.SetAmbientLight -> "앰비언트 조명을 켜요"
    is ActionBlock.PlayTextToSpeech -> "음성으로 안내해요"
    else -> "${toChip().text}을 켜요"
}

fun formatTemp(t: Float): String = if (t % 1f == 0f) t.toInt().toString() else t.toString()
