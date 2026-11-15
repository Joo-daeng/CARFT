package com.carft.feature.simulation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.Routes
import com.carft.app.carftViewModel
import com.carft.appContainer

@Composable
fun SimulationRoute(optionId: String, onClose: () -> Unit) {
    val container = LocalContext.current.appContainer
    val option = if (optionId == Routes.DRAFT) container.draft.draft.value.toOption() else container.options.get(optionId)
    if (option == null) {
        LaunchedEffect(Unit) { onClose() }
        return
    }
    val viewModel = carftViewModel(key = "sim-${option.id}") { SimulationViewModel(option) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    SimulationScreen(
        state = state,
        onClose = onClose,
        onTemp = viewModel::changeTemp,
        onTempUnknown = viewModel::setTempUnknown,
        onHour = viewModel::setHour,
        onWiper = viewModel::setWiper,
        onDriving = viewModel::setDriving,
        onDriverSeat = viewModel::setDriverSeat,
        onRearBelt = viewModel::setRearBelt
    )
}
