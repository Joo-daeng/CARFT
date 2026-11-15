package com.carft.feature.demopanel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.app.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DemoPanelViewModel(private val container: AppContainer) : ViewModel() {
    private val local = MutableStateFlow(DemoPanelUiState())
    private val vehicle get() = container.vehicle

    val state: StateFlow<DemoPanelUiState> = combine(local, vehicle.state) { ui, car ->
        ui.copy(
            driving = car.isDriving,
            interiorTemp = car.interiorTemp,
            active = buildSet {
                if (car.rearRightBeltBuckled == true) add(DemoSituation.RearBelt)
                if (car.driverSeatOccupied == true) add(DemoSituation.DriverSeat)
                if (car.wiperOn == true) add(DemoSituation.Wiper)
                if (car.hour == 7) add(DemoSituation.Morning)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DemoPanelUiState())

    fun show() = local.update { it.copy(visible = true) }

    fun hide() = local.update { it.copy(visible = false) }

    fun setDriving(value: Boolean) = vehicle.setDriving(value)

    fun changeTemp(delta: Float) = vehicle.simulate { it.copy(interiorTemp = (it.interiorTemp ?: 20f) + delta) }

    fun setTempUnknown() = vehicle.simulate { it.copy(interiorTemp = null) }

    fun selectTime(preset: TimePreset) {
        local.update { it.copy(time = preset) }
        vehicle.simulate { it.copy(hour = preset.hour) }
    }

    fun toggle(situation: DemoSituation) = vehicle.simulate {
        when (situation) {
            DemoSituation.RearBelt -> it.copy(rearRightBeltBuckled = it.rearRightBeltBuckled != true, rearRightSeatOccupied = true)
            DemoSituation.DriverSeat -> it.copy(driverSeatOccupied = it.driverSeatOccupied != true)
            DemoSituation.Wiper -> it.copy(wiperOn = it.wiperOn != true)
            DemoSituation.Morning -> it.copy(hour = if (it.hour == 7) local.value.time.hour else 7)
        }
    }

    fun resetSituations() = vehicle.simulate {
        it.copy(rearRightBeltBuckled = false, driverSeatOccupied = false, wiperOn = false, hour = local.value.time.hour)
    }
}
