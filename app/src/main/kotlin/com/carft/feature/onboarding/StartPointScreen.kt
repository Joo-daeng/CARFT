package com.carft.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.icon.CarftLogo
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftTheme
import com.carft.designsystem.theme.CarftType

@Composable
fun StartPointScreen(point: StartPoint) {
    Box(Modifier.fillMaxSize().background(CarftColors.App), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CarftLogo(height = 112.dp)
            Text("CARFT", style = CarftType.Hero, color = CarftColors.TextPrimary, modifier = Modifier.padding(top = 28.dp))
            Text("블록으로 만드는 내 차 옵션", style = CarftType.BodyL, color = CarftColors.TextSecondary, modifier = Modifier.padding(top = 20.dp))
            DemoBadge(point.label, Modifier.padding(top = 24.dp))
        }
    }
}

@Composable
private fun DemoBadge(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(44.dp)
            .drawBehind {
                drawRoundRect(
                    color = CarftColors.Demo,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 5.dp.toPx()))
                    ),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
            }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = CarftType.LabelS, color = CarftColors.Demo)
    }
}

@Preview(widthDp = 1920, heightDp = 904)
@Composable
private fun StartPointScreenPreview() {
    CarftTheme { StartPointScreen(StartPoint.FirstRun) }
}
