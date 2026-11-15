package com.carft.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftType

@Composable
fun ScreenPlaceholder(figmaNode: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit = {}) {
    Column(
        modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusPill("구현 전 · Figma $figmaNode", StatusTone.Warning)
        content()
    }
}

@Composable
fun PlaceholderLine(text: String) {
    Text(text, style = CarftType.BodyM, color = CarftColors.TextSecondary)
}
