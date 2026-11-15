package com.carft.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.VerticalDivider
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftSpacing
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onBack: () -> Unit,
    onSelectFilter: (HistoryFilter) -> Unit
) {
    CarftScreen(topBar = {
        CarftTopBar("실행 기록", onBack = onBack) {
            HistoryFilter.values().forEach { filter ->
                FilterPill(filter.label, selected = filter == state.filter, onClick = { onSelectFilter(filter) })
            }
        }
    }) {
        Row(
            modifier = Modifier.fillMaxSize().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(CarftSpacing.Column)
        ) {
            Timeline(state.entries, Modifier.weight(1f).fillMaxHeight())
            WeeklyCard(state.weekly, Modifier.width(616.dp))
        }
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .background(if (selected) CarftColors.TextPrimary else CarftColors.Surface, CarftShapes.Pill)
            .then(if (selected) Modifier else Modifier.border(1.5.dp, CarftColors.StrokeStrong, CarftShapes.Pill))
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = CarftType.Label, color = if (selected) CarftColors.TextOnLight else CarftColors.TextSecondary)
    }
}

@Composable
private fun Timeline(entries: List<HistoryEntryUi>, modifier: Modifier = Modifier) {
    if (entries.isEmpty()) {
        Box(modifier.padding(top = 24.dp)) {
            Text("아직 실행 기록이 없어요", style = CarftType.BodyM, color = CarftColors.TextTertiary)
        }
        return
    }
    LazyColumn(modifier) {
        items(entries, key = { it.id }) { TimelineEntry(it) }
    }
}

@Composable
private fun TimelineEntry(entry: HistoryEntryUi) {
    val tone = entry.status.tone.color
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(Modifier.width(56.dp).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(56.dp).background(tone.copy(alpha = 0.16f), CarftShapes.Pill),
                contentAlignment = Alignment.Center
            ) {
                CarftIconView(entry.status.icon, size = 28.dp, tint = tone)
            }
            Box(Modifier.width(2.dp).weight(1f).background(CarftColors.Stroke))
        }
        Column(
            modifier = Modifier.weight(1f).padding(top = 6.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        entry.title,
                        style = CarftType.CardTitle,
                        color = CarftColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    StatusPill(entry.status)
                    if (entry.status == OptionStatus.Conflict && entry.resolved) StatusPill(OptionStatus.Resolved)
                }
                Text(entry.timeLabel, style = CarftType.BodyS, color = CarftColors.TextTertiary)
            }
            Text(entry.summary, style = CarftType.BodyM.copy(fontWeight = FontWeight.Normal), color = CarftColors.TextSecondary)
        }
    }
}

@Composable
private fun WeeklyCard(weekly: WeeklySummaryUi, modifier: Modifier = Modifier) {
    CarftCard(modifier = modifier, padding = 28.dp) {
        Text("이번 주", style = CarftType.Label, color = CarftColors.TextSecondary)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            WeeklyMetric("실행", weekly.executed, CarftColors.TextPrimary, "회", Modifier.weight(1f))
            VerticalDivider(height = 70.dp)
            WeeklyMetric("일부만", weekly.partial, CarftColors.Warning, "회", Modifier.weight(1f))
            VerticalDivider(height = 70.dp)
            WeeklyMetric(
                "충돌",
                weekly.conflicts,
                CarftColors.Danger,
                if (weekly.conflicts > 0 && weekly.resolved >= weekly.conflicts) "회 · 해결" else "회",
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeeklyMetric(label: String, value: Int, valueColor: Color, unit: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = CarftType.Caption, color = CarftColors.TextTertiary)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value.toString(), style = CarftType.Display, color = valueColor, modifier = Modifier.alignByBaseline())
            Text(unit, style = CarftType.LabelS, color = CarftColors.TextSecondary, modifier = Modifier.alignByBaseline())
        }
    }
}

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun HistoryScreenPreview() {
    CarftTheme {
        HistoryScreen(
            state = HistoryUiState(
                entries = listOf(
                    HistoryEntryUi("1", "하린이 아침", OptionStatus.Success, false, "정상 실행 · 공조 24°C · 열선 3단 · 음성 안내", "어제 07:42"),
                    HistoryEntryUi("2", "출근 모드", OptionStatus.Success, false, "공조 22°C · 핸들 열선", "어제 08:05"),
                    HistoryEntryUi("3", "비 오는 등원", OptionStatus.Partial, false, "음성 안내 실행 · 내비 목적지는 정차 후 실행", "지난 금 08:12"),
                    HistoryEntryUi("4", "하린이 아침", OptionStatus.Unknown, false, "실내 온도를 알 수 없어 실행하지 않았어요", "지난 금 07:40"),
                    HistoryEntryUi("5", "하린이 아침 ↔ 출근 모드", OptionStatus.Conflict, true, "운전석 온도 24°C / 22°C → 하린이 아침 값으로 맞춤", "지난 목 07:52")
                ),
                weekly = WeeklySummaryUi(12, 2, 1, 1)
            ),
            onBack = {},
            onSelectFilter = {}
        )
    }
}
