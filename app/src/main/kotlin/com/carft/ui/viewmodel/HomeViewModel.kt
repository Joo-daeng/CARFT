package com.carft.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.carft.CarftApplication
import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.Option
import com.carft.domain.model.VehicleState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as CarftApplication

    val vehicleState: StateFlow<VehicleState> = app.pleos.state
    val sdkError: StateFlow<String?> = app.pleos.lastError
    val records: StateFlow<List<ExecutionRecord>> = app.runner.records
    val events: SharedFlow<ExecutionRecord> = app.runner.events

    fun syncOptions(options: List<Option>) = app.runner.setOptions(options)

    fun runNow(option: Option) = app.runner.runNow(option)
}
