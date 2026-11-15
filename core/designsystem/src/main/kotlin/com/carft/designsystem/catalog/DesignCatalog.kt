package com.carft.designsystem.catalog

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.BlockChipSize
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.BlockSlot
import com.carft.designsystem.component.CardHeader
import com.carft.designsystem.component.CarftBrandBar
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftIconButton
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftSegmented
import com.carft.designsystem.component.CarftToast
import com.carft.designsystem.component.CarftToggle
import com.carft.designsystem.component.ConflictAlert
import com.carft.designsystem.component.MetricTile
import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.component.VerticalDivider
import com.carft.designsystem.component.WatchBanner
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignCatalog(modifier: Modifier = Modifier) {
    var toggle by remember { mutableStateOf(true) }
    var segment by remember { mutableIntStateOf(0) }
    CarftScreen(
        modifier = modifier,
        topBar = {
            CarftBrandBar {
                CarftButton("추천 옵션", {}, style = CarftButtonStyle.Outline, icon = CarftIcon.Sparkle)
                CarftButton("설정", {}, style = CarftButtonStyle.Outline, icon = CarftIcon.Gear)
            }
        }
    ) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Section("타이포그래피 · Asta Sans") {
                Text("블록을 끼우면 내 차가 알아서 움직여요", style = CarftType.Hero)
                Text("내 옵션 TitleL 30", style = CarftType.TitleL)
                Text("하린이 아침 CardTitle 26", style = CarftType.CardTitle)
                Text("본문 BodyM 22 · 보조 텍스트", style = CarftType.BodyM, color = CarftColors.TextSecondary)
                Text("캡션 Caption 18", style = CarftType.Caption, color = CarftColors.TextTertiary)
            }
            Section("블록") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BlockChip("뒷좌석 벨트를 매면", BlockKind.Situation, icon = CarftIcon.Belt)
                    BlockChip("실내 10°C 이하", BlockKind.Condition, icon = CarftIcon.Thermo)
                    BlockChip("모두", BlockKind.Logic, icon = CarftIcon.Logic)
                    BlockChip("공조 24°C", BlockKind.Action, icon = CarftIcon.Fan)
                    BlockChip("내비 목적지", BlockKind.StopOnlyAction, icon = CarftIcon.Nav)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    BlockChip("상황", BlockKind.Situation, icon = CarftIcon.Belt, size = BlockChipSize.Medium)
                    BlockChip("열선 3단", BlockKind.Action, icon = CarftIcon.Heat, size = BlockChipSize.Large, selected = true)
                    BlockSlot("상황 블록 놓기", BlockKind.Situation)
                    BlockSlot("동작 블록 놓기", BlockKind.Action)
                }
            }
            Section("상태 · 토글") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OptionStatus.values().forEach { StatusPill(it) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    CarftToggle(toggle, { toggle = it })
                    CarftIconButton(CarftIcon.More, {})
                    CarftSegmented(listOf("평일 아침 08:10", "주말 오후 14:00"), segment, { segment = it }, Modifier.width(520.dp))
                }
            }
            Section("버튼") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CarftButton("확인하고 시작하기", {}, style = CarftButtonStyle.Primary, size = CarftButtonSize.Large, icon = CarftIcon.Arrow)
                    CarftButton("저장하기", {}, style = CarftButtonStyle.Primary, icon = CarftIcon.Check)
                    CarftButton("우선순위 바꾸기", {}, style = CarftButtonStyle.Secondary, icon = CarftIcon.List)
                    CarftButton("실행 기록", {}, style = CarftButtonStyle.Outline, icon = CarftIcon.History)
                    CarftButton("버리고 나가기", {}, style = CarftButtonStyle.Danger)
                    CarftButton("삭제", {}, style = CarftButtonStyle.DangerSoft, size = CarftButtonSize.Small)
                    CarftButton("저장 및 검증", {}, enabled = false)
                }
            }
            Section("카드") {
                WatchBanner(on = true, modifier = Modifier.width(616.dp))
                CarftCard(Modifier.width(616.dp)) {
                    CardHeader("지금 차 상태", icon = CarftIcon.Car)
                    Row {
                        MetricTile("실내 온도", "8", Modifier.weight(1f), unit = "°C", caption = "쌀쌀해요")
                        VerticalDivider()
                        MetricTile("뒷좌석 벨트", "안 맴", Modifier.weight(1f), emphasized = false)
                        VerticalDivider()
                        MetricTile("운전석", "비어 있음", Modifier.weight(1f), emphasized = false)
                    }
                }
            }
            Section("오버레이") {
                CarftToast("'하린이 아침' 실행했어요", "정상 실행 · 공조 24°C · 열선 3단 · 음성 안내", StatusTone.Success)
                CarftToast("옵션을 지웠어요", "10초 안에 되살릴 수 있어요", StatusTone.Neutral, icon = CarftIcon.History,
                    action = { CarftButton("되돌리기", {}, size = CarftButtonSize.Small, icon = CarftIcon.History) })
                ConflictAlert(
                    title = "옵션 두 개가 부딪쳤어요",
                    message = "'하린이 아침' · '출근 모드' 두 옵션의 운전석 온도 값이 서로 달라요.",
                    leftLabel = "지금 적용 · 순위 높음", leftName = "하린이 아침", leftValue = "24°C",
                    rightLabel = "멈춤", rightName = "출근 모드", rightValue = "22°C",
                    onLater = {}, onResolve = {}
                )
            }
            Section("아이콘") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CarftIcon.values().forEach { icon ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CarftIconView(icon, size = 32.dp)
                            Text(icon.name, style = CarftType.Caption, color = CarftColors.TextTertiary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, style = CarftType.TitleS, color = CarftColors.TextSecondary)
        Box { Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { content() } }
    }
}

@Preview(widthDp = 1920, heightDp = 2400)
@Composable
private fun DesignCatalogPreview() {
    CarftTheme { DesignCatalog() }
}
