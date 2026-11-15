package com.hyundai.carlego.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyundai.carlego.designsystem.icon.CarftIcon
import com.hyundai.carlego.designsystem.icon.CarftIconView
import com.hyundai.carlego.designsystem.theme.CarftColors
import com.hyundai.carlego.designsystem.theme.CarftShapes
import com.hyundai.carlego.designsystem.theme.CarftType

enum class CarftButtonStyle(val container: Color, val content: Color, val border: Color?) {
    Primary(CarftColors.TextPrimary, CarftColors.TextOnLight, null),
    Secondary(CarftColors.SurfaceRaised, CarftColors.TextPrimary, null),
    Outline(Color.Transparent, CarftColors.TextPrimary, CarftColors.StrokeStrong),
    Danger(CarftColors.Danger, CarftColors.TextOnLight, null),
    DangerSoft(CarftColors.Danger.copy(alpha = 0.16f), CarftColors.Danger, null),
    Ghost(Color.Transparent, CarftColors.TextPrimary, null),
    OnDanger(Color.White.copy(alpha = 0.22f), Color.White, null),
    Dark(CarftColors.Background, Color.White, null)
}

enum class CarftButtonSize(val height: Dp, val horizontal: Dp, val icon: Dp, val text: TextStyle) {
    Small(48.dp, 20.dp, 22.dp, CarftType.LabelS),
    Medium(72.dp, 24.dp, 26.dp, CarftType.Label),
    Large(88.dp, 36.dp, 28.dp, CarftType.Button)
}

@Composable
fun CarftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CarftButtonStyle = CarftButtonStyle.Primary,
    size: CarftButtonSize = CarftButtonSize.Medium,
    icon: CarftIcon? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .height(size.height)
            .alpha(if (enabled) 1f else 0.4f)
            .background(style.container, CarftShapes.Button)
            .then(if (style.border != null) Modifier.border(2.dp, style.border, CarftShapes.Button) else Modifier)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = size.horizontal),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) CarftIconView(icon, size = size.icon, tint = style.content)
        Text(text, style = size.text, color = style.content)
    }
}

@Composable
fun CarftIconButton(
    icon: CarftIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .background(CarftColors.SurfaceRaised, CarftShapes.IconButton)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        CarftIconView(icon, size = size * 0.47f, tint = CarftColors.TextPrimary, contentDescription = contentDescription)
    }
}

@Composable
fun CarftSegmented(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(CarftColors.SurfaceRaised, CarftShapes.Small)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(if (selected) CarftColors.TextPrimary else Color.Transparent, CarftShapes.Small)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(label, style = CarftType.LabelS, color = if (selected) CarftColors.TextOnLight else CarftColors.TextSecondary)
            }
        }
    }
}
