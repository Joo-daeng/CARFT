package com.carft.designsystem.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.carft.designsystem.R

@OptIn(ExperimentalTextApi::class)
private fun astaSans(weight: Int) = Font(
    resId = R.font.asta_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

val AstaSans = FontFamily(
    astaSans(400),
    astaSans(500),
    astaSans(600),
    astaSans(700),
    astaSans(800)
)

private fun style(size: Int, weight: Int, lineHeight: Float = 1.35f) = TextStyle(
    fontFamily = AstaSans,
    fontSize = size.sp,
    fontWeight = FontWeight(weight),
    lineHeight = (size * lineHeight).sp
)

object CarftType {
    val Hero = style(60, 800, 1.25f)
    val Display = style(48, 700, 1.1f)
    val Metric = style(40, 700, 1.2f)
    val Brand = style(36, 700)
    val Title = style(36, 700)
    val TitleL = style(30, 700)
    val TitleM = style(28, 700)
    val CardTitle = style(26, 700)
    val TitleS = style(24, 700)
    val BodyL = style(24, 400, 1.55f)
    val Button = style(26, 600)
    val Label = style(22, 600)
    val BodyM = style(22, 500)
    val LabelS = style(20, 600)
    val BodyS = style(20, 500)
    val Chip = style(18, 600)
    val Caption = style(18, 500)
}
