package com.carft.feature.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockChipSize
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
            EqualHeightRow(gap = 24.dp) {
                state.templates.forEach { template ->
                    TemplateCard(template, onImport = { onImport(template.id) })
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
private fun EqualHeightRow(gap: Dp, content: @Composable () -> Unit) {
    SubcomposeLayout(Modifier.fillMaxWidth()) { constraints ->
        val gapPx = gap.roundToPx()
        val loose = subcompose("measure", content)
        val width = ((constraints.maxWidth - gapPx * (loose.size - 1)) / loose.size.coerceAtLeast(1)).coerceAtLeast(0)
        val height = loose.maxOfOrNull { it.measure(Constraints(maxWidth = width, minWidth = width)).height } ?: 0
        val measurables = subcompose("place", content)
        val placeables = measurables.map { it.measure(Constraints.fixed(width, height)) }
        layout(constraints.maxWidth, height) {
            placeables.forEachIndexed { i, p -> p.place(i * (width + gapPx), 0) }
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
        Text(template.title, style = CarftType.Title.copy(fontSize = 32.sp), color = CarftColors.TextPrimary, modifier = Modifier.padding(top = 22.dp))
        Text(template.description, style = CarftType.BodyM.copy(fontWeight = FontWeight.Normal), color = CarftColors.TextSecondary, modifier = Modifier.padding(top = 22.dp))
        RuleBox(template, Modifier.padding(top = 22.dp))
        StatusPill(
            text = if (template.runsWhileDriving) "주행 중에도 실행" else "정차 중에만 실행",
            tone = if (template.runsWhileDriving) StatusTone.Success else StatusTone.Warning,
            icon = if (template.runsWhileDriving) CarftIcon.Car else CarftIcon.Pause,
            modifier = Modifier.padding(top = 22.dp)
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
            .height(36.dp)
            .background(CarftColors.SurfaceRaised, CarftShapes.Small)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = CarftType.Chip, color = CarftColors.TextSecondary)
    }
}

@Composable
private fun PopularChip() {
    Row(
        Modifier
            .height(36.dp)
            .background(CarftColors.Situation.copy(alpha = 0.16f), CarftShapes.Pill)
            .border(1.5.dp, CarftColors.Situation.copy(alpha = 0.45f), CarftShapes.Pill)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CarftIconView(CarftIcon.Star, size = 20.dp, tint = CarftColors.Situation)
        Text("인기", style = CarftType.LabelS, color = CarftColors.Situation)
    }
}

@Composable
private fun RuleBox(template: TemplateUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CarftColors.App, CarftShapes.Button)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RuleLine("언제", template.rule.situation)
        RuleLine("만약", template.rule.conditions)
        RuleLine("그러면", template.rule.actions)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RuleLine(label: String, chips: List<BlockChipUi>) {
    Row(Modifier.padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = CarftType.Caption.copy(fontWeight = FontWeight.Bold), color = CarftColors.TextTertiary, modifier = Modifier.width(74.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (chips.isEmpty()) {
                AlwaysChip()
            } else {
                chips.forEach { BlockChip(it.text, it.kind, icon = it.icon, size = BlockChipSize.Regular) }
            }
        }
    }
}

@Composable
private fun AlwaysChip() {
    Box(
        Modifier
            .height(48.dp)
            .background(CarftColors.SurfaceRaised, CarftShapes.Small)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("항상", style = CarftType.LabelS, color = CarftColors.TextSecondary)
    }
}

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun RecommendScreenPreview() {
    CarftTheme { RecommendScreen(RecommendViewModel().state.value, {}, {}) }
}
