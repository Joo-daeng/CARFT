package com.carft.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftSpacing
import com.carft.designsystem.theme.CarftType

@Composable
fun CarftCard(
    modifier: Modifier = Modifier,
    padding: Dp = CarftSpacing.CardPadding,
    spacing: Dp = 18.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(CarftColors.Surface, CarftShapes.Card)
            .border(1.5.dp, CarftColors.Stroke, CarftShapes.Card)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}

@Composable
fun CardHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: CarftIcon? = null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (icon != null) CarftIconView(icon, size = 26.dp, tint = CarftColors.TextSecondary)
        Text(title, style = CarftType.Label, color = CarftColors.TextSecondary)
        Spacer(Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    caption: String? = null,
    emphasized: Boolean = true
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = CarftType.Caption, color = CarftColors.TextTertiary)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                style = if (unit != null) CarftType.Display else CarftType.Metric,
                color = if (emphasized) CarftColors.TextPrimary else CarftColors.TextSecondary
            )
            if (unit != null) Text(unit, style = CarftType.TitleS, color = CarftColors.TextSecondary)
        }
        if (caption != null) Text(caption, style = CarftType.LabelS, color = CarftColors.Info)
    }
}

@Composable
fun VerticalDivider(modifier: Modifier = Modifier, height: Dp = 80.dp) {
    Box(modifier.width(1.5.dp).height(height).background(CarftColors.Stroke))
}

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.5.dp).background(CarftColors.Stroke))
}

@Composable
fun WatchBanner(
    on: Boolean,
    modifier: Modifier = Modifier,
    lastChecked: String = "방금"
) {
    val tone = if (on) CarftColors.Success else CarftColors.Neutral
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(tone.copy(alpha = 0.1f), CarftShapes.Button)
            .border(1.5.dp, tone.copy(alpha = 0.35f), CarftShapes.Button)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(Modifier.width(14.dp).height(14.dp).background(tone, CarftShapes.Pill))
        Text(if (on) "감시 켜짐" else "감시 꺼짐", style = CarftType.TitleS, color = tone)
        Text(if (on) "· 차 상태를 보고 있어요" else "· 자동 실행이 멈춰 있어요", style = CarftType.BodyS, color = CarftColors.TextSecondary)
        Spacer(Modifier.weight(1f))
        Text("마지막 확인 $lastChecked", style = CarftType.BodyS, color = CarftColors.TextTertiary)
    }
}
