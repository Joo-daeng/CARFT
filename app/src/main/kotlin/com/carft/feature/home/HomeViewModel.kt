package com.carft.feature.home

import androidx.lifecycle.ViewModel
import com.carft.app.AppContainer
import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.Option
import com.carft.domain.model.VehicleState
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val container: AppContainer) : ViewModel() {
    val options: StateFlow<List<Option>> = container.options.options
    val vehicleState: StateFlow<VehicleState> = container.vehicle.state
    val sdkError: StateFlow<String?> = container.vehicle.lastError
    val records: StateFlow<List<ExecutionRecord>> = container.history.records

    fun runNow(option: Option) = container.runner.runNow(option)
}
