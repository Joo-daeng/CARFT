package com.carft.feature.priority

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.carft.app.AppContainer
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType
import com.carft.domain.rule.ActionConflict
import com.carft.domain.rule.ConflictEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ConflictChoice { MatchValue, SwapPriority, SkipOnce }

data class ConflictUiState(val event: ConflictEvent? = null, val resolved: ConflictChoice? = null)

class ConflictViewModel(private val container: AppContainer) : ViewModel() {
    private val _state = MutableStateFlow(ConflictUiState(container.runner.pendingConflict.value))
    val state: StateFlow<ConflictUiState> = _state.asStateFlow()

    fun resolve(choice: ConflictChoice) {
        val event = _state.value.event ?: return
        val options = container.options
        when (choice) {
            ConflictChoice.MatchValue -> options.get(event.lowerId)?.let { lower ->
                val actions = lower.rule.actions.map { if (it.id == event.lowerAction.id) ActionConflict.adopt(it, event.higherAction) else it }
                options.upsert(lower.copy(rule = lower.rule.copy(actions = actions)))
            }
            ConflictChoice.SwapPriority -> {
                val higher = options.get(event.higherId)
                val lower = options.get(event.lowerId)
                if (higher != null && lower != null) options.move(lower.id, higher.priority - lower.priority)
            }
            ConflictChoice.SkipOnce -> Unit
        }
        container.runner.clearConflict()
        _state.value = _state.value.copy(resolved = choice)
    }
}

@Composable
fun ConflictScreen(state: ConflictUiState, onBack: () -> Unit, onResolve: (ConflictChoice) -> Unit, onOpenPriority: () -> Unit, onHome: () -> Unit) {
    val event = state.event
    val resolved = state.resolved
    when {
        event == null -> CarftScreen(topBar = { CarftTopBar("충돌 해결", onBack = onBack) }) {
            Text("지금 해결할 충돌이 없어요", style = CarftType.BodyM, color = CarftColors.TextSecondary, modifier = Modifier.padding(top = 24.dp))
        }
        resolved != null -> ResolvedCard(event, resolved, onOpenPriority, onHome)
        else -> CarftScreen(topBar = { CarftTopBar("충돌 해결", subtitle = "두 옵션이 같은 동작을 다르게 바꾸려 했어요", onBack = onBack) }) {
            Column(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    SideCard(event.higherName, "순위 높음 · 적용 중", event.target, event.higherValue, CarftColors.Success, Modifier.weight(1f))
                    Box(Modifier.size(56.dp).background(CarftColors.Danger.copy(alpha = 0.18f), CarftShapes.Pill), contentAlignment = Alignment.Center) {
                        CarftIconView(CarftIcon.Warning, size = 28.dp, tint = CarftColors.Danger)
                    }
                    SideCard(event.lowerName, "충돌 · 멈춤", event.target, event.lowerValue, CarftColors.Danger, Modifier.weight(1f))
                }
                Text("어떻게 할까요?", style = CarftType.TitleS, color = CarftColors.TextPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    ChoiceCard(
                        "값을 하나로 맞추기", "${event.lowerName}의 값도 ${event.higherValue}에 맞춰요", "두 옵션 모두 실행 · 다음부터 부딪치지 않아요",
                        "이 방법으로 해결", recommended = true, Modifier.weight(1f)
                    ) { onResolve(ConflictChoice.MatchValue) }
                    ChoiceCard(
                        "순위 바꾸기", "${event.lowerName}을(를) 위로 올려 ${event.lowerValue}을(를) 우선해요", "앞으로 같은 상황에서 이 옵션 값이 적용돼요",
                        "선택", recommended = false, Modifier.weight(1f)
                    ) { onResolve(ConflictChoice.SwapPriority) }
                    ChoiceCard(
                        "이번만 넘어가기", "${event.lowerName}의 겹치는 동작만 이번에 건너뛰어요", "다음에 또 부딪치면 다시 물어봐요",
                        "선택", recommended = false, Modifier.weight(1f)
                    ) { onResolve(ConflictChoice.SkipOnce) }
                }
            }
        }
    }
}

