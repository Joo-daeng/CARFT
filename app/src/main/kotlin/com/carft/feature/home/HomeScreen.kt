package com.carft.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockChipSize
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.CardHeader
import com.carft.designsystem.component.CarftBrandBar
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftIconButton
import com.carft.designsystem.component.CarftToggle
import com.carft.designsystem.component.MetricTile
import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.component.VerticalDivider
import com.carft.designsystem.component.WatchBanner
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.icon.CarftLogo
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftSpacing
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType
import com.carft.ui.model.BlockChipUi
import com.carft.ui.model.RuleUi

@Composable
fun HomeScreen(
    state: HomeUiState,
    onOpenRecommend: () -> Unit,
    onOpenPriority: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDemoPanel: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onToggle: (String, Boolean) -> Unit,
    onOpenMenu: (String) -> Unit,
    onCreate: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(CarftColors.App)) {
        CarftBrandBar {
            CarftButton("추천 옵션", onOpenRecommend, style = CarftButtonStyle.Outline, icon = CarftIcon.Sparkle)
            CarftButton("우선순위", onOpenPriority, style = CarftButtonStyle.Outline, icon = CarftIcon.List)
            CarftButton("실행 기록", onOpenHistory, style = CarftButtonStyle.Outline, icon = CarftIcon.History)
            CarftButton("설정", onOpenSettings, style = CarftButtonStyle.Outline, icon = CarftIcon.Gear)
            DemoButton(onOpenDemoPanel)
        }
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = CarftSpacing.ScreenH, end = CarftSpacing.ScreenH, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(CarftSpacing.Column)
        ) {
            Column(
                Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()).padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (state.options.isEmpty()) EmptyOptions(onCreate, onOpenRecommend) else ListTitle(state.enabledCount, state.options.size)
                state.options.forEach { card ->
                    OptionCard(card, onClick = { onOpenDetail(card.id) }, onToggle = { onToggle(card.id, it) }, onMore = { onOpenMenu(card.id) })
                }
            }
            Column(
                Modifier.width(560.dp).fillMaxHeight().verticalScroll(rememberScrollState()).padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                WatchBanner(on = state.watching)
                CarStateCard(state.car)
                NewOptionButton(onCreate)
                RecentCard(state.recent, onOpenHistory)
            }
        }
    }
}

@Composable
private fun DemoButton(onClick: () -> Unit) {
    Row(
        Modifier
            .height(48.dp)
            .border(2.dp, CarftColors.Demo, CarftShapes.Small)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CarftIconView(CarftIcon.Car, size = 22.dp, tint = CarftColors.Demo)
        Text("시연 패널", style = CarftType.LabelS, color = CarftColors.Demo)
    }
}

@Composable
private fun ListTitle(enabled: Int, total: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("내 옵션", style = CarftType.TitleL, color = CarftColors.TextPrimary)
        Text("$enabled", style = CarftType.Button, color = CarftColors.TextSecondary)
        Text("/ $total", style = CarftType.Button, color = CarftColors.TextTertiary)
        Spacer(Modifier.weight(1f))
        CarftIconView(CarftIcon.Info, size = 22.dp, tint = CarftColors.TextTertiary)
        Text("위에 있을수록 먼저 실행돼요", style = CarftType.BodyS, color = CarftColors.TextTertiary)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptionCard(card: OptionCardUi, onClick: () -> Unit, onToggle: (Boolean) -> Unit, onMore: () -> Unit) {
    val stripe = if (card.enabled) CarftColors.Situation else CarftColors.StrokeStrong
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CarftShapes.Card)
            .background(CarftColors.Surface)
            .drawBehind { drawRect(stripe, size = Size(8.dp.toPx(), size.height)) }
            .border(1.5.dp, CarftColors.Stroke, CarftShapes.Card)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.weight(1f).padding(start = 28.dp, top = 18.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(card.name, style = CarftType.CardTitle, color = if (card.enabled) CarftColors.TextPrimary else CarftColors.TextTertiary)
                StatusPill(card.status)
            }
            FlowRow(
                Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                card.rule.all.forEach { chip -> BlockChip(chip.text, chip.kind, icon = chip.icon) }
            }
        }
        Row(
            Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CarftToggle(card.enabled, onToggle)
            CarftIconButton(CarftIcon.More, onMore, contentDescription = "${card.name} 메뉴")
        }
    }
}

@Composable
private fun EmptyOptions(onCreate: () -> Unit, onOpenRecommend: () -> Unit) {
    val dash = CarftColors.StrokeStrong
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 543.dp)
            .drawBehind {
                val stroke = 2.dp.toPx()
                drawRoundRect(
                    color = dash,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius(28.dp.toPx()),
                    style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 10.dp.toPx())))
                )
            }
            .padding(horizontal = 40.dp, vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Box(Modifier.size(width = 520.dp, height = 170.dp)) {
            BlockChip("벨트를 매면", BlockKind.Situation, Modifier.offset(20.dp, 7.dp).rotate(4f), icon = CarftIcon.Belt, size = BlockChipSize.Large)
            BlockChip("10°C 이하", BlockKind.Condition, Modifier.offset(187.dp, 70.dp).rotate(-3f), icon = CarftIcon.Thermo, size = BlockChipSize.Large)
            BlockChip("열선 3단", BlockKind.Action, Modifier.offset(330.dp, 10.dp).rotate(5f), icon = CarftIcon.Heat, size = BlockChipSize.Large)
        }
        Text("아직 만든 옵션이 없어요", style = CarftType.Title.copy(fontSize = 32.sp), color = CarftColors.TextPrimary)
        Text(
            "상황 · 조건 · 동작 블록을 끼우면 나만의 차량 옵션이 돼요.\n처음이라면 추천 옵션에서 골라 시작해도 좋아요.",
            style = CarftType.BodyM.copy(fontWeight = FontWeight.Normal),
            color = CarftColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarftButton("새 옵션 만들기", onCreate, size = CarftButtonSize.Large, icon = CarftIcon.Plus)
            CarftButton("추천 옵션 둘러보기", onOpenRecommend, style = CarftButtonStyle.Secondary, size = CarftButtonSize.Large, icon = CarftIcon.Sparkle)
        }
    }
}

