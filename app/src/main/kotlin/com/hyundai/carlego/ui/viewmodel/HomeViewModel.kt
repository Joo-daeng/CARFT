package com.hyundai.carlego.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hyundai.carlego.CarLegoApplication
import com.hyundai.carlego.domain.model.ExecutionRecord
import com.hyundai.carlego.domain.model.Option
import com.hyundai.carlego.domain.model.VehicleState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as CarLegoApplication

    val vehicleState: StateFlow<VehicleState> = app.pleos.state
    val sdkError: StateFlow<String?> = app.pleos.lastError
    val records: StateFlow<List<ExecutionRecord>> = app.runner.records
    val events: SharedFlow<ExecutionRecord> = app.runner.events

    fun syncOptions(options: List<Option>) = app.runner.setOptions(options)

    fun runNow(option: Option) = app.runner.runNow(option)
}
