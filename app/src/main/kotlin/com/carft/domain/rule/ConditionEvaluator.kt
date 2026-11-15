package com.carft.domain.rule

import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.TriState
import com.carft.domain.model.VehicleState
import com.carft.domain.model.timeSlotContains
import com.carft.domain.model.weekdayMatches
import com.carft.infrastructure.vehicle.VehicleAdapter

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

            is ConditionBlock.Weekday -> if (weekdayMatches(block.days, state.dayOfWeek)) TriState.TRUE else TriState.FALSE

            is ConditionBlock.TimeSlot -> if (timeSlotContains(block.slot, state.hour)) TriState.TRUE else TriState.FALSE

            is ConditionBlock.WiperOff -> when (state.wiperOn) {
                true -> TriState.FALSE
                false -> TriState.TRUE
                null -> TriState.UNKNOWN
            }
        }
    }
}
