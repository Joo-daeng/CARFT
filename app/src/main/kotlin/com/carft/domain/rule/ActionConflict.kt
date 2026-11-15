package com.carft.domain.rule

import com.carft.domain.model.ActionBlock

data class ConflictEvent(
    val higherId: String,
    val higherName: String,
    val higherAction: ActionBlock,
    val lowerId: String,
    val lowerName: String,
    val lowerAction: ActionBlock,
    val at: Long
) {
    val target: String get() = ActionConflict.label(higherAction)
    val higherValue: String get() = ActionConflict.value(higherAction)
    val lowerValue: String get() = ActionConflict.value(lowerAction)
}

object ActionConflict {
    fun key(action: ActionBlock): String? = when (action) {
        is ActionBlock.SetHvacTemperature -> "hvac"
        is ActionBlock.SetSeatHeat -> "seat-heat:${action.seat}"
        is ActionBlock.SetAmbientLight -> "ambient"
        is ActionBlock.PlayTextToSpeech -> null
        is ActionBlock.SteeringHeat -> "steering-heat"
        is ActionBlock.CabinLight -> "cabin-light"
        is ActionBlock.NaviDestination -> null
    }

    fun differs(a: ActionBlock, b: ActionBlock): Boolean = key(a) != null && key(a) == key(b) && value(a) != value(b)

    fun label(action: ActionBlock): String = when (action) {
        is ActionBlock.SetHvacTemperature -> "운전석 온도"
        is ActionBlock.SetSeatHeat -> if (action.seat == "rearRight") "뒷좌석 열선" else "운전석 열선"
        is ActionBlock.SetAmbientLight -> "앰비언트 조명"
        is ActionBlock.PlayTextToSpeech -> "음성 안내"
        is ActionBlock.SteeringHeat -> "핸들 열선"
        is ActionBlock.CabinLight -> "실내등"
        is ActionBlock.NaviDestination -> "내비 목적지"
    }

    fun value(action: ActionBlock): String = when (action) {
        is ActionBlock.SetHvacTemperature -> "${if (action.targetTemp % 1f == 0f) action.targetTemp.toInt() else action.targetTemp}°C"
        is ActionBlock.SetSeatHeat -> "${action.level}단"
        is ActionBlock.SetAmbientLight -> action.color
        is ActionBlock.PlayTextToSpeech -> action.text
        is ActionBlock.SteeringHeat -> "켜기"
        is ActionBlock.CabinLight -> "켜기"
        is ActionBlock.NaviDestination -> "목적지"
    }

    fun adopt(target: ActionBlock, from: ActionBlock): ActionBlock = when {
        target is ActionBlock.SetHvacTemperature && from is ActionBlock.SetHvacTemperature -> target.copy(targetTemp = from.targetTemp)
        target is ActionBlock.SetSeatHeat && from is ActionBlock.SetSeatHeat -> target.copy(level = from.level)
        target is ActionBlock.SetAmbientLight && from is ActionBlock.SetAmbientLight -> target.copy(color = from.color)
        else -> target
    }
}
