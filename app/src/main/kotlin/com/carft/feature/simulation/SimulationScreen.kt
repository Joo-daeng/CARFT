package com.carft.feature.simulation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftSegmented
import com.carft.designsystem.component.CarftToggle
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType
import com.carft.domain.model.TriState

@Composable
fun SimulationScreen(
    state: SimulationUiState,
    onClose: () -> Unit,
    onTemp: (Float) -> Unit,
    onTempUnknown: () -> Unit,
    onHour: (Int) -> Unit,
    onWiper: (Boolean) -> Unit,
    onDriving: (Boolean) -> Unit,
    onDriverSeat: (Boolean) -> Unit,
    onRearBelt: (Boolean) -> Unit
) {
    val car = state.input
    CarftScreen(topBar = { CarftTopBar("가상 테스트", subtitle = "${state.name} · 실제 차는 움직이지 않아요", onBack = onClose) }) {
        Column(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 32.dp)) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                CarftCard(Modifier.width(720.dp).verticalScroll(rememberScrollState()), spacing = 16.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CarftIconView(CarftIcon.Sliders, size = 24.dp, tint = CarftColors.TextSecondary)
                        Text("가상 상황", style = CarftType.Label, color = CarftColors.TextPrimary)
                        Text("· 값을 바꾸면 결과가 바로 바뀌어요", style = CarftType.Caption, color = CarftColors.TextTertiary)
                    }
                    InputBox("실내 온도", CarftIcon.Thermo) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            CarftButton("−", { onTemp(-1f) }, style = CarftButtonStyle.Secondary, size = CarftButtonSize.Medium)
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(car.interiorTemp?.let { "${formatTemp(it)}°C" } ?: "모름", style = CarftType.Display, color = CarftColors.TextPrimary)
                                Text(tempCaption(car.interiorTemp), style = CarftType.Caption, color = CarftColors.Info)
                            }
                            CarftButton("+", { onTemp(1f) }, style = CarftButtonStyle.Secondary, size = CarftButtonSize.Medium)
                        }
                        CarftButton("센서 모름으로 바꾸기", onTempUnknown, style = CarftButtonStyle.Ghost, size = CarftButtonSize.Small, icon = CarftIcon.Info)
                    }
                    InputBox("시간", CarftIcon.Clock) {
                        CarftSegmented(listOf("평일 아침 08:10", "주말 오후 14:00"), if (car.hour < 12) 0 else 1, { onHour(if (it == 0) 8 else 14) })
                    }
                    InputBox("와이퍼", CarftIcon.Wiper) {
                        CarftSegmented(listOf("꺼짐 · 맑음", "켜짐 · 비"), if (car.wiperOn == true) 1 else 0, { onWiper(it == 1) })
                    }
                    InputBox("차 상태", CarftIcon.Car) {
                        CarftSegmented(listOf("정차 · P", "주행 · D"), if (car.isDriving) 1 else 0, { onDriving(it == 1) })
                        if (state.needsSeat) {
                            ToggleRow("운전석에 앉음", car.driverSeatOccupied == true, onDriverSeat)
                            ToggleRow("뒷좌석 벨트 맴", car.rearRightBeltBuckled == true, onRearBelt)
                        }
                    }
                }
                ResultCard(state, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.weight(1f))
                CarftButton("닫기", onClose, icon = CarftIcon.Check)
            }
        }
    }
}

@Composable
private fun InputBox(label: String, icon: CarftIcon, content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Card).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CarftIconView(icon, size = 20.dp, tint = CarftColors.TextTertiary)
            Text(label, style = CarftType.Caption, color = CarftColors.TextTertiary)
        }
        content()
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = CarftType.BodyM, color = CarftColors.TextPrimary, modifier = Modifier.weight(1f))
        CarftToggle(checked, onChange)
    }
}

@Composable
private fun ResultCard(state: SimulationUiState, modifier: Modifier) {
    CarftCard(modifier.verticalScroll(rememberScrollState()), spacing = 20.dp) {
        Text("결과", style = CarftType.Label, color = CarftColors.TextPrimary)
        Step(1, "상황이 일어났다고 가정해요") {
            state.triggerChip?.let { BlockChip(it.text, it.kind, icon = it.icon) }
            StatusPill(if (state.triggerMet) "일어남" else "안 일어남", if (state.triggerMet) StatusTone.Success else StatusTone.Danger,
                icon = if (state.triggerMet) CarftIcon.Check else CarftIcon.Warning)
        }
        Step(2, "조건을 하나씩 확인해요") {
            if (state.conditions.isEmpty()) {
                Text("조건 없음 · 항상", style = CarftType.BodyM, color = CarftColors.TextSecondary)
                StatusPill("통과", StatusTone.Success, icon = CarftIcon.Check)
            }
        }
        state.conditions.forEach { check ->
            Row(Modifier.padding(start = 56.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BlockChip(check.chip.text, check.chip.kind, icon = check.chip.icon)
                Box(Modifier.weight(1f))
                when (check.result) {
                    TriState.TRUE -> StatusPill("통과", StatusTone.Success, icon = CarftIcon.Check)
                    TriState.FALSE -> StatusPill("불통과", StatusTone.Danger, icon = CarftIcon.Warning)
                    TriState.UNKNOWN -> StatusPill("모름", StatusTone.Neutral, icon = CarftIcon.Info)
                }
            }
        }
        Step(3, "그래서 이렇게 돼요") {}
        val (tone, title) = when (state.outcome) {
            SimOutcome.Run -> CarftColors.Success to "실행돼요"
            SimOutcome.Partial -> CarftColors.Warning to "일부만 실행돼요"
            SimOutcome.Unknown -> CarftColors.Neutral to "확인할 수 없어 멈춰요"
            SimOutcome.Skip -> CarftColors.Danger to "실행하지 않아요"
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 56.dp)
                .background(tone.copy(alpha = 0.1f), CarftShapes.Card)
                .border(1.5.dp, tone.copy(alpha = 0.5f), CarftShapes.Card)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CarftIconView(if (state.outcome == SimOutcome.Run) CarftIcon.Check else CarftIcon.Warning, size = 28.dp, tint = tone)
                Text(title, style = CarftType.TitleM, color = tone)
            }
            state.detail.forEach { Text("· $it", style = CarftType.BodyS, color = CarftColors.TextSecondary) }
        }
    }
}

@Composable
private fun Step(number: Int, label: String, trailing: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(Modifier.size(40.dp).background(CarftColors.SurfaceRaised, CarftShapes.Pill), contentAlignment = Alignment.Center) {
            Text("$number", style = CarftType.LabelS, color = CarftColors.TextSecondary)
        }
        Text(label, style = CarftType.BodyM, color = CarftColors.TextSecondary, modifier = Modifier.weight(1f))
        trailing()
    }
}

private fun tempCaption(t: Float?): String = when {
    t == null -> "센서 값을 알 수 없어요"
    t <= 12f -> "쌀쌀해요"
    t >= 27f -> "더워요"
    else -> "쾌적해요"
}

private fun formatTemp(t: Float) = if (t % 1f == 0f) t.toInt().toString() else "%.1f".format(t)
