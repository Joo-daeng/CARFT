package com.carft.designsystem.component

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftIconView
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType

enum class BlockKind(val color: Color, val content: Color, val label: String) {
    Situation(CarftColors.Situation, CarftColors.TextOnLight, "상황"),
    Condition(CarftColors.Condition, CarftColors.TextOnLight, "조건"),
    Logic(CarftColors.Logic, CarftColors.TextOnLight, "논리"),
    Action(CarftColors.Action, CarftColors.TextOnLight, "동작"),
    StopOnlyAction(CarftColors.StopOnly, CarftColors.StopOnlyText, "동작")
}

enum class BlockChipSize(val height: Dp, val horizontal: Dp, val icon: Dp, val text: TextStyle) {
    Small(40.dp, 16.dp, 18.dp, CarftType.Chip),
    Medium(52.dp, 20.dp, 24.dp, CarftType.Label),
    Large(68.dp, 24.dp, 31.dp, CarftType.Button)
}

@Composable
fun BlockChip(
    text: String,
    kind: BlockKind,
    modifier: Modifier = Modifier,
    icon: CarftIcon? = null,
    size: BlockChipSize = BlockChipSize.Small,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .height(size.height)
            .background(kind.color, CarftShapes.Chip)
            .then(if (selected) Modifier.border(3.dp, CarftColors.TextPrimary, CarftShapes.Chip) else Modifier)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = size.horizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) CarftIconView(icon, size = size.icon, tint = kind.content)
        Text(text, style = size.text, color = kind.content)
        if (kind == BlockKind.StopOnlyAction) {
            CarftIconView(CarftIcon.Pause, size = 14.dp, tint = kind.content)
        }
    }
}

@Composable
fun BlockSlot(
    text: String,
    kind: BlockKind,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .height(BlockChipSize.Large.height)
            .border(2.dp, kind.color.copy(alpha = 0.8f), CarftShapes.Chip)
            .background(kind.color.copy(alpha = 0.06f), CarftShapes.Chip)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CarftIconView(CarftIcon.Plus, size = 24.dp, tint = kind.color)
        Text(text, style = CarftType.Label, color = kind.color)
    }
}

@Composable
fun BlockDot(kind: BlockKind, modifier: Modifier = Modifier) {
    Box(modifier.size(10.dp).background(kind.color, CarftShapes.Pill))
}
