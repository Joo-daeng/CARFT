package com.carft.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockChipSize
import com.carft.designsystem.component.BlockDot
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftIconButton
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftToggle
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType
import com.carft.ui.model.BlockChipUi
import com.carft.ui.model.RuleUi

@Composable
fun OptionDetailScreen(
    state: OptionDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onSimulate: () -> Unit,
    onOpenMenu: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onPriority: () -> Unit,
    onDelete: () -> Unit
) {
    val card = state.card
    CarftScreen(topBar = {
        CarftTopBar(card?.name ?: "옵션", onBack = onBack) {
            CarftButton("수정", onEdit, style = CarftButtonStyle.Outline, icon = CarftIcon.Edit)
            CarftButton("가상 테스트", onSimulate, style = CarftButtonStyle.Outline, icon = CarftIcon.Flask)
            CarftIconButton(CarftIcon.More, onOpenMenu, contentDescription = "메뉴")
        }
    }) {
        if (card == null) return@CarftScreen
        Column(Modifier.fillMaxSize().padding(top = 16.dp, bottom = 32.dp)) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                RuleCard(card, onToggle, Modifier.weight(1f))
                Column(Modifier.width(560.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    RankCard(state.rank, onPriority)
                    DrivingCard(state.drivingRules)
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CarftButton("삭제", onDelete, style = CarftButtonStyle.DangerSoft, icon = CarftIcon.Trash)
                Box(Modifier.weight(1f))
                CarftButton("이 옵션 고치기", onEdit, icon = CarftIcon.Edit)
            }
        }
    }
}

@Composable
private fun RuleCard(card: OptionCardUi, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    CarftCard(modifier, padding = 32.dp, spacing = 32.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(card.name, style = CarftType.TitleL, color = CarftColors.TextPrimary)
            StatusPill(card.status)
            Box(Modifier.weight(1f))
            CarftToggle(card.enabled, onToggle)
        }
        RuleRow("언제", BlockKind.Situation, card.rule.situation)
        RuleRow("만약", BlockKind.Condition, card.rule.conditions, emptyText = "조건 없이 항상")
        RuleRow("그러면", BlockKind.Action, card.rule.actions)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RuleRow(label: String, kind: BlockKind, chips: List<BlockChipUi>, emptyText: String = "") {
    Row(verticalAlignment = Alignment.Top) {
        Row(Modifier.width(150.dp).padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BlockDot(kind)
            Text(label, style = CarftType.Label, color = CarftColors.TextSecondary)
        }
        if (chips.isEmpty()) {
            Text(emptyText, style = CarftType.BodyM, color = CarftColors.TextTertiary, modifier = Modifier.padding(top = 18.dp))
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chips.forEach { BlockChip(it.text, it.kind, icon = it.icon, size = BlockChipSize.Large) }
            }
        }
    }
}

@Composable
private fun RankCard(rank: Int, onPriority: () -> Unit) {
    CarftCard(Modifier.fillMaxWidth(), spacing = 12.dp) {
        Text("실행 순서", style = CarftType.Label, color = CarftColors.TextSecondary)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$rank", style = CarftType.Hero, color = CarftColors.TextPrimary)
            Text("번째", style = CarftType.TitleS, color = CarftColors.TextSecondary, modifier = Modifier.padding(bottom = 10.dp))
        }
        Text("같은 동작이 부딪치면 순서가 높은 옵션이 이겨요", style = CarftType.Caption, color = CarftColors.TextTertiary)
        CarftButton("우선순위 바꾸기", onPriority, Modifier.fillMaxWidth(), style = CarftButtonStyle.Secondary, icon = CarftIcon.List)
    }
}

@Composable
private fun DrivingCard(rules: List<DrivingRuleUi>) {
    CarftCard(Modifier.fillMaxWidth(), spacing = 14.dp) {
        Text("주행 중 실행", style = CarftType.Label, color = CarftColors.TextSecondary)
        rules.forEach { rule ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CarftIconView(rule.chip.icon, size = 22.dp, tint = CarftColors.TextSecondary)
                Text(rule.chip.text, style = CarftType.BodyM, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
                if (rule.whileDriving) {
                    StatusPill("주행 중 OK", StatusTone.Success, icon = CarftIcon.Car)
                } else {
                    StatusPill("정차 중만", StatusTone.Warning, icon = CarftIcon.Pause)
                }
            }
        }
    }
}

@Preview(widthDp = 1700, heightDp = 1000)
@Composable
private fun OptionDetailPreview() {
    val actions = listOf(
        BlockChipUi("공조 24°C", BlockKind.Action, CarftIcon.Fan),
        BlockChipUi("운전석 열선 3단", BlockKind.Action, CarftIcon.Heat),
        BlockChipUi("음성 안내", BlockKind.Action, CarftIcon.Speaker)
    )
    val rule = RuleUi(
        listOf(BlockChipUi("뒷좌석 벨트를 매면", BlockKind.Situation, CarftIcon.Belt)),
        listOf(BlockChipUi("실내 10°C 이하", BlockKind.Condition, CarftIcon.Thermo)),
        actions
    )
    CarftTheme {
        OptionDetailScreen(
            state = OptionDetailUiState(
                card = OptionCardUi("1", "하린이 아침", OptionStatus.Waiting, true, rule),
                rank = 1,
                drivingRules = actions.map { DrivingRuleUi(it, true) }
            ),
            onBack = {}, onEdit = {}, onSimulate = {}, onOpenMenu = {}, onToggle = {}, onPriority = {}, onDelete = {}
        )
    }
}
