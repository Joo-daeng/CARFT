package com.carft.feature.demopanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftSegmented
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftType
import com.carft.ui.component.PlaceholderLine
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun DemoPanel(
    state: DemoPanelUiState,
    onClose: () -> Unit,
    onSetDriving: (Boolean) -> Unit,
    onTempUp: () -> Unit,
    onTempDown: () -> Unit,
    onTempUnknown: () -> Unit,
    onSelectTime: (TimePreset) -> Unit,
    onToggleSituation: (DemoSituation) -> Unit,
    onResetSituations: () -> Unit,
    onFirstRun: () -> Unit,
    onReturning: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        Column(
            Modifier.width(620.dp).fillMaxHeight().background(CarftColors.Background).padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("시연 패널", style = CarftType.TitleM, color = CarftColors.Demo, modifier = Modifier.weight(1f))
                CarftButton("닫기", onClose, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
            }
            ScreenPlaceholder("20:1764") {
                CarftSegmented(listOf("정차 · P", "주행 · D"), if (state.driving) 1 else 0, { onSetDriving(it == 1) })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    CarftButton("−", onTempDown, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                    PlaceholderLine(state.interiorTemp?.let { "%.1f°C".format(it) } ?: "모름")
                    CarftButton("+", onTempUp, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                    CarftButton("센서 모름", onTempUnknown, size = CarftButtonSize.Small, style = CarftButtonStyle.Ghost)
                }
                CarftSegmented(TimePreset.values().map { it.label }, state.time.ordinal, { onSelectTime(TimePreset.values()[it]) })
                DemoSituation.values().forEach { situation ->
                    CarftButton(
                        situation.label + if (situation in state.active) " · 켜짐" else "",
                        { onToggleSituation(situation) },
                        size = CarftButtonSize.Small,
                        style = if (situation in state.active) CarftButtonStyle.Primary else CarftButtonStyle.Outline,
                        icon = situation.icon
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CarftButton("상황 초기화", onResetSituations, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                    CarftButton("첫 실행", onFirstRun, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                    CarftButton("이후 실행", onReturning, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                }
            }
        }
    }
}
