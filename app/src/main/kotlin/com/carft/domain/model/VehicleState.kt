package com.carft.domain.model

data class VehicleState(
    val interiorTemp: Float? = null,
    val driverSeatOccupied: Boolean? = null,
    val driverBeltBuckled: Boolean? = null,
    val rearRightSeatOccupied: Boolean? = null,
    val rearRightBeltBuckled: Boolean? = null,
    val wiperOn: Boolean? = null,
    val appliedHvacTemp: Float? = null,
    val isDriving: Boolean = false,
    val hour: Int = 8,
    val targetHvacTemp: Float = 20f,
    val seatHeatLevel: Map<String, Int> = mapOf("driver" to 0, "rearRight" to 0),
    val ambientLightColor: String = "off",
    val ambientLightBrightness: Int = 0
) {
    companion object {
        fun default() = VehicleState()
    }
}

data class DragState(
    val blockId: String? = null,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val isDragging: Boolean = false
)

data class EditorState(
    val blocks: List<Block> = emptyList(),
    val selectedBlockId: String? = null,
    val dragState: DragState = DragState(),
    val combinationLogic: CombinationLogic = CombinationLogic("AND")
)

data class PreviewState(
    val fakeVehicleState: VehicleState = VehicleState.default(),
    val isSimulating: Boolean = false,
    val simulationResult: ExecutionResult? = null
)
