package com.carft.feature.demopanel

import com.carft.designsystem.icon.CarftIcon

enum class TimePreset(val label: String, val hour: Int) { WeekdayMorning("평일 아침 08:10", 8), WeekendAfternoon("주말 오후 14:00", 14) }

enum class DemoSituation(val label: String, val icon: CarftIcon) {
    RearBelt("뒷좌석 벨트 매기", CarftIcon.Belt),
    DriverSeat("운전석에 앉기", CarftIcon.Seat),
    Wiper("와이퍼 켜기", CarftIcon.Wiper),
    Morning("아침 7:30 되기", CarftIcon.Clock)
}

data class DemoPanelUiState(
    val visible: Boolean = false,
    val driving: Boolean = false,
    val interiorTemp: Float? = null,
    val time: TimePreset = TimePreset.WeekdayMorning,
    val active: Set<DemoSituation> = emptySet()
)
