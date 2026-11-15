package com.carft.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.data.ConflictPolicy
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftSheet
import com.carft.designsystem.component.CarftToggle
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.HorizontalDivider
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.icon.CarftLogo
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftSpacing
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType

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
    var confirmReset by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        CarftScreen(topBar = { CarftTopBar("설정", onBack = onBack) }) {
            Row(
                modifier = Modifier.fillMaxSize().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(CarftSpacing.Column)
            ) {
                CarftCard(Modifier.weight(1f), padding = 0.dp, spacing = 0.dp) {
                    SettingRow(CarftIcon.Eye, "옵션 감시", "끄면 어떤 옵션도 자동으로 실행되지 않아요") {
                        CarftToggle(state.watching, onToggleWatching)
                    }
                    HorizontalDivider()
                    SettingRow(CarftIcon.Speaker, "주행 중 음성 안내", "옵션이 실행되면 짧게 소리로 알려 줘요") {
                        CarftToggle(state.voiceWhileDriving, onToggleVoice)
                    }
                    HorizontalDivider()
                    SettingRow(CarftIcon.Warning, "옵션이 부딪치면", "같은 동작을 다르게 바꾸려 할 때") {}
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = ROW_TEXT_START, end = ROW_H, bottom = 22.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PolicyOption(
                            title = "매번 물어보기",
                            description = "정차 중이면 해결 화면을 보여 줘요",
                            selected = state.conflictPolicy == ConflictPolicy.AskEveryTime,
                            onClick = { onSelectConflictPolicy(ConflictPolicy.AskEveryTime) },
                            modifier = Modifier.weight(1f)
                        )
                        PolicyOption(
                            title = "순위대로 자동 적용",
                            description = "위에 있는 옵션의 값을 그대로 써요",
                            selected = state.conflictPolicy == ConflictPolicy.FollowPriority,
                            onClick = { onSelectConflictPolicy(ConflictPolicy.FollowPriority) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider()
                    SettingRow(CarftIcon.Sparkle, "튜토리얼 다시 보기", "첫 옵션 만들기 안내를 처음부터 다시 봐요") {
                        CarftButton("다시 보기", onReplayTutorial, Modifier.height(60.dp).width(192.dp), style = CarftButtonStyle.Secondary, size = CarftButtonSize.Small)
                    }
                    HorizontalDivider()
                    SettingRow(CarftIcon.History, "처음 설치한 상태로", "저장한 옵션과 기록을 모두 지워요 · 시연용") {
                        CarftButton("되돌리기", { confirmReset = true }, Modifier.height(60.dp).width(192.dp), style = CarftButtonStyle.DangerSoft, size = CarftButtonSize.Small)
                    }
                }
                AppInfoCard(state, Modifier.width(616.dp))
            }
        }

        if (confirmReset) {
            CarftSheet(
                title = "처음 설치한 상태로 되돌릴까요?",
                subtitle = "저장한 옵션과 실행 기록이 모두 지워지고 온보딩부터 다시 시작해요",
                onDismiss = { confirmReset = false },
                leading = CarftIcon.Warning,
                leadingTone = StatusTone.Danger
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CarftButton("취소", { confirmReset = false }, Modifier.weight(1f), style = CarftButtonStyle.Ghost)
                    CarftButton(
                        "되돌리기",
                        {
                            confirmReset = false
                            onResetAll()
                        },
                        Modifier.weight(1f),
                        style = CarftButtonStyle.Danger
                    )
                }
            }
        }
    }
}

private val ROW_H = 32.dp
private val ROW_ICON = 26.dp
private val ROW_GAP = 24.dp
private val ROW_TEXT_START = ROW_H + ROW_ICON + ROW_GAP

@Composable
private fun SettingRow(
    icon: CarftIcon,
    title: String,
    description: String,
    trailing: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = ROW_H, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ROW_GAP)
    ) {
        CarftIconView(icon, size = ROW_ICON, tint = CarftColors.TextSecondary)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = CarftType.TitleS, color = CarftColors.TextPrimary)
            Text(description, style = CarftType.Caption, color = CarftColors.TextSecondary)
        }
        trailing()
    }
}

@Composable
private fun PolicyOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(CarftColors.App, CarftShapes.Small)
            .border(1.5.dp, if (selected) CarftColors.StrokeStrong else CarftColors.Stroke, CarftShapes.Small)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RadioMark(selected)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = CarftType.LabelS, color = CarftColors.TextPrimary)
            Text(description, style = CarftType.Caption, color = CarftColors.TextSecondary)
        }
    }
}

@Composable
private fun RadioMark(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .border(2.dp, if (selected) CarftColors.TextPrimary else CarftColors.TextTertiary, CarftShapes.Pill),
        contentAlignment = Alignment.Center
    ) {
        if (selected) Box(Modifier.size(14.dp).background(CarftColors.TextPrimary, CarftShapes.Pill))
    }
}

@Composable
private fun AppInfoCard(state: SettingsUiState, modifier: Modifier = Modifier) {
    CarftCard(modifier, padding = 32.dp, spacing = 0.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarftLogo(height = 44.dp)
            Text("CARFT", style = CarftType.TitleL, color = CarftColors.TextPrimary)
        }
        Text(
            state.appVersion,
            style = CarftType.Caption,
            color = CarftColors.TextTertiary,
            modifier = Modifier.padding(top = 18.dp, bottom = 20.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CarftColors.App, CarftShapes.Small)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("쓰는 권한", style = CarftType.Caption, color = CarftColors.TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
            state.permissions.forEach { permission ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(permission.name, style = CarftType.Caption, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
                    Text(permission.usage, style = CarftType.Caption, color = CarftColors.TextSecondary)
                }
            }
        }
    }
}

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun SettingsScreenPreview() {
    CarftTheme { SettingsScreen(SettingsUiState(), {}, {}, {}, {}, {}, {}) }
}
