package com.carft.feature.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftCard
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
fun RecommendScreen(
    state: RecommendUiState,
    onBack: () -> Unit,
    onImport: (templateId: String) -> Unit
) {
    CarftScreen(topBar = { CarftTopBar("추천 옵션", subtitle = "가져온 뒤 내 상황에 맞게 고칠 수 있어요", onBack = onBack) }) {
        Column(Modifier.fillMaxSize().padding(top = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                state.templates.forEach { template ->
                    TemplateCard(template, onImport = { onImport(template.id) }, modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.padding(bottom = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CarftIconView(CarftIcon.Info, size = 22.dp, tint = CarftColors.TextTertiary)
                Text("가져오면 이름 단계에서 시작해요. 검증을 거쳐 저장돼요.", style = CarftType.Caption, color = CarftColors.TextTertiary)
            }
        }
    }
}

@Composable
private fun TemplateCard(template: TemplateUi, onImport: () -> Unit, modifier: Modifier = Modifier) {
    CarftCard(modifier, padding = 32.dp, spacing = 0.dp) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TagChip(template.tag)
            if (template.popular) PopularChip()
        }
        Text(template.title, style = CarftType.CardTitle, color = CarftColors.TextPrimary, modifier = Modifier.padding(top = 22.dp))
        Text(template.description, style = CarftType.BodyS, color = CarftColors.TextSecondary, modifier = Modifier.padding(top = 12.dp))
        RuleBox(template, Modifier.padding(top = 24.dp))
        StatusPill(
            text = if (template.runsWhileDriving) "주행 중에도 실행" else "정차 중에만 실행",
            tone = if (template.runsWhileDriving) StatusTone.Success else StatusTone.Warning,
            icon = if (template.runsWhileDriving) CarftIcon.Car else CarftIcon.Pause,
            modifier = Modifier.padding(top = 24.dp)
        )
        Spacer(Modifier.weight(1f))
        CarftButton(
            "내 옵션으로 가져오기",
            onImport,
            Modifier.fillMaxWidth().padding(top = 32.dp),
            size = CarftButtonSize.Large,
            icon = CarftIcon.Plus
        )
    }
}

@Composable
private fun TagChip(text: String) {
    Box(
        Modifier
            .height(32.dp)
            .background(CarftColors.SurfaceRaised, CarftShapes.Chip)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = CarftType.Caption, color = CarftColors.TextSecondary)
    }
}

@Composable
private fun PopularChip() {
    Row(
        Modifier
            .height(32.dp)
            .border(1.5.dp, CarftColors.Situation, CarftShapes.Pill)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CarftIconView(CarftIcon.Star, size = 18.dp, tint = CarftColors.Situation)
        Text("인기", style = CarftType.Chip, color = CarftColors.Situation)
    }
}

@Composable
private fun RuleBox(template: TemplateUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CarftColors.App, CarftShapes.Small)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        RuleLine("언제", template.rule.situation)
        RuleLine("만약", template.rule.conditions)
        RuleLine("그러면", template.rule.actions)
    }
}

@Composable
private fun RuleLine(label: String, chips: List<BlockChipUi>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = CarftType.Caption, color = CarftColors.TextTertiary, modifier = Modifier.width(76.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (chips.isEmpty()) {
                AlwaysChip()
            } else {
                chips.forEach { BlockChip(it.text, it.kind, icon = it.icon) }
            }
        }
    }
}

@Composable
private fun AlwaysChip() {
    Box(
        Modifier
            .height(40.dp)
            .background(CarftColors.SurfaceRaised, CarftShapes.Chip)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("항상", style = CarftType.Chip, color = CarftColors.TextSecondary)
    }
}

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun RecommendScreenPreview() {
    CarftTheme { RecommendScreen(RecommendViewModel().state.value, {}, {}) }
}
