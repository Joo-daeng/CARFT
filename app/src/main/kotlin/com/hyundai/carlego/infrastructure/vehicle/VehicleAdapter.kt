package com.hyundai.carlego.infrastructure.vehicle

import com.hyundai.carlego.domain.model.ActionBlock
import com.hyundai.carlego.domain.model.VehicleState

interface VehicleAdapter {
    suspend fun getSeatOccupancy(seat: String): Boolean?
    suspend fun getSeatBeltState(seat: String): Boolean?
    suspend fun getInteriorTemp(): Float?
    suspend fun executeAction(action: ActionBlock): String
}
