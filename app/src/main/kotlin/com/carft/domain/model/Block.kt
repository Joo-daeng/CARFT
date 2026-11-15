package com.carft.domain.model

import java.util.*

sealed class Block {
    abstract val id: String
    abstract fun displayName(): String
}

sealed class TriggerBlock : Block() {
    data class Manual(override val id: String = UUID.randomUUID().toString()) : TriggerBlock() {
        override fun displayName() = "내가 버튼을 누르면"
    }

    data class SeatOccupied(
        override val id: String = UUID.randomUUID().toString(),
        val seat: String = "driver"
    ) : TriggerBlock() {
        override fun displayName() = "${seat}석에 앉으면"
    }

    data class SeatBeltBuckled(
        override val id: String = UUID.randomUUID().toString(),
        val seat: String = "driver"
    ) : TriggerBlock() {
        override fun displayName() = "${seat}석 벨트를 매면"
    }

    data class TemperatureThreshold(
        override val id: String = UUID.randomUUID().toString(),
        val threshold: Float = 10f
    ) : TriggerBlock() {
        override fun displayName() = "실내 온도가 ${threshold.toInt()}°C 이하"
    }

    data class WiperOn(override val id: String = UUID.randomUUID().toString()) : TriggerBlock() {
        override fun displayName() = "와이퍼가 켜지면"
    }

    data class TimeOfDay(
        override val id: String = UUID.randomUUID().toString(),
        val hour: Int = 7,
        val minute: Int = 30
    ) : TriggerBlock() {
        override fun displayName() = "${clockText(hour, minute)}이 되면"
    }
}

fun clockText(hour: Int, minute: Int): String = "%02d:%02d".format(hour, minute)

sealed class ConditionBlock : Block() {
    data class IsSeated(
        override val id: String = UUID.randomUUID().toString(),
        val seat: String = "driver"
    ) : ConditionBlock() {
        override fun displayName() = "${seat}석에 사람이 있다"
    }

    data class TemperatureRange(
        override val id: String = UUID.randomUUID().toString(),
        val minTemp: Float = 10f,
        val maxTemp: Float = 20f
    ) : ConditionBlock() {
        override fun displayName() = "실내 온도가 ${minTemp.toInt()}~${maxTemp.toInt()}°C"
    }

    data class IsDay(
        override val id: String = UUID.randomUUID().toString(),
        val hour: Int = 7
    ) : ConditionBlock() {
        override fun displayName() = "아침 ${hour}시"
    }
}

sealed class ActionBlock : Block() {
    data class SetHvacTemperature(
        override val id: String = UUID.randomUUID().toString(),
        val targetTemp: Float = 22f,
        val requiresStationary: Boolean = false
    ) : ActionBlock() {
        override fun displayName() = "공조 ${targetTemp.toInt()}°C"
    }

    data class SetSeatHeat(
        override val id: String = UUID.randomUUID().toString(),
        val seat: String = "driver",
        val level: Int = 3,
        val requiresStationary: Boolean = false
    ) : ActionBlock() {
        override fun displayName() = "${seat}석 열선 ${level}단"
    }

    data class SetAmbientLight(
        override val id: String = UUID.randomUUID().toString(),
        val color: String = "blue",
        val brightness: Int = 50,
        val requiresStationary: Boolean = false
    ) : ActionBlock() {
        override fun displayName() = "앰비언트 조명 $color"
    }

    data class PlayTextToSpeech(
        override val id: String = UUID.randomUUID().toString(),
        val text: String = "안녕하세요",
        val requiresStationary: Boolean = false
    ) : ActionBlock() {
        override fun displayName() = "음성: \"$text\""
    }
}

data class CombinationLogic(
    val operator: String = "AND" // "AND" or "OR"
)

data class Rule(
    val id: String = UUID.randomUUID().toString(),
    val trigger: TriggerBlock,
    val conditions: List<ConditionBlock> = emptyList(),
    val combinationLogic: CombinationLogic = CombinationLogic("AND"),
    val actions: List<ActionBlock> = emptyList()
) {
    fun toNaturalLanguage(): String {
        val conditionText = if (conditions.isEmpty()) {
            ""
        } else {
            " ${combinationLogic.operator.lowercase()} " +
            conditions.joinToString(" ${combinationLogic.operator.lowercase()} ") { it.displayName() }
        }

        val actionText = actions.joinToString(", ") { it.displayName() }

        return "${trigger.displayName()}$conditionText → $actionText"
    }
}

data class Option(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "새 옵션",
    val rule: Rule,
    val isEnabled: Boolean = true,
    val priority: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
