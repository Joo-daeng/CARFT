package com.carft.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carft.data.Draft
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.component.CarftCard
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.Block
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.TriggerBlock
import com.carft.ui.model.seatLabel
import com.carft.ui.model.toChip

private val SEAT_GRID = listOf(listOf("driver", "passenger"), listOf("rearLeft", "rearRight"))
private val HEAT_ON = Color(0xFFFF9B38)
private val HEAT_BG = Color(0xFF382C23)

@Composable
fun BlockPanel(block: Block, onChange: (Block) -> Unit, onRemove: () -> Unit, onDone: () -> Unit, modifier: Modifier = Modifier) {
    val chip = block.toChip()
    val stopOnly = chip.kind == BlockKind.StopOnlyAction
    PanelFrame(
        dot = chip.kind.color,
        title = when (block) {
            is TriggerBlock.SeatBeltBuckled -> "어느 자리 벨트를 매면 시작할까요?"
            is TriggerBlock.SeatOccupied -> "어느 자리에 앉으면 시작할까요?"
            is TriggerBlock.TimeOfDay -> "몇 시 몇 분에 시작할까요?"
            is ConditionBlock.TemperatureRange -> "실내 온도가 몇 도일 때?"
            is ActionBlock.SetHvacTemperature -> "공조를 몇 도로 맞출까요?"
            is ActionBlock.SetSeatHeat -> "운전석 열선을 어떻게 설정할까요?"
            is ConditionBlock.Weekday -> "어떤 요일에 실행할까요?"
            is ConditionBlock.TimeSlot -> "어떤 시간대에 실행할까요?"
            else -> if (stopOnly) "정차 중에만 실행할 수 있는 동작입니다." else "따로 설정할 값이 없는 블록이에요."
        },
        description = when (block) {
            is TriggerBlock.SeatBeltBuckled, is TriggerBlock.SeatOccupied -> "감지할 좌석을 하나 선택하세요."
            is TriggerBlock.TimeOfDay -> "매일 같은 시간에 실행해요."
            is ConditionBlock.TemperatureRange -> "기준 온도와 이상/이하를 정해요"
            is ActionBlock.SetHvacTemperature -> "실내를 이 온도로 맞춰요. 0.5°C 단위예요."
            is ActionBlock.SetSeatHeat -> "원하는 열선 단계를 선택하세요."
            is ConditionBlock.Weekday -> "요일 조건을 하나 선택하세요."
            is ConditionBlock.TimeSlot -> "시간대를 하나 선택하세요."
            else -> if (stopOnly) "주행 중에는 실행되지 않아요. 앱의 안전 규칙에 따라 정차한 뒤 실행되거나 이번에는 건너뛸 수 있어요."
            else "이 블록은 고를 값이 없어요. 필요 없으면 아래에서 삭제할 수 있어요."
        },
        modifier = modifier,
        onRemove = onRemove,
        onDone = onDone
    ) {
        when (block) {
            is TriggerBlock.SeatBeltBuckled -> SeatGrid(block.seat) { onChange(block.copy(seat = it)) }
            is TriggerBlock.SeatOccupied -> SeatGrid(block.seat) { onChange(block.copy(seat = it)) }
            is TriggerBlock.TimeOfDay -> TimeWheelEditor(block.hour, block.minute) { h, m -> onChange(block.copy(hour = h, minute = m)) }
            is ConditionBlock.TemperatureRange -> TemperatureEditor(block, onChange)
            is ActionBlock.SetHvacTemperature -> HvacEditor(block.targetTemp) { onChange(block.copy(targetTemp = it)) }
            is ActionBlock.SetSeatHeat -> HeatEditor(block.level) { onChange(block.copy(level = it)) }
            is ConditionBlock.Weekday -> {
                WEEKDAYS.forEach { (key, label, sub) -> OptionRow(label, sub, block.days == key, Modifier.fillMaxWidth()) { onChange(block.copy(days = key)) } }
                Summary("선택한 요일", WEEKDAYS.first { it.first == block.days }.second)
            }
            is ConditionBlock.TimeSlot -> {
                TIME_SLOTS.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { (key, label, sub) -> OptionRow(label, sub, block.slot == key, Modifier.weight(1f)) { onChange(block.copy(slot = key)) } }
                    }
                }
                val picked = TIME_SLOTS.first { it.first == block.slot }
                Summary("선택한 시간대", "${picked.second} · ${picked.third}")
            }
            else -> Unit
        }
    }
}

