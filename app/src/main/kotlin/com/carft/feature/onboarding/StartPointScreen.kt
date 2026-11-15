package com.carft.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.StatusPill
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.icon.CarftLogo
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftType

@Composable
fun StartPointScreen(point: StartPoint) {
    Box(Modifier.fillMaxSize().background(CarftColors.Background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CarftLogo(height = 120.dp)
            Text("CARFT", style = CarftType.Hero, color = CarftColors.TextPrimary)
            Text("블록으로 만드는 내 차 옵션", style = CarftType.BodyM, color = CarftColors.TextSecondary)
            StatusPill(point.label, StatusTone.Neutral)
        }
    }
}
