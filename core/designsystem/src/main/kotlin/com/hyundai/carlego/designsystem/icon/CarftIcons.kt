package com.hyundai.carlego.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyundai.carlego.designsystem.R

enum class CarftIcon(@DrawableRes val res: Int) {
    Arrow(R.drawable.ic_carft_arrow),
    Belt(R.drawable.ic_carft_belt),
    Bulb(R.drawable.ic_carft_bulb),
    Calendar(R.drawable.ic_carft_calendar),
    Car(R.drawable.ic_carft_car),
    Check(R.drawable.ic_carft_check),
    Clock(R.drawable.ic_carft_clock),
    Eye(R.drawable.ic_carft_eye),
    Fan(R.drawable.ic_carft_fan),
    Gear(R.drawable.ic_carft_gear),
    Heat(R.drawable.ic_carft_heat),
    History(R.drawable.ic_carft_history),
    Info(R.drawable.ic_carft_info),
    List(R.drawable.ic_carft_list),
    Lock(R.drawable.ic_carft_lock),
    Logic(R.drawable.ic_carft_logic),
    More(R.drawable.ic_carft_more),
    Nav(R.drawable.ic_carft_nav),
    Pause(R.drawable.ic_carft_pause),
    Plus(R.drawable.ic_carft_plus),
    Power(R.drawable.ic_carft_power),
    Seat(R.drawable.ic_carft_seat),
    Sparkle(R.drawable.ic_carft_sparkle),
    Speaker(R.drawable.ic_carft_speaker),
    Steering(R.drawable.ic_carft_steering),
    Thermo(R.drawable.ic_carft_thermo),
    Warning(R.drawable.ic_carft_warning),
    Wiper(R.drawable.ic_carft_wiper)
}

@Composable
fun CarftIconView(
    icon: CarftIcon,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null
) {
    Icon(
        painter = painterResource(icon.res),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint
    )
}

@Composable
fun CarftLogo(modifier: Modifier = Modifier, height: Dp = 48.dp) {
    Image(
        painter = painterResource(R.drawable.carft_logo),
        contentDescription = "CARFT",
        modifier = modifier.size(width = height * LOGO_RATIO, height = height)
    )
}

private const val LOGO_RATIO = 254f / 239f
