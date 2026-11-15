package com.carft.feature.simulation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.carft.app.Routes
import com.carft.appContainer

@Composable
fun SimulationRoute(optionId: String, onClose: () -> Unit) {
    val container = LocalContext.current.appContainer
    val option = if (optionId == Routes.DRAFT) container.draft.draft.value.toOption() else container.options.get(optionId)
    if (option == null) {
        onClose()
        return
    }
    PreviewScreen(option = option, onClose = onClose)
}
