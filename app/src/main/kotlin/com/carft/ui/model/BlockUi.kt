package com.carft.ui.model

import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.icon.CarftIcon
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.Block
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.Rule
import com.carft.domain.model.TriggerBlock
import com.carft.domain.model.isStopOnly

data class BlockChipUi(val text: String, val kind: BlockKind, val icon: CarftIcon)

data class RuleUi(
    val situation: List<BlockChipUi>,
    val conditions: List<BlockChipUi>,
    val actions: List<BlockChipUi>
) {
    val all: List<BlockChipUi> get() = situation + conditions + actions
}

fun Rule.toUi(): RuleUi = RuleUi(
    situation = listOf(trigger.toChip()),
    conditions = conditions.map { it.toChip() },
    actions = actions.map { it.toChip() }
)

fun seatLabel(seat: String): String = when (seat) {
    "rearRight" -> "오른쪽 뒷좌석"
    "rearLeft" -> "왼쪽 뒷좌석"
    "passenger" -> "조수석"
    else -> "운전석"
}

fun Block.toChip(): BlockChipUi = when (this) {
    is TriggerBlock.Manual -> BlockChipUi("내가 버튼을 누르면", BlockKind.Situation, CarftIcon.Power)
    is TriggerBlock.SeatOccupied -> BlockChipUi("${seatLabel(seat)}에 앉으면", BlockKind.Situation, CarftIcon.Seat)
    is TriggerBlock.SeatBeltBuckled -> BlockChipUi("${seatLabel(seat)} 벨트를 매면", BlockKind.Situation, CarftIcon.Belt)
    is TriggerBlock.TemperatureThreshold -> BlockChipUi("실내 ${threshold.toInt()}°C 이하가 되면", BlockKind.Situation, CarftIcon.Thermo)
    is TriggerBlock.WiperOn -> BlockChipUi("와이퍼가 켜지면", BlockKind.Situation, CarftIcon.Wiper)
    is TriggerBlock.TimeOfDay -> BlockChipUi(displayName(), BlockKind.Situation, CarftIcon.Clock)
    is ConditionBlock.IsSeated -> BlockChipUi("${seatLabel(seat)}에 사람이 있으면", BlockKind.Condition, CarftIcon.Seat)
    is ConditionBlock.TemperatureRange -> BlockChipUi(temperatureText(minTemp, maxTemp), BlockKind.Condition, CarftIcon.Thermo)
    is ConditionBlock.IsDay -> BlockChipUi("아침 ${hour}시", BlockKind.Condition, CarftIcon.Clock)
    is ConditionBlock.Weekday -> BlockChipUi(displayName(), BlockKind.Condition, CarftIcon.Calendar)
    is ConditionBlock.TimeSlot -> BlockChipUi(displayName(), BlockKind.Condition, CarftIcon.Clock)
    is ConditionBlock.WiperOff -> BlockChipUi("와이퍼 꺼짐", BlockKind.Condition, CarftIcon.Wiper)
    is ActionBlock.SetHvacTemperature -> BlockChipUi("공조 ${if (targetTemp % 1f == 0f) targetTemp.toInt() else targetTemp}°C", actionKind(isStopOnly()), CarftIcon.Fan)
    is ActionBlock.SetSeatHeat -> BlockChipUi("${seatLabel(seat)} 열선 ${level}단", actionKind(isStopOnly()), CarftIcon.Heat)
    is ActionBlock.SetAmbientLight -> BlockChipUi("앰비언트 조명", actionKind(isStopOnly()), CarftIcon.Bulb)
    is ActionBlock.PlayTextToSpeech -> BlockChipUi("음성 안내", actionKind(isStopOnly()), CarftIcon.Speaker)
    is ActionBlock.SteeringHeat -> BlockChipUi("핸들 열선", actionKind(isStopOnly()), CarftIcon.Steering)
    is ActionBlock.CabinLight -> BlockChipUi("실내등 켜기", actionKind(isStopOnly()), CarftIcon.Bulb)
    is ActionBlock.NaviDestination -> BlockChipUi("내비 목적지", actionKind(isStopOnly()), CarftIcon.Nav)
    else -> BlockChipUi(displayName(), BlockKind.Action, CarftIcon.Sparkle)
}

private fun actionKind(stopOnly: Boolean) = if (stopOnly) BlockKind.StopOnlyAction else BlockKind.Action

private fun temperatureText(min: Float, max: Float): String = when {
    min <= -30f -> "실내 ${max.toInt()}°C 이하"
    max >= 50f -> "실내 ${min.toInt()}°C 이상"
    else -> "실내 ${min.toInt()}~${max.toInt()}°C"
}
