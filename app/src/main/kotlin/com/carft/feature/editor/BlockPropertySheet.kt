package com.carft.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftSegmented
import com.carft.designsystem.component.CarftSheet
import com.carft.designsystem.component.CarftToggle
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftType
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.Block
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.TriggerBlock
import com.carft.ui.model.toChip

private val SEATS = listOf("driver" to "운전석", "rearRight" to "뒷좌석")
private val COLORS = listOf("blue" to "파랑", "white" to "흰색", "orange" to "주황", "green" to "초록")

@Composable
fun BlockPropertySheet(block: Block, onChange: (Block) -> Unit, onRemove: () -> Unit, onDone: () -> Unit) {
    CarftSheet(title = block.toChip().text, subtitle = "값을 바꾸면 조립판에 바로 반영돼요", onDismiss = onDone) {
        when (block) {
            is TriggerBlock.SeatBeltBuckled -> SeatPicker(block.seat) { onChange(block.copy(seat = it)) }
            is TriggerBlock.SeatOccupied -> SeatPicker(block.seat) { onChange(block.copy(seat = it)) }
            is TriggerBlock.TemperatureThreshold -> Stepper("기준 온도", "${block.threshold.toInt()}°C",
                { onChange(block.copy(threshold = block.threshold - 1)) }, { onChange(block.copy(threshold = block.threshold + 1)) })
            is TriggerBlock.TimeOfDay -> TimeWheelEditor(block.hour, block.minute) { h, m -> onChange(block.copy(hour = h, minute = m)) }
            is ConditionBlock.IsSeated -> SeatPicker(block.seat) { onChange(block.copy(seat = it)) }
            is ConditionBlock.TemperatureRange -> Stepper("실내 온도 이하", "${block.maxTemp.toInt()}°C",
                { onChange(block.copy(maxTemp = block.maxTemp - 1)) }, { onChange(block.copy(maxTemp = block.maxTemp + 1)) })
            is ConditionBlock.IsDay -> Stepper("시각", "${block.hour}시",
                { onChange(block.copy(hour = (block.hour + 23) % 24)) }, { onChange(block.copy(hour = (block.hour + 1) % 24)) })
            is ActionBlock.SetHvacTemperature -> {
                Stepper("공조 온도", "${formatTemp(block.targetTemp)}°C",
                    { onChange(block.copy(targetTemp = (block.targetTemp - 0.5f).coerceAtLeast(16f))) },
                    { onChange(block.copy(targetTemp = (block.targetTemp + 0.5f).coerceAtMost(30f))) })
                StationaryRow(block.requiresStationary) { onChange(block.copy(requiresStationary = it)) }
            }
            is ActionBlock.SetSeatHeat -> {
                SeatPicker(block.seat) { onChange(block.copy(seat = it)) }
                Stepper("열선 단계", "${block.level}단",
                    { onChange(block.copy(level = (block.level - 1).coerceAtLeast(1))) },
                    { onChange(block.copy(level = (block.level + 1).coerceAtMost(3))) })
                StationaryRow(block.requiresStationary) { onChange(block.copy(requiresStationary = it)) }
            }
            is ActionBlock.SetAmbientLight -> {
                CarftSegmented(COLORS.map { it.second }, COLORS.indexOfFirst { it.first == block.color }.coerceAtLeast(0),
                    { onChange(block.copy(color = COLORS[it].first)) })
                StationaryRow(block.requiresStationary) { onChange(block.copy(requiresStationary = it)) }
            }
            is ActionBlock.PlayTextToSpeech -> {
                OutlinedTextField(
                    value = block.text,
                    onValueChange = { onChange(block.copy(text = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("안내할 문장") },
                    textStyle = CarftType.BodyM,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CarftColors.TextPrimary,
                        unfocusedTextColor = CarftColors.TextPrimary,
                        focusedBorderColor = CarftColors.TextPrimary,
                        unfocusedBorderColor = CarftColors.StrokeStrong
                    )
                )
                StationaryRow(block.requiresStationary) { onChange(block.copy(requiresStationary = it)) }
            }
            else -> Text("바꿀 값이 없는 블록이에요", style = CarftType.BodyM, color = CarftColors.TextSecondary)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarftButton("블록 빼기", onRemove, Modifier.weight(1f), style = CarftButtonStyle.DangerSoft, icon = CarftIcon.Trash)
            CarftButton("확인", onDone, Modifier.weight(1f), icon = CarftIcon.Check)
        }
    }
}

@Composable
private fun SeatPicker(seat: String, onPick: (String) -> Unit) {
    CarftSegmented(SEATS.map { it.second }, SEATS.indexOfFirst { it.first == seat }.coerceAtLeast(0), { onPick(SEATS[it].first) })
}

@Composable
private fun Stepper(label: String, value: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(label, style = CarftType.Label, color = CarftColors.TextSecondary, modifier = Modifier.weight(1f))
        CarftButton("−", onMinus, style = CarftButtonStyle.Secondary, size = CarftButtonSize.Small)
        Text(value, style = CarftType.TitleM, color = CarftColors.TextPrimary, modifier = Modifier.width(140.dp))
        CarftButton("+", onPlus, style = CarftButtonStyle.Secondary, size = CarftButtonSize.Small)
    }
}

@Composable
private fun StationaryRow(checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("정차 중에만 실행", style = CarftType.Label, color = CarftColors.TextPrimary)
            Text("켜면 주행 중에는 미뤘다가 멈춘 뒤 실행해요", style = CarftType.Caption, color = CarftColors.TextSecondary)
        }
        CarftToggle(checked, onChange)
    }
}

private fun formatTemp(t: Float) = if (t % 1f == 0f) t.toInt().toString() else t.toString()
