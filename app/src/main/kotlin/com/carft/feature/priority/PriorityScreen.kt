package com.carft.feature.priority

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftIconButton
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType
import com.carft.ui.model.BlockChipUi

@Composable
fun PriorityScreen(state: PriorityUiState, onBack: () -> Unit, onMoveUp: (String) -> Unit, onMoveDown: (String) -> Unit) {
    CarftScreen(topBar = {
        CarftTopBar("우선순위", subtitle = "위에 있는 옵션이 먼저 실행돼요", onBack = onBack) {
            if (state.locked) StatusPill("주행 중 · 바꿀 수 없어요", StatusTone.Warning, icon = CarftIcon.Lock)
        }
    }) {
        Column(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 32.dp)) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Column(
                    Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    state.rows.forEachIndexed { index, row ->
                        RankRow(
                            row = row,
                            canUp = index > 0 && !state.locked,
                            canDown = index < state.rows.lastIndex && !state.locked,
                            onUp = { onMoveUp(row.id) },
                            onDown = { onMoveDown(row.id) }
                        )
                    }
                }
                ExampleCard(state.example, Modifier.width(560.dp))
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CarftIconView(CarftIcon.Info, size = 22.dp, tint = CarftColors.TextTertiary)
                Text("↑ ↓ 버튼으로 바꿔요. 주행 중에는 바꿀 수 없어요.", style = CarftType.BodyS, color = CarftColors.TextTertiary, modifier = Modifier.weight(1f))
                CarftButton("완료", onBack, icon = CarftIcon.Check)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RankRow(row: PriorityRowUi, canUp: Boolean, canDown: Boolean, onUp: () -> Unit, onDown: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(CarftShapes.Card)
            .background(CarftColors.Surface)
            .border(1.5.dp, CarftColors.Stroke, CarftShapes.Card)
            .alpha(if (row.enabled) 1f else 0.55f)
    ) {
    Box(Modifier.matchParentSize()) {
        Box(Modifier.width(120.dp).fillMaxHeight().background(CarftColors.SurfaceRaised))
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(
            Modifier.width(120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("${row.rank}", style = CarftType.Metric, color = CarftColors.TextPrimary)
            if (row.rank == 1) Text("먼저", style = CarftType.Caption, color = CarftColors.Success)
        }
        Column(Modifier.weight(1f).padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(row.name, style = CarftType.CardTitle, color = CarftColors.TextPrimary)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                row.chips.forEach { BlockChip(it.text, it.kind, icon = it.icon) }
            }
        }
        Row(Modifier.padding(end = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ArrowButton(-90f, canUp, onUp, "위로")
            ArrowButton(90f, canDown, onDown, "아래로")
        }
    }
    }
}

@Composable
private fun ArrowButton(rotation: Float, enabled: Boolean, onClick: () -> Unit, label: String) {
    Box(Modifier.size(72.dp).alpha(if (enabled) 1f else 0.3f)) {
        CarftIconButton(CarftIcon.Arrow, { if (enabled) onClick() }, Modifier.rotate(rotation), size = 72.dp, contentDescription = label)
    }
}

@Composable
private fun ExampleCard(example: PriorityExampleUi?, modifier: Modifier) {
    CarftCard(modifier, spacing = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(48.dp).background(CarftColors.Info.copy(alpha = 0.18f), CarftShapes.Small), contentAlignment = Alignment.Center) {
                CarftIconView(CarftIcon.List, size = 26.dp, tint = CarftColors.Info)
            }
            Text("순서는 이렇게 쓰여요", style = CarftType.TitleS, color = CarftColors.TextPrimary)
        }
        Text("두 옵션이 같은 순간에 같은 동작을 다르게 바꾸려 하면, 위에 있는 옵션의 값이 적용돼요.", style = CarftType.BodyS, color = CarftColors.TextSecondary)
        if (example != null) {
            Column(
                Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Card).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ExampleLine(1, example.topName, example.topValue, true)
                ExampleLine(2, example.lowName, example.lowValue, false)
                Text("→ ${example.outcome}", style = CarftType.LabelS, color = CarftColors.Success)
            }
        }
    }
}

@Composable
private fun ExampleLine(rank: Int, name: String, value: BlockChipUi, winner: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(32.dp).background(if (winner) CarftColors.Success else CarftColors.SurfaceRaised, CarftShapes.Pill),
            contentAlignment = Alignment.Center
        ) { Text("$rank", style = CarftType.LabelS, color = if (winner) CarftColors.TextOnLight else CarftColors.TextSecondary) }
        Text(name, style = CarftType.BodyM, color = if (winner) CarftColors.TextPrimary else CarftColors.TextTertiary, modifier = Modifier.weight(1f))
        BlockChip(value.text, if (winner) value.kind else BlockKind.Action, icon = value.icon, modifier = Modifier.alpha(if (winner) 1f else 0.45f))
    }
}

@Preview(widthDp = 1700, heightDp = 1000)
@Composable
private fun PriorityPreview() {
    val chips = listOf(
        BlockChipUi("뒷좌석 벨트를 매면", BlockKind.Situation, CarftIcon.Belt),
        BlockChipUi("공조 24°C", BlockKind.Action, CarftIcon.Fan)
    )
    CarftTheme {
        PriorityScreen(
            state = PriorityUiState(
                rows = listOf(PriorityRowUi("1", 1, "하린이 아침", chips, true), PriorityRowUi("2", 2, "출근 모드", chips, true)),
                example = PriorityExampleUi("하린이 아침", chips[1], "출근 모드", BlockChipUi("공조 22°C", BlockKind.Action, CarftIcon.Fan), "운전석은 24°C로 맞춰져요")
            ),
            onBack = {}, onMoveUp = {}, onMoveDown = {}
        )
    }
}