@Composable
fun LogicPanel(draft: Draft, onSetLogic: (String) -> Unit, onDone: () -> Unit, modifier: Modifier = Modifier) {
    val or = draft.logic.uppercase() == "OR"
    PanelFrame(
        dot = CarftColors.Logic,
        title = "조건을 어떻게 판단할까요?",
        description = "두 조건을 연결하는 방식을 선택하세요.",
        modifier = modifier,
        onRemove = null,
        onDone = onDone
    ) {
        LogicChoice("그리고", "모든 조건을 만족해야 실행", !or) { onSetLogic("AND") }
        LogicChoice("또는", "하나라도 만족하면 실행", or) { onSetLogic("OR") }
        Column(
            Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Small).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            draft.conditions.forEachIndexed { i, c ->
                Text(
                    (if (i > 0) "${if (or) "또는" else "그리고"} " else "") + c.sentence(),
                    style = CarftType.BodyS,
                    color = CarftColors.TextSecondary
                )
            }
            Text(if (or) "→ 하나라도 만족하면 실행합니다." else "→ 모두 만족해야 실행합니다.", style = CarftType.LabelS, color = CarftColors.Logic)
        }
    }
}

@Composable
private fun PanelFrame(
    dot: Color,
    title: String,
    description: String,
    modifier: Modifier,
    onRemove: (() -> Unit)?,
    onDone: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    CarftCard(modifier, spacing = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(12.dp).background(dot, CarftShapes.Chip))
            Text(title, style = CarftType.TitleS, color = CarftColors.TextPrimary)
        }
        Text(description, style = CarftType.BodyS, color = CarftColors.TextSecondary)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            content()
        }
        if (onRemove != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .border(1.5.dp, CarftColors.Danger.copy(alpha = 0.6f), CarftShapes.Small)
                    .background(CarftColors.Danger.copy(alpha = 0.06f), CarftShapes.Small)
                    .clickable(onClick = onRemove),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CarftIconView(CarftIcon.Trash, size = 22.dp, tint = CarftColors.Danger)
                Text("블록 삭제", style = CarftType.Label, color = CarftColors.Danger, modifier = Modifier.padding(start = 10.dp))
            }
        }
        Box(
            Modifier.fillMaxWidth().height(72.dp).background(CarftColors.Success, CarftShapes.Small).clickable(onClick = onDone),
            contentAlignment = Alignment.Center
        ) { Text("확인", style = CarftType.Label, color = CarftColors.TextOnLight) }
    }
}

@Composable
private fun Summary(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Small).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(label, style = CarftType.Caption, color = CarftColors.TextTertiary)
        Text(value, style = CarftType.Label, color = CarftColors.TextPrimary)
    }
}

@Composable
private fun SeatGrid(seat: String, onPick: (String) -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(CarftColors.App, CarftShapes.Small).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("▲ 앞", style = CarftType.Caption, color = CarftColors.TextTertiary)
        SEAT_GRID.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { s ->
                    val selected = s == seat
                    Box(
                        Modifier
                            .weight(1f)
                            .height(128.dp)
                            .background(if (selected) CarftColors.Info.copy(alpha = 0.18f) else CarftColors.SurfaceRaised, CarftShapes.Small)
                            .border(if (selected) 2.dp else 1.dp, if (selected) CarftColors.Info else CarftColors.Stroke, CarftShapes.Small)
                            .clickable { onPick(s) }
                    ) {
                        if (selected) {
                            Box(Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp).background(CarftColors.Info, CarftShapes.Pill), contentAlignment = Alignment.Center) {
                                CarftIconView(CarftIcon.Check, size = 16.dp, tint = Color.White)
                            }
                        }
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CarftIconView(CarftIcon.Seat, size = 30.dp, tint = if (selected) CarftColors.TextPrimary else CarftColors.TextSecondary)
                            Text(seatLabel(s), style = CarftType.Caption, color = if (selected) CarftColors.TextPrimary else CarftColors.TextSecondary)
                        }
                    }
                }
            }
        }
    }
    Summary("선택한 좌석", seatLabel(seat))
}

@Composable
private fun TemperatureEditor(block: ConditionBlock.TemperatureRange, onChange: (Block) -> Unit) {
    val above = block.maxTemp >= 50f
    val value = if (above) block.minTemp else block.maxTemp
    val (feel, feelColor) = when {
        value <= 12f -> "추운 편" to CarftColors.Info
        value <= 22f -> "선선한 편" to CarftColors.Warning
        else -> "따뜻한 편" to CarftColors.Danger
    }
    Summary("선택한 블록", "실내 온도")
    BigValue("${value.toInt()}", feel, feelColor, CarftColors.Info)
    Slider(
        value = value,
        onValueChange = { v ->
            val t = v.toInt().toFloat()
            onChange(if (above) block.copy(minTemp = t) else block.copy(maxTemp = t))
        },
        valueRange = 0f..30f,
        steps = 29,
        colors = sliderColors(CarftColors.Info)
    )
    Ticks(listOf("0°", "5°", "10°", "15°", "20°", "25°", "30°"))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CompareButton("이하 (≤)", "기준보다 춥거나 같을 때", !above, Modifier.weight(1f)) { onChange(block.copy(minTemp = -40f, maxTemp = value)) }
        CompareButton("이상 (≥)", "기준보다 덥거나 같을 때", above, Modifier.weight(1f)) { onChange(block.copy(minTemp = value, maxTemp = 50f)) }
    }
}

