package com.carft.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockDot
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.component.VerticalDivider
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType
import com.carft.ui.model.BlockChipUi

@Composable
fun NamingScreen(state: EditorFlowUiState, onBack: () -> Unit, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    CarftScreen(topBar = { CarftTopBar(if (state.draft.isNew) "새 옵션 만들기" else "옵션 고치기", onBack = onBack) }) {
        Column(Modifier.fillMaxSize().padding(bottom = 32.dp)) {
            Column(
                Modifier.weight(1f).width(1000.dp).align(Alignment.CenterHorizontally).padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("이름을 정해요", style = CarftType.TitleM, color = CarftColors.TextPrimary)
                Text("내 차에서 알아볼 수 있는 이름을 정해 주세요.", style = CarftType.BodyS, color = CarftColors.TextSecondary)
                OutlinedTextField(
                    value = state.draft.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("옵션 이름") },
                    placeholder = { Text("예: 출근 모드") },
                    singleLine = true,
                    textStyle = CarftType.BodyL,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CarftColors.TextPrimary,
                        unfocusedTextColor = CarftColors.TextPrimary,
                        focusedBorderColor = CarftColors.TextPrimary,
                        unfocusedBorderColor = CarftColors.StrokeStrong,
                        focusedContainerColor = CarftColors.Surface,
                        unfocusedContainerColor = CarftColors.Surface
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("추천 이름", style = CarftType.Caption, color = CarftColors.TextTertiary)
                    state.suggestions.forEach { name ->
                        Box(
                            Modifier
                                .height(44.dp)
                                .border(1.5.dp, CarftColors.StrokeStrong, CarftShapes.Pill)
                                .clickable { onNameChange(name) }
                                .padding(horizontal = 18.dp),
                            contentAlignment = Alignment.Center
                        ) { Text(name, style = CarftType.LabelS, color = CarftColors.TextPrimary) }
                    }
                }
                Text("만든 규칙 확인하기", style = CarftType.Label, color = CarftColors.TextPrimary, modifier = Modifier.padding(top = 12.dp))
                RuleColumns(state)
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CarftButton("이전", onBack, style = CarftButtonStyle.Secondary)
                Box(Modifier.weight(1f))
                CarftButton("검증하기", onNext, enabled = state.draft.name.isNotBlank(), icon = CarftIcon.Arrow)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RuleColumns(state: EditorFlowUiState) {
    Row(
        Modifier.fillMaxWidth().background(CarftColors.Surface, CarftShapes.Card).border(1.5.dp, CarftColors.Stroke, CarftShapes.Card).padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        listOf(
            Triple("언제", BlockKind.Situation, state.rule.situation),
            Triple("만약", BlockKind.Condition, state.rule.conditions),
            Triple("그러면", BlockKind.Action, state.rule.actions)
        ).forEachIndexed { index, (label, kind, chips) ->
            if (index > 0) VerticalDivider(height = 140.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BlockDot(kind)
                    Text(label, style = CarftType.LabelS, color = CarftColors.TextSecondary)
                }
                if (chips.isEmpty()) {
                    Text(if (kind == BlockKind.Condition) "조건 없이 항상" else "아직 끼우지 않았어요", style = CarftType.BodyS, color = CarftColors.TextTertiary)
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chips.forEach { BlockChip(it.text, it.kind, icon = it.icon) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ValidationScreen(state: EditorFlowUiState, onBack: () -> Unit, onFix: () -> Unit, onSimulate: () -> Unit, onSave: () -> Unit) {
    val result = state.validation
    CarftScreen(topBar = { CarftTopBar("검증 결과", subtitle = "저장하기 전에 문제가 없는지 확인했어요", onBack = onBack) }) {
        Column(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CarftCard(Modifier.fillMaxWidth(), spacing = 12.dp) {
                Text("검증할 옵션 · ${state.draft.name}", style = CarftType.Label, color = CarftColors.TextSecondary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.rule.all.ifEmpty { listOf(BlockChipUi("항상", BlockKind.Condition, CarftIcon.Check)) }.forEach { BlockChip(it.text, it.kind, icon = it.icon) }
                }
            }
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                val tone = when {
                    result.allOk -> CarftColors.Success
                    result.canSave -> CarftColors.Warning
                    else -> CarftColors.Danger
                }
                Column(
                    Modifier
                        .weight(1f)
                        .background(tone.copy(alpha = 0.1f), CarftShapes.Card)
                        .border(1.5.dp, tone.copy(alpha = 0.45f), CarftShapes.Card)
                        .padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.size(56.dp).background(tone.copy(alpha = 0.2f), CarftShapes.Pill), contentAlignment = Alignment.Center) {
                            CarftIconView(if (result.canSave) CarftIcon.Check else CarftIcon.Warning, size = 30.dp, tint = tone)
                        }
                        Column {
                            Text(
                                when {
                                    result.allOk -> "문제없어요. 저장할 수 있어요"
                                    result.canSave -> "저장할 수 있지만 확인할 게 있어요"
                                    else -> "고칠 부분이 있어요"
                                },
                                style = CarftType.TitleM, color = tone
                            )
                            Text("다른 옵션과 부딪히지 않고, 조건끼리 서로 막지 않는지 봤어요", style = CarftType.BodyS, color = CarftColors.TextSecondary)
                        }
                    }
                    result.checks.forEach { check ->
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            val c = when {
                                check.ok -> CarftColors.Success
                                check.blocking -> CarftColors.Danger
                                else -> CarftColors.Warning
                            }
                            CarftIconView(if (check.ok) CarftIcon.Check else CarftIcon.Warning, size = 24.dp, tint = c)
                            Column {
                                Text(check.label, style = CarftType.BodyM, color = CarftColors.TextPrimary)
                                check.detail?.let { Text(it, style = CarftType.Caption, color = c) }
                            }
                        }
                    }
                }
                Column(Modifier.width(560.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    CarftCard(Modifier.fillMaxWidth(), spacing = 12.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CarftIconView(CarftIcon.Sparkle, size = 26.dp, tint = CarftColors.Info)
                            Text("가상 테스트로 미리 확인", style = CarftType.TitleS, color = CarftColors.TextPrimary)
                        }
                        Text("실내 온도 · 좌석 · 와이퍼를 바꿔 보며 이 옵션이 실행되는지 미리 돌려 봐요. 실제 차는 움직이지 않아요.", style = CarftType.BodyS, color = CarftColors.TextSecondary)
                        CarftButton("가상 테스트 하기", onSimulate, Modifier.fillMaxWidth(), style = CarftButtonStyle.Secondary, icon = CarftIcon.Sparkle, enabled = state.draft.canAssemble)
                    }
                    CarftCard(Modifier.fillMaxWidth(), spacing = 14.dp) {
                        Text("주행 중 실행", style = CarftType.Label, color = CarftColors.TextSecondary)
                        state.drivingRules.forEach { rule ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                CarftIconView(rule.chip.icon, size = 22.dp, tint = CarftColors.TextSecondary)
                                Text(rule.chip.text, style = CarftType.BodyM, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
                                if (rule.whileDriving) StatusPill("주행 중 OK", StatusTone.Success, icon = CarftIcon.Car)
                                else StatusPill("정차 중만", StatusTone.Warning, icon = CarftIcon.Pause)
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CarftButton("고치기", onFix, style = CarftButtonStyle.Secondary, icon = CarftIcon.Gear)
                Box(Modifier.weight(1f))
                CarftButton("저장하기", onSave, enabled = result.canSave, icon = CarftIcon.Check)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SavedScreen(name: String, rank: Int, chips: List<BlockChipUi>, onMore: () -> Unit, onHome: () -> Unit) {
    Box(Modifier.fillMaxSize().background(CarftColors.App), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .width(820.dp)
                .background(CarftColors.Surface, CarftShapes.Panel)
                .border(1.5.dp, CarftColors.Stroke, CarftShapes.Panel)
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(Modifier.size(96.dp).background(CarftColors.Success.copy(alpha = 0.18f), CarftShapes.Pill), contentAlignment = Alignment.Center) {
                CarftIconView(CarftIcon.Check, size = 48.dp, tint = CarftColors.Success)
            }
            Text("'$name' 옵션을 저장했어요", style = CarftType.TitleL, color = CarftColors.TextPrimary, textAlign = TextAlign.Center)
            Text("이제 상황이 생기면 차가 알아서 실행해요.", style = CarftType.BodyM, color = CarftColors.TextSecondary)
            Column(
                Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Card).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("실행 순서", style = CarftType.Caption, color = CarftColors.TextTertiary, modifier = Modifier.width(100.dp))
                    Text("$rank", style = CarftType.TitleM, color = CarftColors.TextPrimary)
                    Text("번째로 실행돼요 · 우선순위에서 바꿀 수 있어요", style = CarftType.BodyS, color = CarftColors.TextSecondary)
                }
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("블록", style = CarftType.Caption, color = CarftColors.TextTertiary, modifier = Modifier.width(100.dp).padding(top = 10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chips.forEach { BlockChip(it.text, it.kind, icon = it.icon) }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CarftButton("하나 더 만들기", onMore, Modifier.weight(1f), style = CarftButtonStyle.Secondary, icon = CarftIcon.Plus)
                CarftButton("홈으로", onHome, Modifier.weight(1f), icon = CarftIcon.Arrow)
            }
        }
    }
}
