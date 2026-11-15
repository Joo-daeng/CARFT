package com.carft.domain.rule

import com.carft.domain.model.ActionBlock
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.Option
import com.carft.domain.model.TriggerBlock

data class ValidationCheck(val label: String, val ok: Boolean, val detail: String? = null, val blocking: Boolean = true)

data class ValidationResult(val checks: List<ValidationCheck>) {
    val canSave: Boolean get() = checks.none { it.blocking && !it.ok }
    val allOk: Boolean get() = checks.all { it.ok }
}

object Validator {
    fun validate(
        optionId: String,
        trigger: TriggerBlock?,
        conditions: List<ConditionBlock>,
        logic: String,
        actions: List<ActionBlock>,
        others: List<Option>
    ): ValidationResult = ValidationResult(
        listOf(
            completeness(trigger, actions),
            consistency(conditions, logic),
            conflicts(optionId, trigger, actions, others)
        )
    )

    private fun completeness(trigger: TriggerBlock?, actions: List<ActionBlock>) = ValidationCheck(
        label = "상황과 동작이 모두 있어요",
        ok = trigger != null && actions.isNotEmpty(),
        detail = when {
            trigger == null -> "상황 블록을 놓아 주세요"
            actions.isEmpty() -> "동작 블록을 하나 이상 놓아 주세요"
            else -> null
        }
    )

    private fun consistency(conditions: List<ConditionBlock>, logic: String): ValidationCheck {
        if (logic.uppercase() != "AND") return ValidationCheck("조건이 서로 부딪히지 않아요", true)
        val ranges = conditions.filterIsInstance<ConditionBlock.TemperatureRange>()
        val tempClash = ranges.size > 1 && ranges.maxOf { it.minTemp } > ranges.minOf { it.maxTemp }
        val hours = conditions.filterIsInstance<ConditionBlock.IsDay>().map { it.hour }.distinct()
        val hourClash = hours.size > 1
        return ValidationCheck(
            label = "조건이 서로 부딪히지 않아요",
            ok = !tempClash && !hourClash,
            detail = when {
                tempClash -> "실내 온도 조건의 범위가 겹치지 않아요"
                hourClash -> "시각 조건이 서로 달라요"
                else -> null
            }
        )
    }

    private fun conflicts(optionId: String, trigger: TriggerBlock?, actions: List<ActionBlock>, others: List<Option>): ValidationCheck {
        val clash = others.asSequence()
            .filter { it.id != optionId && it.isEnabled && trigger != null && sameMoment(it.rule.trigger, trigger) }
            .mapNotNull { other ->
                actions.firstNotNullOfOrNull { mine ->
                    other.rule.actions.firstOrNull { differs(mine, it) }?.let { theirs -> Triple(other.name, mine, theirs) }
                }
            }
            .firstOrNull()
        return ValidationCheck(
            label = "다른 옵션과 같은 동작을 다르게 바꾸지 않아요",
            ok = clash == null,
            detail = clash?.let { (name, mine, theirs) -> "'$name'와 ${target(mine)}이 달라요 (${value(mine)} / ${value(theirs)}) · 우선순위가 높은 쪽이 적용돼요" },
            blocking = false
        )
    }

    private fun sameMoment(a: TriggerBlock, b: TriggerBlock): Boolean = when {
        a is TriggerBlock.SeatOccupied && b is TriggerBlock.SeatOccupied -> a.seat == b.seat
        a is TriggerBlock.SeatBeltBuckled && b is TriggerBlock.SeatBeltBuckled -> a.seat == b.seat
        a is TriggerBlock.WiperOn && b is TriggerBlock.WiperOn -> true
        a is TriggerBlock.TimeOfDay && b is TriggerBlock.TimeOfDay -> a.hour == b.hour && a.minute == b.minute
        a is TriggerBlock.TemperatureThreshold && b is TriggerBlock.TemperatureThreshold -> a.threshold == b.threshold
        else -> false
    }

    private fun differs(a: ActionBlock, b: ActionBlock): Boolean = when {
        a is ActionBlock.SetHvacTemperature && b is ActionBlock.SetHvacTemperature -> a.targetTemp != b.targetTemp
        a is ActionBlock.SetSeatHeat && b is ActionBlock.SetSeatHeat -> a.seat == b.seat && a.level != b.level
        a is ActionBlock.SetAmbientLight && b is ActionBlock.SetAmbientLight -> a.color != b.color
        else -> false
    }

    private fun target(a: ActionBlock) = when (a) {
        is ActionBlock.SetHvacTemperature -> "공조 온도"
        is ActionBlock.SetSeatHeat -> "좌석 열선"
        is ActionBlock.SetAmbientLight -> "조명 색"
        is ActionBlock.PlayTextToSpeech -> "음성 안내"
    }

    private fun value(a: ActionBlock) = when (a) {
        is ActionBlock.SetHvacTemperature -> "${formatTemp(a.targetTemp)}°C"
        is ActionBlock.SetSeatHeat -> "${a.level}단"
        is ActionBlock.SetAmbientLight -> a.color
        is ActionBlock.PlayTextToSpeech -> a.text
    }

    private fun formatTemp(t: Float) = if (t % 1f == 0f) t.toInt().toString() else t.toString()
}