@Composable
private fun CarStateCard(car: CarStateUi) {
    CarftCard(Modifier.fillMaxWidth()) {
        CardHeader("지금 차 상태", icon = CarftIcon.Car) {
            StatusPill(if (car.driving) "주행 중" else "정차 중", if (car.driving) StatusTone.Warning else StatusTone.Neutral, icon = if (car.driving) CarftIcon.Car else CarftIcon.Pause)
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
            MetricTile(
                "실내 온도",
                car.interiorTemp ?: "모름",
                unit = car.interiorTemp?.let { "°C" },
                caption = car.tempCaption
            )
            VerticalDivider()
            MetricTile("뒷좌석 벨트", car.rearBelt, emphasized = car.rearBelt == "맴")
            VerticalDivider()
            MetricTile("운전석", car.driverSeat, emphasized = car.driverSeat == "앉음")
        }
        if (car.applied.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("적용 중", style = CarftType.Caption, color = CarftColors.TextTertiary)
                car.applied.forEach { label ->
                    Row(
                        Modifier.height(36.dp).background(CarftColors.SurfaceRaised, CarftShapes.Small).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CarftIconView(CarftIcon.Fan, size = 17.dp, tint = CarftColors.TextSecondary)
                        Text(label, style = CarftType.Chip, color = CarftColors.TextSecondary)
                    }
                }
            }
        }
        if (car.applied.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("적용 중", style = CarftType.Caption, color = CarftColors.TextTertiary)
                car.applied.forEach { label ->
                    Row(
                        Modifier.height(36.dp).background(CarftColors.SurfaceRaised, CarftShapes.Small).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CarftIconView(CarftIcon.Fan, size = 17.dp, tint = CarftColors.TextSecondary)
                        Text(label, style = CarftType.Chip, color = CarftColors.TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewOptionButton(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CarftShapes.Card)
            .background(CarftColors.TextPrimary)
            .clickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CarftLogo(height = 48.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("새 옵션 만들기", style = CarftType.TitleM, color = CarftColors.TextOnLight)
            Text("블록을 끼워 나만의 옵션을 만들어요", style = CarftType.BodyS, color = CarftColors.TextOnLightSub)
        }
        CarftIconView(CarftIcon.Arrow, size = 32.dp, tint = CarftColors.TextOnLight)
    }
}

@Composable
private fun RecentCard(recent: RecentRunUi?, onOpenHistory: () -> Unit) {
    CarftCard(Modifier.fillMaxWidth(), spacing = 14.dp) {
        CardHeader("최근 실행", icon = CarftIcon.History) {
            Text("전체 보기", style = CarftType.LabelS, color = CarftColors.Info, modifier = Modifier.clickable(onClick = onOpenHistory))
        }
        if (recent == null) {
            Text("아직 실행된 옵션이 없어요. 시연 패널에서 상황을 만들어 보세요.", style = CarftType.BodyS, color = CarftColors.TextTertiary)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier.size(48.dp).background(recent.tone.color.copy(alpha = 0.16f), CarftShapes.Pill),
                    contentAlignment = Alignment.Center
                ) {
                    CarftIconView(
                        when (recent.tone) {
                            StatusTone.Success -> CarftIcon.Check
                            StatusTone.Warning -> CarftIcon.Pause
                            StatusTone.Danger -> CarftIcon.Warning
                            else -> CarftIcon.Info
                        },
                        tint = recent.tone.color
                    )
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(recent.title, style = CarftType.TitleS, color = CarftColors.TextPrimary)
                    Text(recent.summary, style = CarftType.BodyS, color = CarftColors.TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Text(recent.timeLabel, style = CarftType.BodyS, color = CarftColors.TextTertiary)
            }
        }
    }
}

@Preview(widthDp = 1700, heightDp = 1000)
@Composable
private fun HomeScreenPreview() {
    val rule = RuleUi(
        situation = listOf(BlockChipUi("뒷좌석 벨트를 매면", BlockKind.Situation, CarftIcon.Belt)),
        conditions = listOf(BlockChipUi("실내 10°C 이하", BlockKind.Condition, CarftIcon.Thermo)),
        actions = listOf(
            BlockChipUi("공조 24°C", BlockKind.Action, CarftIcon.Fan),
            BlockChipUi("운전석 열선 3단", BlockKind.Action, CarftIcon.Heat),
            BlockChipUi("음성 안내", BlockKind.Action, CarftIcon.Speaker)
        )
    )
    CarftTheme {
        HomeScreen(
            state = HomeUiState(
                options = listOf(
                    OptionCardUi("1", "하린이 아침", OptionStatus.Waiting, true, rule),
                    OptionCardUi("2", "출근 모드", OptionStatus.Off, false, rule)
                ),
                enabledCount = 1,
                car = CarStateUi("8", "쌀쌀해요", StatusTone.Info, "안 맴", "비어 있음", false),
                recent = RecentRunUi("하린이 아침", "정상 실행 · 공조 24°C · 열선 3단 · 음성 안내", "어제 07:42", StatusTone.Success)
            ),
            onOpenRecommend = {}, onOpenPriority = {}, onOpenHistory = {}, onOpenSettings = {}, onOpenDemoPanel = {},
            onOpenDetail = {}, onToggle = { _, _ -> }, onOpenMenu = {}, onCreate = {}
        )
    }
}
