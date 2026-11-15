package com.carft.feature.home

import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.component.StatusTone
import com.carft.ui.model.RuleUi

data class OptionCardUi(
    val id: String,
    val name: String,
    val status: OptionStatus,
    val enabled: Boolean,
    val rule: RuleUi
)

data class CarStateUi(
    val interiorTemp: String?,
    val tempCaption: String?,
    val tempTone: StatusTone,
    val rearBelt: String,
    val driverSeat: String,
    val driving: Boolean
)

data class RecentRunUi(
    val title: String,
    val summary: String,
    val timeLabel: String,
    val tone: StatusTone
)

data class UndoUi(val name: String, val secondsLeft: Int)

data class HomeUiState(
    val options: List<OptionCardUi> = emptyList(),
    val enabledCount: Int = 0,
    val watching: Boolean = true,
    val car: CarStateUi = CarStateUi(null, null, StatusTone.Neutral, "모름", "모름", false),
    val recent: RecentRunUi? = null,
    val menuFor: OptionCardUi? = null,
    val confirmDeleteFor: OptionCardUi? = null,
    val undo: UndoUi? = null
)
