package com.carft.feature.priority

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun PriorityRoute(onBack: () -> Unit) {
    val viewModel = carftViewModel { PriorityViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    PriorityScreen(state = state, onBack = onBack, onMoveUp = viewModel::moveUp, onMoveDown = viewModel::moveDown)
}

@Composable
fun ConflictRoute(onBack: () -> Unit) {
    CarftScreen(topBar = { CarftTopBar("충돌 해결", subtitle = "두 옵션이 같은 동작을 다르게 바꾸려 했어요", onBack = onBack) }) {
        ScreenPlaceholder("18:1328")
    }
}
