package com.carft.infrastructure.vehicle

import com.carft.domain.model.ActionBlock
import com.carft.domain.model.VehicleState
import kotlinx.coroutines.delay

class FakeVehicleAdapter : VehicleAdapter {
    private val fakeState = mutableMapOf<String, Any?>(
        "driverSeatOccupied" to false,
        "driverBeltBuckled" to false,
        "rearRightSeatOccupied" to false,
        "rearRightBeltBuckled" to false,
        "interiorTemp" to null,
        "isDriving" to false,
        "hvacTargetTemp" to 20f,
        "driverSeatHeat" to 0,
        "rearRightSeatHeat" to 0,
        "ambientLight" to "off"
    )

    override suspend fun getSeatOccupancy(seat: String): Boolean? {
        delay(50) // 센서 딜레이 시뮬레이션
        return fakeState["${seat}SeatOccupied"] as? Boolean
    }

    override suspend fun getSeatBeltState(seat: String): Boolean? {
        delay(50)
        return fakeState["${seat}BeltBuckled"] as? Boolean
    }

    override suspend fun getInteriorTemp(): Float? {
        delay(50)
        return fakeState["interiorTemp"] as? Float
    }

    override suspend fun executeAction(action: ActionBlock): String {
        delay(100) // 액추에이터 반응 시뮬레이션
        return when (action) {
            is ActionBlock.SetHvacTemperature -> {
                fakeState["hvacTargetTemp"] = action.targetTemp
                "공조 설정: ${action.targetTemp}°C"
            }
            is ActionBlock.SetSeatHeat -> {
                fakeState["${action.seat}SeatHeat"] = action.level
                "${action.seat}석 열선: ${action.level}단"
            }
            is ActionBlock.SetAmbientLight -> {
                fakeState["ambientLight"] = action.color
                "조명: ${action.color}"
            }
            is ActionBlock.PlayTextToSpeech -> {
                "음성: ${action.text}"
            }
        }
    }

    fun setFakeSeatOccupancy(seat: String, occupied: Boolean) {
        fakeState["${seat}SeatOccupied"] = occupied
    }

    fun setFakeSeatBelt(seat: String, buckled: Boolean) {
        fakeState["${seat}BeltBuckled"] = buckled
    }

    fun setFakeInteriorTemp(temp: Float?) {
        fakeState["interiorTemp"] = temp
    }

    fun setFakeDriving(isDriving: Boolean) {
        fakeState["isDriving"] = isDriving
    }

    fun getState(): Map<String, Any?> = fakeState.toMap()
}
