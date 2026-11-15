package com.carft.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

object CarftShapes {
    val Chip = RoundedCornerShape(6.dp)
    val Small = RoundedCornerShape(12.dp)
    val IconButton = RoundedCornerShape(18.dp)
    val Button = RoundedCornerShape(20.dp)
    val Card = RoundedCornerShape(28.dp)
    val Panel = RoundedCornerShape(36.dp)
    val Sheet = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
    val Pill = RoundedCornerShape(999.dp)
}

object CarftSpacing {
    val ScreenH = 48.dp
    val ScreenTop = 16.dp
    val Column = 32.dp
    val Section = 20.dp
    val Item = 14.dp
    val CardPadding = 24.dp
}

private val colorScheme = darkColorScheme(
    primary = CarftColors.TextPrimary,
    onPrimary = CarftColors.TextOnLight,
    secondary = CarftColors.Info,
    background = CarftColors.App,
    onBackground = CarftColors.TextPrimary,
    surface = CarftColors.Surface,
    onSurface = CarftColors.TextPrimary,
    surfaceVariant = CarftColors.SurfaceRaised,
    onSurfaceVariant = CarftColors.TextSecondary,
    outline = CarftColors.Stroke,
    error = CarftColors.Danger
)

private val typography = Typography(
    displayLarge = CarftType.Hero,
    headlineLarge = CarftType.TitleL,
    headlineMedium = CarftType.TitleM,
    titleLarge = CarftType.CardTitle,
    titleMedium = CarftType.TitleS,
    bodyLarge = CarftType.BodyM,
    bodyMedium = CarftType.BodyS,
    labelLarge = CarftType.Label,
    labelMedium = CarftType.LabelS,
    labelSmall = CarftType.Chip
)

@Composable
fun CarftTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colorScheme, typography = typography) {
        CompositionLocalProvider(
            LocalContentColor provides CarftColors.TextPrimary,
            LocalTextStyle provides CarftType.BodyM,
            content = content
        )
    }
}
