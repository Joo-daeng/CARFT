package com.carft.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.icon.CarftIcon
import com.carft.ui.component.PlaceholderLine
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onStart: () -> Unit,
    onBackStep: () -> Unit,
    onConfirm: () -> Unit
) {
    CarftScreen {
        if (state.step == 0) {
            ScreenPlaceholder("11:663") {
                PlaceholderLine("블록을 끼우면 내 차가 알아서 움직여요")
                state.legend.forEach { PlaceholderLine("${it.kind.label} — ${it.description}") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.exampleRule.all.forEach { BlockChip(it.text, it.kind, icon = it.icon) }
                }
                CarftButton("시작하기", onStart, size = CarftButtonSize.Large, icon = CarftIcon.Arrow)
            }
        } else {
            ScreenPlaceholder("11:813") {
                PlaceholderLine("시작하기 전에 알아 두세요")
                state.notices.forEach { PlaceholderLine("${it.title} — ${it.description}") }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CarftButton("이전으로", onBackStep, style = CarftButtonStyle.Ghost)
                    CarftButton("확인하고 시작하기", onConfirm)
                }
            }
        }
    }
}
