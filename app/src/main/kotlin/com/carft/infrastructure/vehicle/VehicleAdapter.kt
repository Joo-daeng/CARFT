package com.carft.infrastructure.vehicle

import com.carft.domain.model.ActionBlock
import com.carft.domain.model.VehicleState

interface VehicleAdapter {
    suspend fun getSeatOccupancy(seat: String): Boolean?
    suspend fun getSeatBeltState(seat: String): Boolean?
    suspend fun getInteriorTemp(): Float?
    suspend fun executeAction(action: ActionBlock): String
}
