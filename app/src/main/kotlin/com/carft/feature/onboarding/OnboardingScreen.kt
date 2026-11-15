package com.carft.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockChipSize
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.HorizontalDivider
import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.icon.CarftLogo
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType
import com.carft.ui.model.BlockChipUi
import com.carft.ui.model.RuleUi

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onStart: () -> Unit,
    onBackStep: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(CarftColors.App)) {
        WelcomeStep(
            state = state,
            onStart = onStart,
            modifier = if (state.step == 0) Modifier else Modifier.blur(12.dp)
        )
        if (state.step == 1) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(CarftColors.Dim)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
            )
            NoticeSheet(
                notices = state.notices,
                onBackStep = onBackStep,
                onConfirm = onConfirm,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// 온보딩 1 (환영)

@Composable
private fun WelcomeStep(state: OnboardingUiState, onStart: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize().padding(horizontal = 72.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        Column(Modifier.weight(1f)) {
            BrandChip()
            Text(
                "블록을 끼우면\n내 차가 알아서 움직여요",
                style = CarftType.Hero,
                color = CarftColors.TextPrimary,
                modifier = Modifier.padding(top = 32.dp)
            )
            Text(
                "아이가 벨트를 매는 순간, 차 안이 쌀쌀하면 히터와 열선을 켜는 것처럼\n원하는 순간과 조건, 동작을 골라 나만의 차량 옵션을 만들어요.",
                style = CarftType.BodyL,
                color = CarftColors.TextSecondary,
                modifier = Modifier.padding(top = 38.dp)
            )
            Column(Modifier.padding(top = 30.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                state.legend.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        BlockChip(item.kind.label, item.kind, Modifier.width(132.dp), icon = item.icon, size = BlockChipSize.Medium)
                        Text(item.description, style = CarftType.BodyS, color = CarftColors.TextPrimary)
                    }
                }
            }
            Row(
                modifier = Modifier.padding(top = 34.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CarftButton("시작하기", onStart, Modifier.width(280.dp), size = CarftButtonSize.Large, icon = CarftIcon.Arrow)
                PageDots(current = 0)
            }
        }
        ExampleCard(state, Modifier.width(800.dp))
    }
}

@Composable
private fun BrandChip() {
    Row(
        modifier = Modifier
            .height(48.dp)
            .background(CarftColors.Surface, CarftShapes.Pill)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CarftLogo(height = 24.dp)
        Text("CARFT · Craft Your Car", style = CarftType.LabelS, color = CarftColors.TextPrimary)
    }
}

@Composable
private fun PageDots(current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(2) { index ->
            Box(
                Modifier
                    .size(width = if (index == current) 40.dp else 8.dp, height = 8.dp)
                    .background(if (index == current) CarftColors.TextPrimary else CarftColors.TextTertiary, CarftShapes.Pill)
            )
        }
    }
}

@Composable
private fun ExampleCard(state: OnboardingUiState, modifier: Modifier = Modifier) {
    CarftCard(modifier, padding = 44.dp, spacing = 0.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(state.exampleName, style = CarftType.CardTitle, color = CarftColors.TextPrimary)
            StatusPill(OptionStatus.Running)
        }
        Column(Modifier.padding(top = 34.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
            ExampleLine("언제", state.exampleRule.situation)
            ExampleLine("만약", state.exampleRule.conditions)
            ExampleLine("그러면", state.exampleRule.actions)
        }
        HorizontalDivider(Modifier.padding(top = 26.dp))
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CarftIconView(CarftIcon.Speaker, size = 24.dp, tint = CarftColors.Action)
            Text(state.exampleVoice, style = CarftType.BodyM, color = CarftColors.TextSecondary)
        }
    }
}

@Composable
private fun ExampleLine(label: String, chips: List<BlockChipUi>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = CarftType.Caption, color = CarftColors.TextTertiary, modifier = Modifier.width(106.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            chips.forEach { BlockChip(it.text, it.kind, icon = it.icon, size = BlockChipSize.Large) }
        }
    }
}

// 온보딩 2 (알고 시작해요)

