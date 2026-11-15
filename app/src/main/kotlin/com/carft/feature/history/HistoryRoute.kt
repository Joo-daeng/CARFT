package com.carft.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel

@Composable
fun HistoryRoute(onBack: () -> Unit) {
    val viewModel = carftViewModel { HistoryViewModel(it.history) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    HistoryScreen(state = state, onBack = onBack, onSelectFilter = viewModel::selectFilter)
}
