package com.carft.feature.demopanel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType

@Composable
fun DemoPanel(
    state: DemoPanelUiState,
    onClose: () -> Unit,
    onSetDriving: (Boolean) -> Unit,
    onTempUp: () -> Unit,
    onTempDown: () -> Unit,
    onTempUnknown: () -> Unit,
    onSelectTime: (TimePreset) -> Unit,
    onToggleSituation: (DemoSituation) -> Unit,
    onResetSituations: () -> Unit,
    onFirstRun: () -> Unit,
    onReturning: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(CarftColors.Background.copy(alpha = 0.08f))
            // 패널 바깥을 눌러도 닫히지 않는다
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            Modifier
                .width(620.dp)
                .fillMaxHeight()
                .background(CarftColors.Background)
                .drawBehind {
                    val stroke = 3.dp.toPx()
                    drawRect(
                        color = CarftColors.Demo,
                        topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
                        size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
                        style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 6.dp.toPx())))
                    )
                }
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .padding(horizontal = 36.dp, vertical = 28.dp)
        ) {
            Header(onClose)

            Section("주행 상태") {
                DemoSegmented(listOf("정차 · P", "주행 · D"), if (state.driving) 1 else 0) { onSetDriving(it == 1) }
            }
            Section("실내 온도") {
                TempControl(state.interiorTemp, onTempDown, onTempUp, onTempUnknown)
            }
            Section("시간") {
                DemoSegmented(TimePreset.values().map { it.label }, state.time.ordinal) { onSelectTime(TimePreset.values()[it]) }
            }
            Section("상황 만들기 · 누르면 옵션이 반응해요") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DemoSituation.values().forEach { situation ->
                        SituationButton(situation, active = situation in state.active, onClick = { onToggleSituation(situation) })
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FooterButton("상황 초기화", filled = true, onClick = onResetSituations, modifier = Modifier.weight(1f))
                FooterButton("첫 실행", filled = false, onClick = onFirstRun, modifier = Modifier.weight(1f))
                FooterButton("이후 실행", filled = false, onClick = onReturning, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Header(onClose: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("시연 패널", style = CarftType.TitleS, color = CarftColors.Demo)
        Box(
            Modifier
                .height(30.dp)
                .border(1.5.dp, CarftColors.Demo, CarftShapes.Pill)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("앱 기능 아님 · 발표용", style = CarftType.Caption, color = CarftColors.Demo)
        }
        Spacer(Modifier.weight(1f))
        Box(
            Modifier
                .size(56.dp)
                .background(CarftColors.SurfaceRaised, CarftShapes.Pill)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Text("✕", style = CarftType.Label, color = CarftColors.TextSecondary)
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(top = 26.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = CarftType.Caption, color = CarftColors.TextTertiary)
        content()
    }
}

@Composable
private fun DemoSegmented(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(CarftColors.Surface, CarftShapes.Small)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                Modifier
                    .weight(1f)
                    .height(54.dp)
                    .background(if (selected) CarftColors.Demo else CarftColors.Surface, CarftShapes.Small)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(label, style = CarftType.LabelS, color = if (selected) CarftColors.TextOnLight else CarftColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun TempControl(temp: Float?, onDown: () -> Unit, onUp: () -> Unit, onUnknown: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SquareButton("−", onDown)
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
            Text(temp?.let { formatTemp(it) } ?: "모름", style = CarftType.Metric, color = CarftColors.TextPrimary)
            if (temp != null) Text("°C", style = CarftType.Caption, color = CarftColors.TextSecondary, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
        }
        SquareButton("+", onUp)
        Box(
            Modifier
                .size(width = 100.dp, height = 64.dp)
                .border(1.5.dp, CarftColors.StrokeStrong, CarftShapes.Small)
                .clickable(onClick = onUnknown),
            contentAlignment = Alignment.Center
        ) {
            Text("센서 모름", style = CarftType.Caption, color = CarftColors.TextSecondary)
        }
    }
}

private fun formatTemp(value: Float): String =
    if (value % 1f == 0f) value.toInt().toString() else "%.1f".format(value)

@Composable
private fun SquareButton(symbol: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(width = 72.dp, height = 64.dp)
            .background(CarftColors.SurfaceRaised, CarftShapes.Small)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, style = CarftType.TitleM, color = CarftColors.TextPrimary)
    }
}

@Composable
private fun SituationButton(situation: DemoSituation, active: Boolean, onClick: () -> Unit) {
    val content = if (active) CarftColors.TextOnLight else CarftColors.Situation
    Row(
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(if (active) CarftColors.Situation else CarftColors.Situation.copy(alpha = 0.12f), CarftShapes.Small)
            .border(1.5.dp, CarftColors.Situation.copy(alpha = if (active) 1f else 0.6f), CarftShapes.Small)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CarftIconView(situation.icon, size = 26.dp, tint = content)
        Text(situation.label, style = CarftType.LabelS, color = content)
    }
}

@Composable
private fun FooterButton(text: String, filled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(60.dp)
            .background(if (filled) CarftColors.SurfaceRaised else CarftColors.Background, CarftShapes.Small)
            .then(if (filled) Modifier else Modifier.border(1.5.dp, CarftColors.StrokeStrong, CarftShapes.Small))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = CarftType.LabelS, color = CarftColors.TextPrimary)
    }
}

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun DemoPanelPreview() {
    CarftTheme {
        DemoPanel(
            state = DemoPanelUiState(visible = true, interiorTemp = 8f, active = setOf(DemoSituation.RearBelt)),
            onClose = {}, onSetDriving = {}, onTempUp = {}, onTempDown = {}, onTempUnknown = {},
            onSelectTime = {}, onToggleSituation = {}, onResetSituations = {}, onFirstRun = {}, onReturning = {}
        )
    }
}
