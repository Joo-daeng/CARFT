package com.carft.infrastructure.pleos

import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.control.LightSwitch
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import android.util.Log
import com.carft.domain.model.ActionBlock
import com.carft.infrastructure.vehicle.VehicleAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.abs

class PleosVehicleAdapter(
    private val pleos: PleosVehicle,
    private val speaker: PleosSpeaker
) : VehicleAdapter {
    private val vehicle get() = pleos.vehicle

    private var hvacPowerAreas: List<SeatArea>? = null
    private var hvacTempBounds: Map<SeatArea, AreaBounds<Float>>? = null
    private var seatTempBounds: Map<SeatArea, AreaBounds<Int>>? = null
    private var ambientSwitchAreas: List<SeatArea>? = null
    private var ambientColorAreas: List<SeatArea>? = null

    override suspend fun getSeatOccupancy(seat: String): Boolean? = PleosVehicle.occupied(call { ok, fail ->
        vehicle.getSeat().getCurrentSeatOccupancy(PleosVehicle.seatArea(seat), ok, fail)
    })

    override suspend fun getSeatBeltState(seat: String): Boolean? = call { ok, fail ->
        vehicle.getSeat().getCurrentSeatBeltBuckledState(PleosVehicle.seatArea(seat), ok, fail)
    }

    override suspend fun getInteriorTemp(): Float? = call { ok, fail ->
        vehicle.getHVAC().getHvacTemperatureCurrent(PleosVehicle.DRIVER, TemperatureUnit.CELSIUS, ok, fail)
    }

    override suspend fun executeAction(action: ActionBlock): String = when (action) {
        is ActionBlock.SetHvacTemperature -> setHvacTemperature(action.targetTemp)
        is ActionBlock.SetSeatHeat -> setSeatHeat(action.seat, action.level)
        is ActionBlock.SetAmbientLight -> setAmbientLight(action.color)
        is ActionBlock.PlayTextToSpeech -> {
            speaker.speak(action.text)
            "음성 안내: ${action.text}"
        }
        is ActionBlock.SteeringHeat -> setSteeringHeat()
        is ActionBlock.CabinLight -> setCabinLight()
        is ActionBlock.NaviDestination -> throw UnsupportedOperationException("내비 목적지 미지원")
    }

    private suspend fun setSteeringHeat(): String {
        val wheel = vehicle.getSteeringWheel()
        val range = call<AreaBounds<Int>> { ok, fail -> wheel.checkHvacSteeringWheelHeatCapabilities({ _: CapabilityConfig, b -> ok(b) }, fail) }
        val value = maxOf(range.maxValue, 1)
        call<Any?> { ok, fail -> wheel.setHvacSteeringWheelHeat(value, { ok(it) }, fail) }
        val applied = verify(expected = value, match = { it == value }) {
            call<Int?> { ok, fail -> wheel.getHvacSteeringWheelHeatState(ok, fail) }
        }
        return "핸들 열선 ${applied}단 반영"
    }

    private suspend fun setCabinLight(): String {
        val light = vehicle.getLight()
        call<Any?> { ok, fail -> light.setCabinLightSwitch(LightSwitch.ON, { ok(it) }, fail) }
        verify(expected = LightSwitch.ON, match = { it == LightSwitch.ON }) {
            call<LightSwitch?> { ok, fail -> light.getCabinLightSwitch(ok, fail) }
        }
        return "실내등 켬"
    }

    private suspend fun setHvacTemperature(target: Float): String {
        val hvac = vehicle.getHVAC()
        val powerAreas = hvacPowerAreas ?: call<List<SeatArea>> { ok, fail ->
            hvac.checkHvacPowerCapabilities({ _: CapabilityConfig, list -> ok(list) }, fail)
        }.also { hvacPowerAreas = it }
        powerAreas.firstOrNull()?.let { area ->
            call<Any?> { ok, fail -> hvac.setHvacPower(area, true, { _, v -> ok(v) }, fail) }
        }

        val bounds = hvacTempBounds ?: call<Map<SeatArea, AreaBounds<Float>>> { ok, fail ->
            hvac.checkHvacTemperatureCapabilities({ _: CapabilityConfig, map -> ok(map) }, fail)
        }.also { hvacTempBounds = it; Log.i(TAG, "hvac temp areas=${it.keys.map(::areaName)}") }
        val (area, range) = pick(bounds) ?: throw IllegalStateException("공조 온도 미지원")
        val value = target.coerceIn(range.minValue, range.maxValue)

        call<Any?> { ok, fail ->
            hvac.setHvacTemperature(area, value, TemperatureUnit.CELSIUS, { _, v -> ok(v) }, fail)
        }
        val applied = verify(expected = value, match = { abs(it - value) < 0.6f }) {
            pleos.state.value.appliedHvacTemp ?: call<Float?> { ok, fail -> hvac.getHvacTemperature(area, TemperatureUnit.CELSIUS, ok, fail) }
        }
        return "공조 ${applied}°C 반영"
    }

    private suspend fun setSeatHeat(seat: String, level: Int): String {
        val seatApi = vehicle.getSeat()
        val bounds = seatTempBounds ?: call<Map<SeatArea, AreaBounds<Int>>> { ok, fail ->
            seatApi.checkSeatTemperatureCapabilities({ _: CapabilityConfig, map -> ok(map) }, fail)
        }.also { seatTempBounds = it; Log.i(TAG, "seat temp areas=${it.mapKeys { e -> areaName(e.key) }}") }
        val wanted = PleosVehicle.seatArea(seat)
        val (area, range) = bounds.entries.firstOrNull { covers(it.key, wanted) }?.toPair()
            ?: throw IllegalStateException("${seatLabel(seat)} 열선 미지원")
        val value = level.coerceIn(maxOf(range.minValue, 0), range.maxValue)

        call<Any?> { ok, fail -> seatApi.setHvacSeatTemperature(area, value, { _, v -> ok(v) }, fail) }
        val applied = verify(expected = value, match = { it == value }) {
            call<Int?> { ok, fail -> seatApi.getHvacSeatTemperature(area, ok, fail) }
        }
        return "${seatLabel(seat)} 열선 ${applied}단 반영"
    }

    private suspend fun setAmbientLight(color: String): String {
        val light = vehicle.getLight()
        val switchArea = (ambientSwitchAreas ?: call<List<SeatArea>> { ok, fail ->
            light.checkAmbientLightSwitchCapabilities({ _: CapabilityConfig, map -> ok(map.keys.toList()) }, fail)
        }.also { ambientSwitchAreas = it }).firstOrNull()
        val colorArea = (ambientColorAreas ?: call<List<SeatArea>> { ok, fail ->
            light.checkAmbientLightBrightnessColorCapabilities({ _: CapabilityConfig, list -> ok(list) }, fail)
        }.also { ambientColorAreas = it }).firstOrNull()
        if (switchArea == null && colorArea == null) throw IllegalStateException("앰비언트 조명 미지원")

        switchArea?.let { area ->
            call<Any?> { ok, fail -> light.setAmbientLightSwitch(area, LightSwitch.ON, { _, v -> ok(v) }, fail) }
        }
        colorArea?.let { area ->
            val argb = argb(color)
            call<Any?> { ok, fail -> light.setAmbientLightBrightnessColor(area, argb, { _, v -> ok(v) }, fail) }
            verify(expected = argb, match = { (it and 0xFFFFFF) == (argb and 0xFFFFFF) }) {
                call<Int?> { ok, fail -> light.getAmbientLightBrightnessColor(area, ok, fail) }
            }
        }
        return "앰비언트 조명 $color 켬"
    }

    private suspend fun <T> verify(expected: T, match: (T) -> Boolean, read: suspend () -> T?): String {
        var last: T? = null
        var readable = false
        repeat(VERIFY_TRIES) {
            val value = runCatching { read() }
            if (value.isSuccess) {
                readable = true
                last = value.getOrNull()
                if (last != null && match(last!!)) return last.toString()
            }
            delay(VERIFY_INTERVAL_MS)
        }
        if (!readable) return "$expected(반영 확인 불가)"
        throw IllegalStateException("반영 안 됨 (요청 $expected, 현재 ${last ?: "모름"})")
    }

    private fun <T> pick(bounds: Map<SeatArea, AreaBounds<T>>): Pair<SeatArea, AreaBounds<T>>? =
        bounds.entries.firstOrNull { covers(it.key, PleosVehicle.DRIVER) }?.toPair()
            ?: bounds.entries.firstOrNull()?.toPair()

    private fun covers(area: SeatArea, wanted: SeatArea) = area.values.any { it in wanted.values }

    private fun areaName(area: SeatArea) = area.values.joinToString("|")

    private suspend fun <T> call(block: (ok: (T) -> Unit, fail: (Exception) -> Unit) -> Unit): T =
        withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                block(
                    { if (cont.isActive) cont.resume(it) },
                    { if (cont.isActive) cont.resumeWithException(it) }
                )
            }
        }

    private fun seatLabel(seat: String) = if (seat == "rearRight") "뒷좌석 오른쪽" else "운전석"

    private fun argb(color: String): Int = when (color) {
        "red" -> 0xFFFF3B30.toInt()
        "green" -> 0xFF34C759.toInt()
        "orange" -> 0xFFFF9500.toInt()
        "white" -> 0xFFFFFFFF.toInt()
        else -> 0xFF1F75FE.toInt()
    }

    companion object {
        private const val TAG = "Carft.Pleos"
        private const val TIMEOUT_MS = 3_000L
        private const val VERIFY_TRIES = 6
        private const val VERIFY_INTERVAL_MS = 250L
    }
}
