package com.carft.feature.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.domain.model.ActionExecutionResult
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.ExecutionResult
import com.carft.domain.model.Option
import com.carft.domain.model.TriState
import com.carft.domain.model.TriggerBlock
import com.carft.domain.model.VehicleState
import com.carft.domain.rule.ConditionEvaluator
import com.carft.domain.rule.RuleEngine
import com.carft.infrastructure.vehicle.FakeVehicleAdapter
import com.carft.ui.model.BlockChipUi
import com.carft.ui.model.toChip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SimOutcome { Run, Partial, Unknown, Skip }

data class ConditionCheckUi(val chip: BlockChipUi, val result: TriState)

data class SimulationUiState(
    val name: String = "",
    val input: VehicleState = VehicleState(),
    val triggerChip: BlockChipUi? = null,
    val triggerMet: Boolean = false,
    val conditions: List<ConditionCheckUi> = emptyList(),
    val outcome: SimOutcome = SimOutcome.Skip,
    val detail: List<String> = emptyList(),
    val needsSeat: Boolean = false,
    val needsDriving: Boolean = false
)

class SimulationViewModel(private val option: Option) : ViewModel() {
    private val adapter = FakeVehicleAdapter()
    private val conditionEvaluator = ConditionEvaluator(adapter)
    private val engine = RuleEngine(conditionEvaluator, adapter)
    private val input = MutableStateFlow(assumeTrigger(VehicleState(interiorTemp = 8f, hour = 8, wiperOn = false)))

    private val _state = MutableStateFlow(SimulationUiState(name = option.name, input = input.value))
    val state: StateFlow<SimulationUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            input.collectLatest { car ->
                val conditions = option.rule.conditions.map { ConditionCheckUi(it.toChip(), conditionEvaluator.evaluate(it, car)) }
                val result = engine.evaluate(option.rule, car)
                val triggerMet = !(result is ExecutionResult.NotExecuted && result.reason == "트리거 미충족")
                _state.value = SimulationUiState(
                    name = option.name,
                    input = car,
                    triggerChip = option.rule.trigger.toChip(),
                    triggerMet = triggerMet,
                    conditions = conditions,
                    outcome = when (result) {
                        is ExecutionResult.Success -> SimOutcome.Run
                        is ExecutionResult.PartialSuccess -> SimOutcome.Partial
                        is ExecutionResult.Deferred -> SimOutcome.Unknown
                        is ExecutionResult.NotExecuted -> SimOutcome.Skip
                    },
                    detail = detail(result, triggerMet),
                    needsSeat = option.rule.trigger is TriggerBlock.SeatOccupied || option.rule.trigger is TriggerBlock.SeatBeltBuckled ||
                        option.rule.conditions.any { it is ConditionBlock.IsSeated },
                    needsDriving = true
                )
            }
        }
    }

    fun changeTemp(delta: Float) = input.update { it.copy(interiorTemp = (it.interiorTemp ?: 20f) + delta) }

    fun setTempUnknown() = input.update { it.copy(interiorTemp = null) }

    fun setHour(hour: Int) = input.update { it.copy(hour = hour, minute = if (hour < 12) 10 else 0, dayOfWeek = if (hour < 12) 1 else 6) }

    fun setWiper(on: Boolean) = input.update { it.copy(wiperOn = on) }

    fun setDriving(driving: Boolean) = input.update { it.copy(isDriving = driving) }

    fun setDriverSeat(on: Boolean) = input.update { it.copy(driverSeatOccupied = on, driverBeltBuckled = on) }

    fun setRearBelt(on: Boolean) = input.update { it.copy(rearRightSeatOccupied = on, rearRightBeltBuckled = on) }

    private fun assumeTrigger(s: VehicleState): VehicleState = when (val t = option.rule.trigger) {
        is TriggerBlock.SeatBeltBuckled -> if (t.seat == "rearRight") s.copy(rearRightBeltBuckled = true, rearRightSeatOccupied = true) else s.copy(driverBeltBuckled = true, driverSeatOccupied = true)
        is TriggerBlock.SeatOccupied -> if (t.seat == "rearRight") s.copy(rearRightSeatOccupied = true) else s.copy(driverSeatOccupied = true)
        is TriggerBlock.WiperOn -> s.copy(wiperOn = true)
        is TriggerBlock.TimeOfDay -> s.copy(hour = t.hour, minute = t.minute)
        is TriggerBlock.TemperatureThreshold -> s.copy(interiorTemp = minOf(8f, t.threshold))
        is TriggerBlock.Manual -> s
    }

    private fun detail(result: ExecutionResult, triggerMet: Boolean): List<String> = when {
        !triggerMet -> listOf("상황이 일어나지 않아서 실행하지 않아요")
        result is ExecutionResult.Success -> result.actions.filterIsInstance<ActionExecutionResult.Success>().map { it.result }
        result is ExecutionResult.PartialSuccess -> result.actions.map {
            when (it) {
                is ActionExecutionResult.Success -> it.result
                is ActionExecutionResult.Deferred -> "미룸 · ${it.reason}"
                is ActionExecutionResult.Conflict -> "충돌 · ${it.reason}"
            }
        }
        result is ExecutionResult.Deferred -> listOf(result.reason)
        result is ExecutionResult.NotExecuted -> listOf(result.reason)
        else -> emptyList()
    }
}
