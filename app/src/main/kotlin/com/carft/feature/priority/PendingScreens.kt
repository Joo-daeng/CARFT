package com.carft.feature.priority

import androidx.compose.runtime.Composable
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun PriorityRoute(onBack: () -> Unit) {
    CarftScreen(topBar = { CarftTopBar("우선순위", subtitle = "위에 있는 옵션이 먼저 실행돼요", onBack = onBack) }) {
        ScreenPlaceholder("16:1872")
    }
}

@Composable
fun ConflictRoute(onBack: () -> Unit) {
    CarftScreen(topBar = { CarftTopBar("충돌 해결", subtitle = "두 옵션이 같은 동작을 다르게 바꾸려 했어요", onBack = onBack) }) {
        ScreenPlaceholder("18:1328")
    }
}
