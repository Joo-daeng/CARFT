package com.carft.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.icon.CarftLogo
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftSpacing
import com.carft.designsystem.theme.CarftType

@Composable
fun CarftScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Column(modifier.fillMaxSize().background(CarftColors.App)) {
        topBar()
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = CarftSpacing.ScreenH),
            content = content
        )
    }
}

@Composable
fun CarftTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .padding(horizontal = CarftSpacing.ScreenH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (onBack != null) CarftIconButton(CarftIcon.Arrow, onClick = onBack, contentDescription = "뒤로", modifier = Modifier.rotate(180f))
        Text(title, style = CarftType.Title, color = CarftColors.TextPrimary)
        if (subtitle != null) Text(subtitle, style = CarftType.BodyM, color = CarftColors.TextSecondary)
        Spacer(Modifier.weight(1f))
        actions()
    }
}

@Composable
fun CarftBrandBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .padding(horizontal = CarftSpacing.ScreenH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CarftLogo(height = 48.dp)
        Text("CARFT", style = CarftType.Brand, color = CarftColors.TextPrimary)
        Text("내 차 옵션", style = CarftType.BodyM, color = CarftColors.TextSecondary)
        Spacer(Modifier.weight(1f))
        actions()
    }
}
