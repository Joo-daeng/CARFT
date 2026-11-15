package com.carft.feature.priority

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel

@Composable
fun PriorityRoute(onBack: () -> Unit) {
    val viewModel = carftViewModel { PriorityViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    PriorityScreen(state = state, onBack = onBack, onMoveUp = viewModel::moveUp, onMoveDown = viewModel::moveDown)
}

@Composable
fun ConflictRoute(onBack: () -> Unit, onOpenPriority: () -> Unit, onHome: () -> Unit) {
    val viewModel = carftViewModel { ConflictViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ConflictScreen(state = state, onBack = onBack, onResolve = viewModel::resolve, onOpenPriority = onOpenPriority, onHome = onHome)
}