@Composable
private fun SideCard(name: String, badge: String, target: String, value: String, tone: Color, modifier: Modifier) {
    Column(
        modifier
            .background(tone.copy(alpha = 0.08f), CarftShapes.Card)
            .border(1.5.dp, tone.copy(alpha = 0.6f), CarftShapes.Card)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(name, style = CarftType.CardTitle, color = CarftColors.TextPrimary)
            StatusPill(badge, if (tone == CarftColors.Success) StatusTone.Success else StatusTone.Danger,
                icon = if (tone == CarftColors.Success) CarftIcon.Check else CarftIcon.Warning)
        }
        Row(
            Modifier.fillMaxWidth().background(tone.copy(alpha = 0.12f), CarftShapes.Small).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CarftIconView(CarftIcon.Fan, size = 24.dp, tint = tone)
            Text(target, style = CarftType.BodyM, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
            Text(value, style = CarftType.TitleM, color = tone)
        }
    }
}

@Composable
private fun ChoiceCard(title: String, line: String, note: String, button: String, recommended: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .background(CarftColors.Surface, CarftShapes.Card)
            .border(if (recommended) 2.dp else 1.5.dp, if (recommended) CarftColors.TextPrimary else CarftColors.Stroke, CarftShapes.Card)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (recommended) StatusPill("추천", StatusTone.Neutral, icon = CarftIcon.Star)
        Text(title, style = CarftType.TitleS, color = CarftColors.TextPrimary)
        Text(line, style = CarftType.BodyS, color = CarftColors.TextSecondary)
        Row(
            Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Small).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CarftIconView(if (recommended) CarftIcon.Check else CarftIcon.Info, size = 20.dp, tint = if (recommended) CarftColors.Success else CarftColors.TextTertiary)
            Text(note, style = CarftType.Caption, color = if (recommended) CarftColors.Success else CarftColors.TextSecondary)
        }
        Spacer(Modifier.size(4.dp))
        CarftButton(button, onClick, Modifier.fillMaxWidth(), style = if (recommended) CarftButtonStyle.Primary else CarftButtonStyle.Secondary)
    }
}

@Composable
private fun ResolvedCard(event: ConflictEvent, choice: ConflictChoice, onOpenPriority: () -> Unit, onHome: () -> Unit) {
    val message = when (choice) {
        ConflictChoice.MatchValue -> "'${event.lowerName}'의 값을 맞췄어요. 이제 두 옵션이 함께 실행돼요."
        ConflictChoice.SwapPriority -> "'${event.lowerName}'을(를) 위로 올렸어요. 앞으로 이 옵션 값이 먼저예요."
        ConflictChoice.SkipOnce -> "이번만 '${event.higherName}' 값을 유지했어요. 다음에 또 물어볼게요."
    }
    val first = if (choice == ConflictChoice.SwapPriority) event.lowerName else event.higherName
    val second = if (choice == ConflictChoice.SwapPriority) event.higherName else event.lowerName
    Box(Modifier.fillMaxSize().background(CarftColors.App), contentAlignment = Alignment.Center) {
        Column(
            Modifier.width(820.dp).background(CarftColors.Surface, CarftShapes.Panel).border(1.5.dp, CarftColors.Stroke, CarftShapes.Panel).padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(Modifier.size(96.dp).background(CarftColors.Success.copy(alpha = 0.18f), CarftShapes.Pill), contentAlignment = Alignment.Center) {
                CarftIconView(CarftIcon.Check, size = 48.dp, tint = CarftColors.Success)
            }
            Text("충돌을 해결했어요", style = CarftType.TitleL, color = CarftColors.TextPrimary)
            Text(message, style = CarftType.BodyM, color = CarftColors.TextSecondary, textAlign = TextAlign.Center)
            Column(Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Card).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row { Text("먼저 실행", style = CarftType.Caption, color = CarftColors.TextTertiary, modifier = Modifier.weight(1f)); Text(first, style = CarftType.Label, color = CarftColors.TextPrimary) }
                Row { Text("함께 조정", style = CarftType.Caption, color = CarftColors.TextTertiary, modifier = Modifier.weight(1f)); Text(second, style = CarftType.Label, color = CarftColors.TextPrimary) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CarftButton("우선순위 보기", onOpenPriority, Modifier.weight(1f), style = CarftButtonStyle.Secondary, icon = CarftIcon.List)
                CarftButton("홈으로", onHome, Modifier.weight(1f), icon = CarftIcon.Arrow)
            }
        }
    }
}