@Composable
private fun HvacEditor(temp: Float, onPick: (Float) -> Unit) {
    val feel = when {
        temp <= 20f -> "시원하게"
        temp <= 25f -> "적당하게"
        else -> "따뜻하게"
    }
    BigValue(formatTemp(temp), feel, CarftColors.Success, CarftColors.Success)
    Slider(
        value = temp.coerceIn(17f, 32f),
        onValueChange = { onPick((Math.round(it * 2) / 2f)) },
        valueRange = 17f..32f,
        steps = 29,
        colors = sliderColors(CarftColors.Success)
    )
    Ticks(listOf("17°", "22°", "27°", "32°"))
}

@Composable
private fun HeatEditor(level: Int, onPick: (Int) -> Unit) {
    (1..3).forEach { l ->
        val selected = l == level
        Row(
            Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(if (selected) HEAT_BG else CarftColors.SurfaceRaised, CarftShapes.Small)
                .border(if (selected) 2.dp else 1.dp, if (selected) HEAT_ON else CarftColors.Stroke, CarftShapes.Small)
                .clickable { onPick(l) }
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${l}단", style = CarftType.Label, color = if (selected) HEAT_ON else CarftColors.TextPrimary, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                (1..3).forEach { b ->
                    Box(
                        Modifier.width(8.dp).height((10 + b * 6).dp)
                            .background(if (b <= l) (if (selected) HEAT_ON else CarftColors.TextSecondary) else CarftColors.Stroke, CarftShapes.Chip)
                    )
                }
            }
        }
    }
    Summary("선택한 단계", "${level}단")
}

@Composable
private fun BigValue(value: String, feel: String, feelColor: Color, valueColor: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Text(value, style = CarftType.Display, color = valueColor)
        Text("°C", style = CarftType.TitleS, color = valueColor, modifier = Modifier.padding(bottom = 6.dp))
        Spacer(Modifier.weight(1f))
        Text(feel, style = CarftType.Label, color = feelColor, modifier = Modifier.padding(bottom = 6.dp))
    }
}

@Composable
private fun Ticks(labels: List<String>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        labels.forEach { Text(it, style = CarftType.Caption, color = CarftColors.TextTertiary, textAlign = TextAlign.Center) }
    }
}

@Composable
private fun sliderColors(color: Color) = SliderDefaults.colors(
    thumbColor = Color.White,
    activeTrackColor = color,
    inactiveTrackColor = CarftColors.StrokeStrong,
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent
)

@Composable
private fun CompareButton(title: String, sub: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .background(if (selected) CarftColors.Info.copy(alpha = 0.18f) else CarftColors.SurfaceRaised, CarftShapes.Small)
            .border(if (selected) 2.dp else 1.dp, if (selected) CarftColors.Info else CarftColors.Stroke, CarftShapes.Small)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, style = CarftType.Label, color = CarftColors.TextPrimary)
        Text(sub, style = CarftType.Caption, color = CarftColors.TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun LogicChoice(title: String, sub: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (selected) CarftColors.Logic.copy(alpha = 0.22f) else CarftColors.SurfaceRaised, CarftShapes.Small)
            .border(if (selected) 2.dp else 1.dp, if (selected) CarftColors.Logic else CarftColors.Stroke, CarftShapes.Small)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = CarftType.TitleS, color = CarftColors.TextPrimary)
            Text(sub, style = CarftType.BodyS, color = CarftColors.TextSecondary)
        }
        if (selected) Box(Modifier.size(14.dp).background(CarftColors.Logic, CarftShapes.Pill))
    }
}

@Composable
private fun OptionRow(title: String, sub: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier
            .height(88.dp)
            .background(if (selected) CarftColors.Info.copy(alpha = 0.18f) else CarftColors.SurfaceRaised, CarftShapes.Small)
            .border(if (selected) 2.dp else 1.dp, if (selected) CarftColors.Info else CarftColors.Stroke, CarftShapes.Small)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = CarftType.Label, color = CarftColors.TextPrimary)
            Text(sub, style = CarftType.Caption, color = CarftColors.TextSecondary)
        }
        if (selected) {
            Box(Modifier.size(24.dp).background(CarftColors.Info, CarftShapes.Pill), contentAlignment = Alignment.Center) {
                CarftIconView(CarftIcon.Check, size = 16.dp, tint = Color.White)
            }
        }
    }
}
