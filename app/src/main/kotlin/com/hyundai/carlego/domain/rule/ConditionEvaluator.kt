package com.hyundai.carlego.domain.rule

import com.hyundai.carlego.domain.model.ConditionBlock
import com.hyundai.carlego.domain.model.TriState
import com.hyundai.carlego.domain.model.VehicleState
import com.hyundai.carlego.infrastructure.vehicle.VehicleAdapter

class ConditionEvaluator(private val adapter: VehicleAdapter) {
    suspend fun evaluate(block: ConditionBlock, state: VehicleState): TriState {
        return when (block) {
            is ConditionBlock.IsSeated -> {
                val occupied = when (block.seat) {
                    "driver" -> state.driverSeatOccupied
                    "rearRight" -> state.rearRightSeatOccupied
                    else -> null
                }
                when (occupied) {
                    true -> TriState.TRUE
                    false -> TriState.FALSE
                    null -> TriState.UNKNOWN
                }
            }

            is ConditionBlock.TemperatureRange -> {
                val temp = state.interiorTemp
                when {
                    temp == null -> TriState.UNKNOWN
                    temp in block.minTemp..block.maxTemp -> TriState.TRUE
                    else -> TriState.FALSE
                }
            }

            is ConditionBlock.IsDay -> {
                if (state.hour == block.hour) TriState.TRUE else TriState.FALSE
            }
        }
    }
}
