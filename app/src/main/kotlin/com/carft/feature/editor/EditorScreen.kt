package com.carft.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.carft.data.Draft
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftSheet
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType
import com.carft.domain.model.Block
import com.carft.domain.model.TriggerBlock
import com.carft.ui.model.seatLabel
import com.carft.ui.model.toChip
import kotlinx.coroutines.delay

private val BLOCK_W = 250.dp
private val BLOCK_H = 96.dp

@Composable
fun EditorScreen(
    state: EditorUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSelectTab: (BoxTab) -> Unit,
    onAdd: (PaletteItem) -> Unit,
    onEditBlock: (Block) -> Unit,
    onOpenLogic: () -> Unit,
    onDeselect: () -> Unit,
    onUndoRemove: () -> Unit,
    onDismissRemoved: () -> Unit,
    panel: @Composable (Modifier) -> Unit
) {
    val draft = state.draft
    CarftScreen(topBar = {
        CarftTopBar(if (draft.isNew) "새 옵션 만들기" else "옵션 고치기", onBack = onBack) {
            CarftButton(
                "저장 및 검증",
                onNext,
                enabled = draft.canAssemble,
                style = if (draft.canAssemble) CarftButtonStyle.Primary else CarftButtonStyle.Secondary,
                icon = if (draft.canAssemble) CarftIcon.Check else null
            )
        }
    }) {
        Row(
            Modifier.fillMaxSize().padding(top = 8.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BlockBox(state, onSelectTab, onAdd, Modifier.width(340.dp).fillMaxHeight())
            Board(state, onEditBlock, onOpenLogic, onDeselect, onUndoRemove, onDismissRemoved, Modifier.weight(1f).fillMaxHeight())
            Box(Modifier.width(400.dp).fillMaxHeight()) {
                when (state.panel) {
                    EditorPanel.Sentence -> Sentence(draft, Modifier.fillMaxSize())
                    else -> panel(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun BlockBox(state: EditorUiState, onSelectTab: (BoxTab) -> Unit, onAdd: (PaletteItem) -> Unit, modifier: Modifier) {
    val tab = state.tab
    CarftCard(modifier, spacing = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("블록 상자", style = CarftType.TitleS, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
        }
        Row(
            Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Small).padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BoxTab.values().forEach { t ->
                val selected = t == tab
                Row(
                    Modifier
                        .weight(1f)
                        .height(52.dp)
                        .background(if (selected) CarftColors.SurfaceRaised else Color.Transparent, CarftShapes.Small)
                        .then(if (selected) Modifier.border(1.5.dp, CarftColors.StrokeStrong, CarftShapes.Small) else Modifier)
                        .clickable { onSelectTab(t) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(8.dp).background(t.kind.color, CarftShapes.Pill))
                    Text(t.label, style = CarftType.LabelS, color = if (selected) CarftColors.TextPrimary else CarftColors.TextSecondary, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        val locked = (tab == BoxTab.Condition && state.conditionsFull) || (tab == BoxTab.Action && state.actionsFull)
        if (tab == BoxTab.Condition && state.conditionsFull) {
            Text(
                "조건은 ${MAX_CONDITIONS}개까지 넣을 수 있어요. 하나를 빼면 다시 넣을 수 있어요.",
                style = CarftType.Caption,
                color = CarftColors.Warning,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CarftColors.Warning.copy(alpha = 0.12f), CarftShapes.Small)
                    .border(1.dp, CarftColors.Warning.copy(alpha = 0.4f), CarftShapes.Small)
                    .padding(14.dp)
            )
        }
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PALETTE.filter { it.tab == tab }.forEach { item ->
                val stopOnly = item.icon == CarftIcon.Bulb || item.icon == CarftIcon.Nav
                val tint = if (stopOnly) CarftColors.StopOnly else tab.kind.color
                Row(
                    Modifier
                        .fillMaxWidth()
                        .alpha(if (locked) 0.4f else 1f)
                        .background(CarftColors.SurfaceRaised, CarftShapes.Small)
                        .clickable(enabled = !locked) { onAdd(item) }
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        Modifier.size(44.dp).background(tint.copy(alpha = 0.18f), CarftShapes.Small),
                        contentAlignment = Alignment.Center
                    ) { CarftIconView(item.icon, size = 24.dp, tint = tint) }
                    Text(item.label, style = CarftType.Label, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
                    if (stopOnly) PBadge()
                }
            }
        }
    }
}

@Composable
private fun PBadge() {
    Box(
        Modifier.size(22.dp).border(1.5.dp, CarftColors.StopOnly, CarftShapes.Chip),
        contentAlignment = Alignment.Center
    ) { Text("P", style = CarftType.Caption, color = CarftColors.StopOnly) }
}

@Composable
private fun Board(
    state: EditorUiState,
    onEditBlock: (Block) -> Unit,
    onOpenLogic: () -> Unit,
    onDeselect: () -> Unit,
    onUndoRemove: () -> Unit,
    onDismissRemoved: () -> Unit,
    modifier: Modifier
) {
    val draft = state.draft
    val selectedId = state.editing?.id
    CarftCard(modifier, spacing = 20.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CarftIconView(CarftIcon.Box, size = 24.dp, tint = CarftColors.TextSecondary)
            Text("조립판", style = CarftType.Label, color = CarftColors.TextPrimary)
            Text("| 블록을 연결해 차량 옵션을 자동으로 실행하는 규칙을 만드세요", style = CarftType.Caption, color = CarftColors.TextTertiary)
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(CarftColors.App, CarftShapes.Card)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDeselect),
            contentAlignment = Alignment.Center
        ) {
            Row(Modifier.verticalScroll(rememberScrollState()).padding(vertical = 24.dp), verticalAlignment = Alignment.Top) {
                val trigger = draft.trigger
                Box(Modifier.padding(top = groupHeaderOffset(draft))) {
                    if (trigger == null) Slot("상황 블록 놓기", BlockKind.Situation) else Placed(trigger, trigger.id == selectedId, onEditBlock)
                }
                Joint(groupHeaderOffset(draft))
                ConditionPart(draft, selectedId, state.panel == EditorPanel.Logic, onEditBlock, onOpenLogic)
                Joint(groupHeaderOffset(draft))
                Column(Modifier.padding(top = groupHeaderOffset(draft))) {
                    if (draft.actions.isEmpty()) Slot("동작 블록 놓기", BlockKind.Action)
                    draft.actions.forEachIndexed { i, action ->
                        if (i > 0) StackJoint()
                        Placed(action, action.id == selectedId, onEditBlock)
                    }
                }
            }
            state.removed?.let {
                RemovedToast(onUndoRemove, onDismissRemoved, Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
            }
        }
    }
}

private fun groupHeaderOffset(draft: Draft) = if (draft.conditions.size >= 2) 56.dp else 0.dp

@Composable
private fun ConditionPart(draft: Draft, selectedId: String?, logicSelected: Boolean, onEditBlock: (Block) -> Unit, onOpenLogic: () -> Unit) {
    when {
        draft.conditions.isEmpty() -> Box(
            Modifier.size(BLOCK_W, BLOCK_H).background(CarftColors.SurfaceRaised),
            contentAlignment = Alignment.Center
        ) { Text("조건 없음 · 항상", style = CarftType.Label, color = CarftColors.TextSecondary) }
        draft.conditions.size == 1 -> Placed(draft.conditions.first(), draft.conditions.first().id == selectedId, onEditBlock)
        else -> {
            val or = draft.logic.uppercase() == "OR"
            Column(
                Modifier
                    .background(CarftColors.Logic, CarftShapes.Small)
                    .then(if (logicSelected) Modifier.border(3.dp, CarftColors.TextPrimary, CarftShapes.Small) else Modifier)
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                Row(
                    Modifier.height(56.dp).clickable(onClick = onOpenLogic),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        if (or) "또는" else "그리고",
                        style = CarftType.Label,
                        color = CarftColors.TextPrimary,
                        modifier = Modifier.background(Color(0xFF4B2A85), CarftShapes.Small).padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Text(if (or) "하나라도 만족" else "모두 만족", style = CarftType.LabelS, color = CarftColors.TextPrimary)
                }
                draft.conditions.forEachIndexed { i, c ->
                    if (i > 0) StackJoint()
                    Placed(c, c.id == selectedId, onEditBlock, width = BLOCK_W - 20.dp)
                }
            }
        }
    }
}

@Composable
private fun Placed(block: Block, selected: Boolean, onEditBlock: (Block) -> Unit, width: androidx.compose.ui.unit.Dp = BLOCK_W) {
    val chip = block.toChip()
    val stopOnly = chip.kind == BlockKind.StopOnlyAction
    val (sub, main) = blockLines(block, chip.text)
    Box(
        Modifier
            .size(width, BLOCK_H)
            .background(chip.kind.color, CarftShapes.Small)
            .then(if (selected) Modifier.border(3.dp, CarftColors.TextPrimary, CarftShapes.Small) else Modifier)
            .clickable { onEditBlock(block) }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (stopOnly) {
            Box(Modifier.align(Alignment.TopStart).padding(top = 8.dp)) {
                Box(Modifier.size(20.dp).border(1.5.dp, chip.kind.content, CarftShapes.Chip), contentAlignment = Alignment.Center) {
                    Text("P", style = CarftType.Caption, color = chip.kind.content)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CarftIconView(chip.icon, size = 26.dp, tint = chip.kind.content)
            Column {
                if (sub != null) Text(sub, style = CarftType.Caption, color = chip.kind.content.copy(alpha = 0.75f))
                Text(main, style = CarftType.Label, color = chip.kind.content, maxLines = 1)
            }
        }
    }
}

private fun blockLines(block: Block, text: String): Pair<String?, String> = when (block) {
    is TriggerBlock.SeatBeltBuckled -> seatLabel(block.seat) to "벨트를 매면"
    is TriggerBlock.SeatOccupied -> if (block.seat.startsWith("rear")) seatLabel(block.seat) to "좌석에 앉으면" else null to text
    else -> null to text
}

@Composable
private fun Slot(text: String, kind: BlockKind) {
    val color = kind.color
    Row(
        Modifier
            .size(BLOCK_W, BLOCK_H)
            .drawBehind {
                drawRoundRect(
                    color = color.copy(alpha = 0.8f),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx())))
                )
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CarftIconView(CarftIcon.Plus, size = 24.dp, tint = color)
        Text(text, style = CarftType.Label, color = color, modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
private fun Joint(top: androidx.compose.ui.unit.Dp) {
    Box(Modifier.width(0.dp).zIndex(1f).padding(top = top + BLOCK_H / 2 - 7.dp)) {
        Box(Modifier.offset(x = (-7).dp).requiredSize(14.dp).background(Color(0xFFE6E8EC), CarftShapes.Pill))
    }
}

@Composable
private fun StackJoint() {
    Box(Modifier.width(BLOCK_W - 20.dp).height(0.dp).zIndex(1f), contentAlignment = Alignment.Center) {
        Box(Modifier.requiredSize(12.dp).background(Color(0xFFE6E8EC), CarftShapes.Pill))
    }
}

@Composable
private fun RemovedToast(onUndo: () -> Unit, onDismiss: () -> Unit, modifier: Modifier) {
    LaunchedEffect(Unit) {
        delay(5_000)
        onDismiss()
    }
    Row(
        modifier
            .background(CarftColors.SurfaceRaised, CarftShapes.Button)
            .border(1.5.dp, CarftColors.StrokeStrong, CarftShapes.Button)
            .padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("블록을 삭제했어요.", style = CarftType.BodyM, color = CarftColors.TextPrimary)
        CarftButton("실행 취소", onUndo, style = CarftButtonStyle.Ghost, icon = CarftIcon.Undo)
    }
}

@Composable
private fun Sentence(draft: Draft, modifier: Modifier) {
    CarftCard(modifier, spacing = 22.dp) {
        Column {
            Text("이렇게 동작해요", style = CarftType.TitleS, color = CarftColors.TextPrimary)
            Text("브릭을 끼우면 바로 바뀌어요", style = CarftType.Caption, color = CarftColors.TextTertiary)
        }
        SentenceBlock("언제", BlockKind.Situation) {
            val trigger = draft.trigger
            Text(trigger?.sentence() ?: "아직 끼우지 않았어요", style = CarftType.BodyM, color = if (trigger != null) CarftColors.TextPrimary else CarftColors.TextTertiary)
        }
        SentenceBlock("만약", BlockKind.Condition) {
            if (draft.conditions.isEmpty()) {
                Text("조건 없이 항상", style = CarftType.BodyM, color = CarftColors.TextPrimary)
            } else {
                val joiner = if (draft.logic.uppercase() == "OR") "또는" else "그리고"
                draft.conditions.forEachIndexed { i, c ->
                    Text(
                        buildAnnotatedString {
                            if (i > 0) {
                                withStyle(SpanStyle(color = CarftColors.Logic)) { append(joiner) }
                                append(" ")
                            }
                            append(c.sentence())
                        },
                        style = CarftType.BodyM,
                        color = CarftColors.TextPrimary
                    )
                }
            }
        }
        SentenceBlock("그러면", BlockKind.Action) {
            if (draft.actions.isEmpty()) {
                Text("아직 끼우지 않았어요", style = CarftType.BodyM, color = CarftColors.TextTertiary)
            } else {
                draft.actions.forEach { Text("· ${it.sentence()}", style = CarftType.BodyM, color = CarftColors.TextPrimary) }
            }
        }
        Spacer(Modifier.weight(1f))
        if (draft.actions.any { it.toChip().kind == BlockKind.StopOnlyAction }) {
            Text(
                "'정차 중만' 브릭은 주행 중이면 차가 멈춘 뒤 실행돼요",
                style = CarftType.BodyS,
                color = CarftColors.Warning,
                modifier = Modifier.fillMaxWidth().background(CarftColors.Warning.copy(alpha = 0.12f), CarftShapes.Small).padding(16.dp)
            )
        }
    }
}

@Composable
private fun SentenceBlock(label: String, kind: BlockKind, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(10.dp).background(kind.color, CarftShapes.Chip))
            Text(label, style = CarftType.LabelS, color = CarftColors.TextSecondary)
        }
        content()
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
