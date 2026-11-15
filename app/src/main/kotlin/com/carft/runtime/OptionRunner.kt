package com.carft.runtime

import com.carft.data.ConflictPolicy
import com.carft.data.HistoryRepository
import com.carft.data.OptionRepository
import com.carft.data.SettingsRepository
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.ActionExecutionResult
import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.ExecutionResult
import com.carft.domain.model.Option
import com.carft.domain.model.TriggerBlock
import com.carft.domain.model.VehicleState
import com.carft.domain.rule.ActionConflict
import com.carft.domain.rule.ConflictEvent
import com.carft.domain.rule.RuleEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class OptionRunner(
    private val scope: CoroutineScope,
    private val state: StateFlow<VehicleState>,
    private val engine: RuleEngine,
    private val options: OptionRepository,
    private val history: HistoryRepository,
    private val settings: SettingsRepository
) {
    private data class Applied(val optionId: String, val name: String, val priority: Int, val action: ActionBlock, val at: Long)

    private val applied = mutableMapOf<String, Applied>()

    private val _events = MutableSharedFlow<ExecutionRecord>(extraBufferCapacity = 8)
    val events: SharedFlow<ExecutionRecord> = _events.asSharedFlow()

    private val _pendingConflict = MutableStateFlow<ConflictEvent?>(null)
    val pendingConflict: StateFlow<ConflictEvent?> = _pendingConflict.asStateFlow()

    fun clearConflict() {
        _pendingConflict.value = null
    }

    fun start() {
        scope.launch {
            var previous = state.value
            state.collect { current ->
                if (settings.settings.value.watching) {
                    options.options.value.filter { it.isEnabled && fired(it.rule.trigger, previous, current) }
                        .sortedBy { it.priority }
                        .forEach { run(it, current) }
                }
                previous = current
            }
        }
    }

    fun runNow(option: Option) {
        scope.launch { run(option, state.value, manual = true) }
    }

    private suspend fun run(option: Option, snapshot: VehicleState, manual: Boolean = false) {
        val now = System.currentTimeMillis()
        val kept = mutableListOf<ActionBlock>()
        var conflict: ConflictEvent? = null
        for (action in option.rule.actions) {
            val previous = ActionConflict.key(action)?.let { applied[it] }
                ?.takeIf { it.optionId != option.id && now - it.at < CONFLICT_WINDOW_MS && ActionConflict.differs(it.action, action) }
            when {
                previous == null -> kept += action
                previous.priority < option.priority -> conflict = ConflictEvent(previous.optionId, previous.name, previous.action, option.id, option.name, action, now)
                else -> {
                    kept += action
                    conflict = ConflictEvent(option.id, option.name, action, previous.optionId, previous.name, previous.action, now)
                }
            }
        }
        val trigger = if (manual) TriggerBlock.Manual() else option.rule.trigger
        val result = runCatching { engine.evaluate(option.rule.copy(trigger = trigger, actions = kept), snapshot) }
            .getOrElse { ExecutionResult.NotExecuted("오류: ${it.message}") }
        val conditionsPassed = result is ExecutionResult.Success || result is ExecutionResult.PartialSuccess ||
            (result is ExecutionResult.NotExecuted && result.reason == NO_ACTIONS)
        val shown = if (kept.isEmpty() && conflict != null) ExecutionResult.NotExecuted("충돌 · 우선순위가 높은 '${conflict.higherName}' 값을 유지해요") else result
        remember(option, kept, shown, now)
        val record = ExecutionRecord(UUID.randomUUID().toString(), option.id, option.name, now, shown)
        history.add(record)
        _events.emit(record)
        if (conflict != null && conditionsPassed) recordConflict(conflict)
    }

    private fun remember(option: Option, kept: List<ActionBlock>, result: ExecutionResult, now: Long) {
        val succeeded = when (result) {
            is ExecutionResult.Success -> result.actions
            is ExecutionResult.PartialSuccess -> result.actions
            else -> emptyList()
        }.filterIsInstance<ActionExecutionResult.Success>().map { it.actionId }.toSet()
        kept.filter { it.id in succeeded }.forEach { action ->
            ActionConflict.key(action)?.let { applied[it] = Applied(option.id, option.name, option.priority, action, now) }
        }
    }

    private fun recordConflict(conflict: ConflictEvent) {
        history.add(
            ExecutionRecord(
                id = UUID.randomUUID().toString(),
                optionId = conflict.lowerId,
                optionName = "${conflict.higherName} ↔ ${conflict.lowerName}",
                triggeredAt = conflict.at,
                result = ExecutionResult.PartialSuccess(
                    listOf(
                        ActionExecutionResult.Success(conflict.higherAction.id, "${conflict.target} ${conflict.higherValue}"),
                        ActionExecutionResult.Conflict(conflict.lowerAction.id, "${conflict.lowerName} ${conflict.lowerValue}")
                    ),
                    "${conflict.target} ${conflict.higherValue} / ${conflict.lowerValue} → ${conflict.higherName} 값으로 맞춤"
                )
            )
        )
        if (settings.settings.value.conflictPolicy == ConflictPolicy.AskEveryTime) _pendingConflict.value = conflict
    }

    private fun fired(trigger: TriggerBlock, before: VehicleState, now: VehicleState): Boolean = when (trigger) {
        is TriggerBlock.Manual -> false
        is TriggerBlock.SeatOccupied -> rose(seat(before, trigger.seat, occupied = true), seat(now, trigger.seat, occupied = true))
        is TriggerBlock.SeatBeltBuckled -> rose(seat(before, trigger.seat, occupied = false), seat(now, trigger.seat, occupied = false))
        is TriggerBlock.TemperatureThreshold -> rose(
            before.interiorTemp?.let { it <= trigger.threshold },
            now.interiorTemp?.let { it <= trigger.threshold }
        )
        is TriggerBlock.WiperOn -> rose(before.wiperOn, now.wiperOn)
    }

    private fun rose(before: Boolean?, now: Boolean?) = now == true && before == false

    private companion object {
        const val CONFLICT_WINDOW_MS = 30 * 60 * 1000L
        const val NO_ACTIONS = "동작 없음"
    }

    private fun seat(s: VehicleState, seat: String, occupied: Boolean): Boolean? = when (seat) {
        "rearRight" -> if (occupied) s.rearRightSeatOccupied else s.rearRightBeltBuckled
        else -> if (occupied) s.driverSeatOccupied else s.driverBeltBuckled
    }
}
