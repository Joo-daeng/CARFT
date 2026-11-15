package com.carft.feature.history

import com.carft.designsystem.component.OptionStatus

enum class HistoryFilter(val label: String) { Recent("최근"), ThisWeek("이번 주") }

data class HistoryEntryUi(
    val id: String,
    val title: String,
    val status: OptionStatus,
    val resolved: Boolean,
    val summary: String,
    val timeLabel: String
)

data class WeeklySummaryUi(
    val executed: Int,
    val partial: Int,
    val conflicts: Int,
    val resolved: Int
)

data class HistoryUiState(
    val filter: HistoryFilter = HistoryFilter.Recent,
    val entries: List<HistoryEntryUi> = emptyList(),
    val weekly: WeeklySummaryUi = WeeklySummaryUi(0, 0, 0, 0)
)
