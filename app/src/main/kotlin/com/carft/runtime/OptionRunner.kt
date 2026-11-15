package com.carft.runtime

import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.ExecutionResult
import com.carft.domain.model.Option
import com.carft.domain.model.TriggerBlock
import com.carft.domain.model.VehicleState
import com.carft.domain.rule.RuleEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class OptionRunner(
    private val scope: CoroutineScope,
    private val state: StateFlow<VehicleState>,
    private val engine: RuleEngine
) {
    private var options: List<Option> = emptyList()

    private val _records = MutableStateFlow<List<ExecutionRecord>>(emptyList())
    val records: StateFlow<List<ExecutionRecord>> = _records.asStateFlow()

    private val _events = MutableSharedFlow<ExecutionRecord>(extraBufferCapacity = 8)
    val events: SharedFlow<ExecutionRecord> = _events.asSharedFlow()

    var watching: Boolean = true

    fun start() {
        scope.launch {
            var previous = state.value
            state.collect { current ->
                if (watching) {
                    options.filter { it.isEnabled && fired(it.rule.trigger, previous, current) }
                        .sortedBy { it.priority }
                        .forEach { run(it, current) }
                }
                previous = current
            }
        }
    }

    fun setOptions(list: List<Option>) {
        options = list
    }

    fun runNow(option: Option) {
        scope.launch { run(option, state.value, manual = true) }
    }

    private suspend fun run(option: Option, snapshot: VehicleState, manual: Boolean = false) {
        val rule = if (manual) option.rule.copy(trigger = TriggerBlock.Manual()) else option.rule
        val result = runCatching { engine.evaluate(rule, snapshot) }
            .getOrElse { ExecutionResult.NotExecuted("오류: ${it.message}") }
        val record = ExecutionRecord(
            id = UUID.randomUUID().toString(),
            optionId = option.id,
            optionName = option.name,
            triggeredAt = System.currentTimeMillis(),
            result = result
        )
        _records.update { listOf(record) + it }
        _events.emit(record)
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

    private fun rose(before: Boolean?, now: Boolean?) = now == true && before != true

    private fun seat(s: VehicleState, seat: String, occupied: Boolean): Boolean? = when (seat) {
        "rearRight" -> if (occupied) s.rearRightSeatOccupied else s.rearRightBeltBuckled
        else -> if (occupied) s.driverSeatOccupied else s.driverBeltBuckled
    }
}
