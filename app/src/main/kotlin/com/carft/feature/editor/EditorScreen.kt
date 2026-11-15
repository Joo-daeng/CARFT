package com.carft.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carft.data.Draft
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockChipSize
import com.carft.designsystem.component.BlockDot
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.BlockSlot
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftSegmented
import com.carft.designsystem.component.CarftSheet
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType
import com.carft.domain.model.Block
import com.carft.ui.model.toChip

@Composable
fun EditorScreen(
    state: EditorUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSelectTab: (BoxTab) -> Unit,
    onAdd: (PaletteItem) -> Unit,
    onEditBlock: (Block) -> Unit,
    onSetLogic: (String) -> Unit
) {
    val draft = state.draft
    CarftScreen(topBar = {
        CarftTopBar(if (draft.isNew) "새 옵션 만들기" else "옵션 고치기", onBack = onBack) {
            CarftButton("저장 및 검증", onNext, enabled = draft.canAssemble, icon = CarftIcon.Check)
        }
    }) {
        Row(
            Modifier.fillMaxSize().padding(top = 8.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            BlockBox(state.tab, onSelectTab, onAdd, Modifier.width(340.dp).fillMaxHeight())
            Board(draft, onEditBlock, onSetLogic, Modifier.weight(1f).fillMaxHeight())
            Sentence(draft, Modifier.width(380.dp).fillMaxHeight())
        }
    }
}

@Composable
private fun BlockBox(tab: BoxTab, onSelectTab: (BoxTab) -> Unit, onAdd: (PaletteItem) -> Unit, modifier: Modifier) {
    CarftCard(modifier, spacing = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("블록 상자", style = CarftType.TitleS, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
            Text("누르면 끼워져요", style = CarftType.Caption, color = CarftColors.TextTertiary)
        }
        CarftSegmented(BoxTab.values().map { "● ${it.label}" }, tab.ordinal, { onSelectTab(BoxTab.values()[it]) })
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PALETTE.filter { it.tab == tab }.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(CarftColors.SurfaceRaised, CarftShapes.Small)
                        .clickable { onAdd(item) }
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        Modifier.size(40.dp).background(tab.kind.color.copy(alpha = 0.18f), CarftShapes.Small),
                        contentAlignment = Alignment.Center
                    ) { CarftIconView(item.icon, size = 22.dp, tint = tab.kind.color) }
                    Text(item.label, style = CarftType.Label, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
                    CarftIconView(CarftIcon.Plus, size = 22.dp, tint = CarftColors.TextTertiary)
                }
            }
        }
    }
}

@Composable
private fun Board(draft: Draft, onEditBlock: (Block) -> Unit, onSetLogic: (String) -> Unit, modifier: Modifier) {
    CarftCard(modifier, spacing = 20.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CarftIconView(CarftIcon.Box, size = 24.dp, tint = CarftColors.TextSecondary)
            Text("조립판", style = CarftType.Label, color = CarftColors.TextPrimary)
            Text("| 블록을 이어 붙이고 위로 쌓아 규칙을 만들어요 · 블록을 누르면 값을 바꿔요", style = CarftType.Caption, color = CarftColors.TextTertiary)
        }
        Row(
            Modifier
                .fillMaxSize()
                .background(CarftColors.App, CarftShapes.Card)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Tower("언제", BlockKind.Situation, Modifier.weight(1f)) {
                val trigger = draft.trigger
                if (trigger == null) BlockSlot("상황 블록 놓기", BlockKind.Situation) else PlacedChip(trigger, onEditBlock)
            }
            Tower("만약", BlockKind.Condition, Modifier.weight(1f)) {
                if (draft.conditions.size >= 2) {
                    CarftSegmented(listOf("모두", "하나라도"), if (draft.logic.uppercase() == "OR") 1 else 0, { onSetLogic(if (it == 1) "OR" else "AND") })
                }
                if (draft.conditions.isEmpty()) {
                    Box(
                        Modifier.height(68.dp).fillMaxWidth().background(CarftColors.SurfaceRaised, CarftShapes.Chip),
                        contentAlignment = Alignment.Center
                    ) { Text("조건 없음 · 항상", style = CarftType.Label, color = CarftColors.TextSecondary) }
                }
                draft.conditions.reversed().forEach { PlacedChip(it, onEditBlock) }
            }
            Tower("그러면", BlockKind.Action, Modifier.weight(1f)) {
                BlockSlot("동작 블록 놓기", BlockKind.Action)
                draft.actions.reversed().forEach { PlacedChip(it, onEditBlock) }
            }
        }
    }
}

@Composable
private fun Tower(label: String, kind: BlockKind, modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        content()
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BlockDot(kind)
            Text(label, style = CarftType.LabelS, color = CarftColors.TextSecondary)
        }
    }
}

@Composable
private fun PlacedChip(block: Block, onEditBlock: (Block) -> Unit) {
    val chip = block.toChip()
    BlockChip(chip.text, chip.kind, icon = chip.icon, size = BlockChipSize.Medium, onClick = { onEditBlock(block) })
}

@Composable
private fun Sentence(draft: Draft, modifier: Modifier) {
    CarftCard(modifier, spacing = 22.dp) {
        Column {
            Text("이렇게 동작해요", style = CarftType.TitleS, color = CarftColors.TextPrimary)
            Text("블록을 끼우면 바로 바뀌어요", style = CarftType.Caption, color = CarftColors.TextTertiary)
        }
        SentenceRow("언제", BlockKind.Situation, draft.trigger?.toChip()?.text ?: "아직 끼우지 않았어요", draft.trigger != null)
        SentenceRow(
            "만약",
            BlockKind.Condition,
            if (draft.conditions.isEmpty()) "조건 없이 항상"
            else draft.conditions.joinToString(if (draft.logic.uppercase() == "OR") " 또는 " else " 그리고 ") { it.toChip().text },
            true
        )
        SentenceRow("그러면", BlockKind.Action, if (draft.actions.isEmpty()) "아직 끼우지 않았어요" else draft.actions.joinToString(", ") { it.toChip().text }, draft.actions.isNotEmpty())
        Spacer(Modifier.weight(1f))
        if (!draft.canAssemble) {
            Text("상황 1개와 동작 1개 이상을 끼우면 저장할 수 있어요", style = CarftType.Caption, color = CarftColors.TextTertiary)
        }
    }
}

@Composable
private fun SentenceRow(label: String, kind: BlockKind, text: String, filled: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BlockDot(kind)
            Text(label, style = CarftType.LabelS, color = CarftColors.TextSecondary)
        }
        Text(text, style = CarftType.BodyM, color = if (filled) CarftColors.TextPrimary else CarftColors.TextTertiary)
    }
}

@Composable
fun QuitSheet(onKeep: () -> Unit, onQuit: () -> Unit) {
    CarftSheet(
        title = "만들던 옵션을 버릴까요?",
        subtitle = "지금까지 고른 블록은 저장되지 않아요",
        onDismiss = onKeep,
        leading = CarftIcon.Warning
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarftButton("계속 만들기", onKeep, Modifier.weight(1f), style = CarftButtonStyle.Ghost)
            CarftButton("버리고 나가기", onQuit, Modifier.weight(1f), style = CarftButtonStyle.Danger)
        }
    }
}
