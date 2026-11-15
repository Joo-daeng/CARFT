package com.carft.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType

@Composable
fun CarftToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val track by animateColorAsState(if (checked) CarftColors.Success else CarftColors.StrokeStrong, label = "track")
    val knobX by animateDpAsState(if (checked) 54.dp else 6.dp, label = "knob")
    Box(
        modifier = modifier
            .size(width = 104.dp, height = 56.dp)
            .background(track, CarftShapes.Pill)
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (checked) "I" else "O",
            style = CarftType.Label,
            color = CarftColors.TextOnLight,
            modifier = Modifier
                .align(if (checked) Alignment.CenterStart else Alignment.CenterEnd)
                .padding(horizontal = 16.dp)
        )
        Box(
            Modifier
                .offset(x = knobX)
                .size(44.dp)
                .background(Color.White, CarftShapes.Pill)
        )
    }
}
