package com.carft.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.data.ConflictPolicy
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftToggle
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.theme.CarftTheme
import com.carft.ui.component.PlaceholderLine
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onToggleWatching: (Boolean) -> Unit,
    onToggleVoice: (Boolean) -> Unit,
    onSelectConflictPolicy: (ConflictPolicy) -> Unit,
    onReplayTutorial: () -> Unit,
    onResetAll: () -> Unit
) {
    CarftScreen(topBar = { CarftTopBar("설정", onBack = onBack) }) {
        ScreenPlaceholder("17:1499") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PlaceholderLine("옵션 감시")
                CarftToggle(state.watching, onToggleWatching)
                PlaceholderLine("주행 중 음성 안내")
                CarftToggle(state.voiceWhileDriving, onToggleVoice)
            }
            PlaceholderLine("충돌 처리: ${state.conflictPolicy}")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CarftButton("매번 물어보기", { onSelectConflictPolicy(ConflictPolicy.AskEveryTime) }, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                CarftButton("순위대로 자동 적용", { onSelectConflictPolicy(ConflictPolicy.FollowPriority) }, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                CarftButton("튜토리얼 다시 보기", onReplayTutorial, size = CarftButtonSize.Small, style = CarftButtonStyle.Secondary)
                CarftButton("처음 설치한 상태로", onResetAll, size = CarftButtonSize.Small, style = CarftButtonStyle.DangerSoft)
            }
            state.permissions.forEach { PlaceholderLine("${it.name} · ${it.usage}") }
            PlaceholderLine(state.appVersion)
        }
    }
}

@Preview(widthDp = 1700, heightDp = 900)
@Composable
private fun SettingsScreenPreview() {
    CarftTheme { SettingsScreen(SettingsUiState(), {}, {}, {}, {}, {}, {}) }
}