@Composable
private fun NoticeSheet(
    notices: List<NoticeUi>,
    onBackStep: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(1200.dp)
            .background(CarftColors.SurfaceRaised, CarftShapes.Sheet)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
            .padding(start = 56.dp, end = 56.dp, top = 18.dp, bottom = 48.dp)
    ) {
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 72.dp, height = 8.dp)
                .background(CarftColors.StrokeStrong, CarftShapes.Pill)
        )
        Row(
            modifier = Modifier.padding(top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                Modifier.size(72.dp).background(CarftColors.Info.copy(alpha = 0.16f), CarftShapes.IconButton),
                contentAlignment = Alignment.Center
            ) {
                CarftIconView(CarftIcon.Lock, size = 30.dp, tint = CarftColors.Info)
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("시작하기 전에 알아 두세요", style = CarftType.TitleL, color = CarftColors.TextPrimary)
                Text("CARFT가 차에서 하는 일은 이것뿐이에요. 언제든 설정에서 끌 수 있어요.", style = CarftType.BodyS, color = CarftColors.TextSecondary)
            }
        }
        Column(
            Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
                .background(CarftColors.Surface, CarftShapes.Small)
        ) {
            notices.forEachIndexed { index, notice ->
                if (index > 0) HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    CarftIconView(notice.icon, size = 26.dp, tint = CarftColors.TextSecondary)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(notice.title, style = CarftType.Label, color = CarftColors.TextPrimary)
                        Text(notice.description, style = CarftType.Caption, color = CarftColors.TextSecondary)
                    }
                }
            }
        }
        Row(Modifier.padding(top = 28.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarftButton("이전으로", onBackStep, Modifier.weight(1f), style = CarftButtonStyle.Ghost, size = CarftButtonSize.Large)
            CarftButton("확인하고 시작하기", onConfirm, Modifier.weight(1f), size = CarftButtonSize.Large)
        }
    }
}

private val PreviewState = OnboardingUiState(
    legend = listOf(
        LegendUi(BlockKind.Situation, CarftIcon.Belt, "언제 시작할까요"),
        LegendUi(BlockKind.Condition, CarftIcon.Thermo, "어떤 때만 할까요"),
        LegendUi(BlockKind.Logic, CarftIcon.Logic, "조건을 모두 / 하나라도"),
        LegendUi(BlockKind.Action, CarftIcon.Fan, "무엇을 할까요")
    ),
    exampleName = "하린이 아침",
    exampleRule = RuleUi(
        situation = listOf(BlockChipUi("뒷좌석 벨트를 매면", BlockKind.Situation, CarftIcon.Belt)),
        conditions = listOf(BlockChipUi("실내 10°C 이하이면", BlockKind.Condition, CarftIcon.Thermo)),
        actions = listOf(
            BlockChipUi("공조 24°C", BlockKind.Action, CarftIcon.Fan),
            BlockChipUi("열선 3단", BlockKind.Action, CarftIcon.Heat)
        )
    ),
    exampleVoice = "“하린이가 탔어요. 히터를 켰어요.”",
    notices = listOf(
        NoticeUi(CarftIcon.Eye, "차 상태를 읽어요", "벨트 · 좌석 · 실내 온도 · 와이퍼를 확인해 상황이 생겼는지 봐요"),
        NoticeUi(CarftIcon.Fan, "공조 · 열선 · 조명을 바꿔요", "내가 만든 옵션에 넣은 동작만 실행해요"),
        NoticeUi(CarftIcon.Car, "주행 중엔 안전한 동작만", "실내등 · 내비 목적지처럼 정차 중에만 되는 동작은 멈춘 뒤 실행해요"),
        NoticeUi(CarftIcon.History, "시동을 다시 걸어도 이어서", "앱을 닫아도 옵션은 계속 살아 있어요")
    )
)

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun OnboardingWelcomePreview() {
    CarftTheme { OnboardingScreen(PreviewState, {}, {}, {}) }
}

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun OnboardingNoticePreview() {
    CarftTheme { OnboardingScreen(PreviewState.copy(step = 1), {}, {}, {}) }
}
