package com.carft.feature.history

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftSegmented
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.theme.CarftTheme
import com.carft.ui.component.PlaceholderLine
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onBack: () -> Unit,
    onSelectFilter: (HistoryFilter) -> Unit
) {
    CarftScreen(topBar = {
        CarftTopBar("실행 기록", onBack = onBack) {
            Row {
                CarftSegmented(
                    options = HistoryFilter.values().map { it.label },
                    selectedIndex = state.filter.ordinal,
                    onSelect = { onSelectFilter(HistoryFilter.values()[it]) },
                    modifier = Modifier.width(260.dp)
                )
            }
        }
    }) {
        ScreenPlaceholder("17:1300") {
            state.entries.forEach { PlaceholderLine("${it.title} · ${it.status.label} · ${it.summary} · ${it.timeLabel}") }
            PlaceholderLine("이번 주 실행 ${state.weekly.executed} · 일부만 ${state.weekly.partial} · 충돌 ${state.weekly.conflicts}")
        }
    }
}

@Preview(widthDp = 1700, heightDp = 900)
@Composable
private fun HistoryScreenPreview() {
    CarftTheme {
        HistoryScreen(
            state = HistoryUiState(
                entries = listOf(
                    HistoryEntryUi("1", "하린이 아침", OptionStatus.Success, false, "정상 실행 · 공조 24°C · 열선 3단 · 음성 안내", "어제 07:42"),
                    HistoryEntryUi("2", "비 오는 등원", OptionStatus.Partial, false, "내비 목적지는 정차 후 실행", "지난 금 08:12"),
                    HistoryEntryUi("3", "하린이 아침 ↔ 출근 모드", OptionStatus.Conflict, true, "충돌 · 하린이 아침 값으로 맞춤", "지난 목 07:52")
                ),
                weekly = WeeklySummaryUi(12, 2, 1, 1)
            ),
            onBack = {},
            onSelectFilter = {}
        )
    }
}
