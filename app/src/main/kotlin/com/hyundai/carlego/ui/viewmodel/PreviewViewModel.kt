package com.hyundai.carlego.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyundai.carlego.domain.model.ExecutionResult
import com.hyundai.carlego.domain.model.Option
import com.hyundai.carlego.domain.model.VehicleState
import com.hyundai.carlego.domain.rule.ConditionEvaluator
import com.hyundai.carlego.domain.rule.RuleEngine
import com.hyundai.carlego.infrastructure.vehicle.FakeVehicleAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreviewViewModel : ViewModel() {
    private val adapter = FakeVehicleAdapter()
    private val conditionEvaluator = ConditionEvaluator(adapter)
    private val ruleEngine = RuleEngine(conditionEvaluator, adapter)

    private val _fakeVehicleState = MutableStateFlow(VehicleState.default())
    val fakeVehicleState: StateFlow<VehicleState> = _fakeVehicleState.asStateFlow()

    private val _simulationResult = MutableStateFlow<ExecutionResult?>(null)
    val simulationResult: StateFlow<ExecutionResult?> = _simulationResult.asStateFlow()

    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    fun setFakeInteriorTemp(temp: Float?) {
        _fakeVehicleState.value = _fakeVehicleState.value.copy(interiorTemp = temp)
        adapter.setFakeInteriorTemp(temp)
    }

    fun setFakeDriverBelt(buckled: Boolean) {
        _fakeVehicleState.value = _fakeVehicleState.value.copy(driverBeltBuckled = buckled)
        adapter.setFakeSeatBelt("driver", buckled)
    }

    fun setFakeRearRightBelt(buckled: Boolean) {
        _fakeVehicleState.value = _fakeVehicleState.value.copy(rearRightBeltBuckled = buckled)
        adapter.setFakeSeatBelt("rearRight", buckled)
    }

    fun setFakeSeatOccupied(seat: String, occupied: Boolean) {
        if (seat == "driver") {
            _fakeVehicleState.value = _fakeVehicleState.value.copy(driverSeatOccupied = occupied)
        } else if (seat == "rearRight") {
            _fakeVehicleState.value = _fakeVehicleState.value.copy(rearRightSeatOccupied = occupied)
        }
        adapter.setFakeSeatOccupancy(seat, occupied)
    }

    fun setFakeDriving(isDriving: Boolean) {
        _fakeVehicleState.value = _fakeVehicleState.value.copy(isDriving = isDriving)
        adapter.setFakeDriving(isDriving)
    }

    fun simulate(option: Option) {
        viewModelScope.launch {
            _isSimulating.value = true
            try {
                val result = ruleEngine.evaluate(option.rule, _fakeVehicleState.value)
                _simulationResult.value = result
            } finally {
                _isSimulating.value = false
            }
        }
    }

    fun reset() {
        _fakeVehicleState.value = VehicleState.default()
        _simulationResult.value = null
        adapter.setFakeInteriorTemp(null)
        adapter.setFakeSeatBelt("driver", false)
        adapter.setFakeSeatBelt("rearRight", false)
        adapter.setFakeSeatOccupancy("driver", false)
        adapter.setFakeSeatOccupancy("rearRight", false)
        adapter.setFakeDriving(false)
    }
}
