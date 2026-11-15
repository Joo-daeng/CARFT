package com.carft.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType

enum class StatusTone(val color: Color) {
    Neutral(CarftColors.Neutral),
    Success(CarftColors.Success),
    Warning(CarftColors.Warning),
    Danger(CarftColors.Danger),
    Info(CarftColors.Info)
}

enum class OptionStatus(val label: String, val tone: StatusTone, val icon: CarftIcon) {
    Waiting("대기 중", StatusTone.Neutral, CarftIcon.Eye),
    Running("실행 중", StatusTone.Success, CarftIcon.Check),
    Success("정상 실행", StatusTone.Success, CarftIcon.Check),
    Partial("일부만 실행", StatusTone.Warning, CarftIcon.Pause),
    Unknown("확인 불가", StatusTone.Neutral, CarftIcon.Info),
    Conflict("충돌", StatusTone.Danger, CarftIcon.Warning),
    Resolved("해결됨", StatusTone.Success, CarftIcon.Check),
    Off("꺼짐", StatusTone.Neutral, CarftIcon.Power)
}

@Composable
fun StatusPill(
    text: String,
    tone: StatusTone,
    modifier: Modifier = Modifier,
    icon: CarftIcon? = null
) {
    Row(
        modifier = modifier
            .height(38.dp)
            .background(tone.color.copy(alpha = 0.16f), CarftShapes.Pill)
            .border(1.5.dp, tone.color.copy(alpha = 0.45f), CarftShapes.Pill)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) CarftIconView(icon, size = 20.dp, tint = tone.color)
        Text(text, style = CarftType.LabelS, color = tone.color)
    }
}

@Composable
fun StatusPill(status: OptionStatus, modifier: Modifier = Modifier) {
    StatusPill(status.label, status.tone, modifier, status.icon)
}
