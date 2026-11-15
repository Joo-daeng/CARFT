package com.carft.feature.demopanel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DemoPanelRoute(viewModel: DemoPanelViewModel, onFirstRun: () -> Unit, onReturning: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (!state.visible) return
    DemoPanel(
        state = state,
        onClose = viewModel::hide,
        onSetDriving = viewModel::setDriving,
        onTempUp = { viewModel.changeTemp(1f) },
        onTempDown = { viewModel.changeTemp(-1f) },
        onTempUnknown = viewModel::setTempUnknown,
        onSelectTime = viewModel::selectTime,
        onToggleSituation = viewModel::toggle,
        onResetSituations = viewModel::resetSituations,
        onFirstRun = {
            viewModel.hide()
            onFirstRun()
        },
        onReturning = {
            viewModel.hide()
            onReturning()
        }
    )
}
