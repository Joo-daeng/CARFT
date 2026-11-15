package com.carft.infrastructure.pleos

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.constant.area.SeatValue
import ai.pleos.playground.vehicle.constant.area.WindowValue
import ai.pleos.playground.vehicle.constant.control.WiperState
import ai.pleos.playground.vehicle.listener.HvacTemperatureListener
import ai.pleos.playground.vehicle.listener.SeatBeltBuckledStateListener
import ai.pleos.playground.vehicle.listener.SeatOccupancyListener
import ai.pleos.playground.vehicle.listener.WiperStateListener
import android.content.Context
import android.util.Log
import com.carft.domain.model.VehicleState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PleosVehicle(context: Context) {
    val vehicle = Vehicle(context.applicationContext)

    private val _state = MutableStateFlow(VehicleState.default())
    val state: StateFlow<VehicleState> = _state.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    private val driverOccupancy = occupancyListener("driver") { v -> _state.update { it.copy(driverSeatOccupied = v) } }
    private val rearRightOccupancy = occupancyListener("rearRight") { v -> _state.update { it.copy(rearRightSeatOccupied = v) } }
    private val driverBelt = beltListener("driver") { v -> _state.update { it.copy(driverBeltBuckled = v) } }
    private val rearRightBelt = beltListener("rearRight") { v -> _state.update { it.copy(rearRightBeltBuckled = v) } }

    private val interiorTemp = object : HvacTemperatureListener {
        override fun onHvacTemperatureUpdated(temperature: Float?) {
            _state.update { it.copy(interiorTemp = temperature) }
        }

        override fun onFailed(e: Exception) = fail("interiorTemp", e)
    }

    private val hvacTarget = object : HvacTemperatureListener {
        override fun onHvacTemperatureUpdated(temperature: Float?) {
            _state.update { it.copy(appliedHvacTemp = temperature) }
        }

        override fun onFailed(e: Exception) = fail("hvacTarget", e)
    }

    private val wiper = object : WiperStateListener {
        override fun onWiperStateUpdated(state: WiperState?) {
            _state.update { it.copy(wiperOn = state?.let { s -> s == WiperState.ON }) }
        }

        override fun onFailed(e: Exception) = fail("wiper", e)
    }

    fun start() {
        vehicle.initialize()
        val seat = vehicle.getSeat()
        seat.registerSeatOccupancy(DRIVER, driverOccupancy)
        seat.registerSeatOccupancy(REAR_RIGHT, rearRightOccupancy)
        seat.registerSeatBeltBuckledState(DRIVER, driverBelt)
        seat.registerSeatBeltBuckledState(REAR_RIGHT, rearRightBelt)
        vehicle.getHVAC().registerHvacTemperatureCurrent(DRIVER, interiorTemp)
        vehicle.getWiper().registerWiperState(FRONT, wiper)
        vehicle.getHVAC().registerHvacTemperature(DRIVER, hvacTarget)
    }

    fun stop() {
        val seat = vehicle.getSeat()
        seat.unregisterSeatOccupancy(DRIVER, driverOccupancy)
        seat.unregisterSeatOccupancy(REAR_RIGHT, rearRightOccupancy)
        seat.unregisterSeatBeltBuckledState(DRIVER, driverBelt)
        seat.unregisterSeatBeltBuckledState(REAR_RIGHT, rearRightBelt)
        vehicle.getHVAC().unregisterHvacTemperatureCurrent(DRIVER, interiorTemp)
        vehicle.getWiper().unregisterWiperState(FRONT, wiper)
        vehicle.getHVAC().unregisterHvacTemperature(DRIVER, hvacTarget)
        vehicle.release()
    }

    fun setDriving(isDriving: Boolean) {
        _state.update { it.copy(isDriving = isDriving) }
    }

    fun simulate(transform: (VehicleState) -> VehicleState) {
        _state.update(transform)
    }

    private fun occupancyListener(name: String, onValue: (Boolean?) -> Unit) = object : SeatOccupancyListener {
        override fun onSeatOccupancyUpdated(occupancy: Boolean?) {
            Log.d(TAG, "occupancy[$name]=$occupancy")
            onValue(occupied(occupancy))
        }
        override fun onFailed(e: Exception) = fail("occupancy", e)
    }

    private fun beltListener(name: String, onValue: (Boolean?) -> Unit) = object : SeatBeltBuckledStateListener {
        override fun onSeatBeltBuckledStateUpdated(state: Boolean?) {
            Log.d(TAG, "belt[$name]=$state")
            onValue(state)
        }
        override fun onFailed(e: Exception) = fail("belt", e)
    }

    private fun fail(source: String, e: Exception) {
        Log.w(TAG, "$source: ${e.message}", e)
        _lastError.value = "$source: ${e.message}"
    }

    companion object {
        private const val TAG = "Carft.Pleos"
        val DRIVER = SeatArea(SeatValue.ROW_1_LEFT)
        val REAR_RIGHT = SeatArea(SeatValue.ROW_2_RIGHT)
        val FRONT = WindowArea(WindowValue.FRONT_WINDSHIELD)

        private const val SDK_OCCUPANCY_INVERTED = true

        fun occupied(sdkValue: Boolean?): Boolean? = sdkValue?.let { if (SDK_OCCUPANCY_INVERTED) !it else it }

        fun seatArea(seat: String): SeatArea = when (seat) {
            "rearRight" -> REAR_RIGHT
            else -> DRIVER
        }
    }
}
