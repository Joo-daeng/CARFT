package com.hyundai.carlego.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hyundai.carlego.designsystem.icon.CarftIcon
import com.hyundai.carlego.designsystem.icon.CarftIconView
import com.hyundai.carlego.designsystem.theme.CarftColors
import com.hyundai.carlego.designsystem.theme.CarftShapes
import com.hyundai.carlego.designsystem.theme.CarftType

@Composable
fun DimLayer(onDismiss: () -> Unit, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(CarftColors.Dim)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
        content = content
    )
}

@Composable
fun CarftSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: CarftIcon? = null,
    leadingTone: StatusTone = StatusTone.Warning,
    content: @Composable ColumnScope.() -> Unit
) {
    DimLayer(onDismiss) {
        Column(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .width(1000.dp)
                .background(CarftColors.SurfaceRaised, CarftShapes.Sheet)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .padding(start = 48.dp, end = 48.dp, top = 18.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 64.dp, height = 6.dp)
                    .background(CarftColors.StrokeStrong, CarftShapes.Pill)
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (leading != null) {
                    Box(
                        Modifier.size(56.dp).background(leadingTone.color.copy(alpha = 0.16f), CarftShapes.Pill),
                        contentAlignment = Alignment.Center
                    ) { CarftIconView(leading, size = 28.dp, tint = leadingTone.color) }
                }
                Column(Modifier.weight(1f)) {
                    Text(title, style = CarftType.TitleM, color = CarftColors.TextPrimary)
                    if (subtitle != null) Text(subtitle, style = CarftType.BodyS, color = CarftColors.TextSecondary)
                }
                Box(
                    Modifier.size(56.dp).background(CarftColors.SurfaceToast, CarftShapes.Pill).clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) { Text("✕", style = CarftType.Label, color = CarftColors.TextSecondary) }
            }
            content()
        }
    }
}

@Composable
fun SheetMenuItem(
    title: String,
    description: String,
    icon: CarftIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    danger: Boolean = false
) {
    val tint = if (danger) CarftColors.Danger else CarftColors.TextPrimary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (danger) CarftColors.Danger.copy(alpha = 0.12f) else CarftColors.SurfaceToast, CarftShapes.Button)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        CarftIconView(icon, size = 26.dp, tint = tint)
        Column(Modifier.weight(1f)) {
            Text(title, style = CarftType.Label, color = tint)
            Text(description, style = CarftType.Caption, color = CarftColors.TextSecondary)
        }
        Text("›", style = CarftType.TitleS, color = CarftColors.TextTertiary)
    }
}

@Composable
fun CarftToast(
    title: String,
    message: String,
    tone: StatusTone,
    modifier: Modifier = Modifier,
    icon: CarftIcon = CarftIcon.Check,
    action: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .widthIn(min = 640.dp, max = 1100.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = CarftShapes.Card,
        color = CarftColors.SurfaceToast,
        border = BorderStroke(1.5.dp, if (tone == StatusTone.Neutral) CarftColors.StrokeStrong else tone.color),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 24.dp, end = 18.dp, top = 18.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                Modifier.size(44.dp).background(tone.color.copy(alpha = 0.2f), CarftShapes.Pill),
                contentAlignment = Alignment.Center
            ) { CarftIconView(icon, size = 24.dp, tint = tone.color) }
            Column(Modifier.weight(1f, fill = false)) {
                Text(title, style = CarftType.Label, color = if (tone == StatusTone.Neutral) CarftColors.TextPrimary else tone.color)
                Spacer(Modifier.height(2.dp))
                Text(message, style = CarftType.Caption, color = CarftColors.TextSecondary)
            }
            if (action != null) action()
        }
    }
}

@Composable
fun ConflictAlert(
    title: String,
    message: String,
    leftLabel: String,
    leftName: String,
    leftValue: String,
    rightLabel: String,
    rightName: String,
    rightValue: String,
    onLater: () -> Unit,
    onResolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(880.dp)
            .background(CarftColors.Danger, CarftShapes.Panel)
            .padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(48.dp).background(Color.White.copy(alpha = 0.25f), CarftShapes.Pill), contentAlignment = Alignment.Center) {
                CarftIconView(CarftIcon.Warning, size = 26.dp, tint = Color.White)
            }
            Text(title, style = CarftType.TitleL, color = Color.White)
        }
        Text(message, style = CarftType.BodyM, color = Color.White)
        Row(
            Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.18f), CarftShapes.Card).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            listOf(Triple(leftLabel, leftName, leftValue), Triple(rightLabel, rightName, rightValue)).forEach { (label, name, value) ->
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(label, style = CarftType.Caption, color = Color.White.copy(alpha = 0.8f))
                    Text(name, style = CarftType.Label, color = Color.White)
                    Text(value, style = CarftType.Display, color = Color.White)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarftButton("나중에", onLater, Modifier.weight(1f), style = CarftButtonStyle.OnDanger)
            CarftButton("해결하기", onResolve, Modifier.weight(1f), style = CarftButtonStyle.Dark, icon = CarftIcon.Arrow)
        }
    }
}
