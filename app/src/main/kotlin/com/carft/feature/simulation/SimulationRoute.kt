package com.carft.feature.simulation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.carft.appContainer

@Composable
fun SimulationRoute(optionId: String, onClose: () -> Unit) {
    val option = LocalContext.current.appContainer.options.get(optionId)
    if (option == null) {
        onClose()
        return
    }
    PreviewScreen(option = option, onClose = onClose)
}
