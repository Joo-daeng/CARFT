package com.carft.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel
import com.carft.designsystem.component.CarftBrandBar
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.icon.CarftIcon

@Composable
fun HomeRoute(
    onCreateOption: () -> Unit,
    onOpenEditor: (optionId: String) -> Unit,
    onOpenSimulation: (optionId: String) -> Unit,
    onOpenRecommend: () -> Unit,
    onOpenPriority: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDemoPanel: () -> Unit
) {
    val viewModel = carftViewModel { HomeViewModel(it) }
    val options by viewModel.options.collectAsStateWithLifecycle()
    Column {
        CarftBrandBar {
            CarftButton("추천 옵션", onOpenRecommend, style = CarftButtonStyle.Outline, icon = CarftIcon.Sparkle)
            CarftButton("우선순위", onOpenPriority, style = CarftButtonStyle.Outline, icon = CarftIcon.List)
            CarftButton("실행 기록", onOpenHistory, style = CarftButtonStyle.Outline, icon = CarftIcon.History)
            CarftButton("설정", onOpenSettings, style = CarftButtonStyle.Outline, icon = CarftIcon.Gear)
            CarftButton("시연", onOpenDemoPanel, style = CarftButtonStyle.Ghost)
        }
        HomeScreen(
            options = options,
            viewModel = viewModel,
            onCreateOption = onCreateOption,
            onEditOption = { index -> options.getOrNull(index)?.let { onOpenEditor(it.id) } },
            onTestOption = { index -> options.getOrNull(index)?.let { onOpenSimulation(it.id) } }
        )
    }
}
