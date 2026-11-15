package com.carft.feature.home

import androidx.compose.runtime.Composable
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun OptionDetailRoute(optionId: String, onBack: () -> Unit) {
    CarftScreen(topBar = { CarftTopBar("옵션 상세", onBack = onBack) }) {
        ScreenPlaceholder("16:883")
    }
}
