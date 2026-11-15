package com.carft.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.data.HistoryRepository
import com.carft.designsystem.component.OptionStatus
import com.carft.domain.model.ActionExecutionResult
import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.ExecutionResult
import com.carft.ui.format.timeLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(history: HistoryRepository) : ViewModel() {
    private val filter = MutableStateFlow(HistoryFilter.Recent)

    val state: StateFlow<HistoryUiState> = combine(history.records, filter) { records, filter ->
        val now = System.currentTimeMillis()
        val week = records.filter { now - it.triggeredAt < WEEK_MS }
        HistoryUiState(
            filter = filter,
            entries = (if (filter == HistoryFilter.ThisWeek) week else records).map { it.toUi() },
            weekly = WeeklySummaryUi(
                executed = week.count { it.result is ExecutionResult.Success },
                partial = week.count { it.result is ExecutionResult.PartialSuccess && !it.isConflict() },
                conflicts = week.count { it.isConflict() },
                resolved = week.count { it.isConflict() }
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun selectFilter(value: HistoryFilter) {
        filter.value = value
    }

    private fun ExecutionRecord.isConflict(): Boolean =
        (result as? ExecutionResult.PartialSuccess)?.actions?.any { it is ActionExecutionResult.Conflict } == true

    private fun ExecutionRecord.toUi(): HistoryEntryUi {
        val conflict = isConflict()
        val status = when {
            conflict -> OptionStatus.Conflict
            result is ExecutionResult.Success -> OptionStatus.Success
            result is ExecutionResult.PartialSuccess -> OptionStatus.Partial
            result is ExecutionResult.Deferred -> OptionStatus.Unknown
            else -> OptionStatus.Off
        }
        val summary = when (val r = result) {
            is ExecutionResult.Success -> "정상 실행 · " + r.actions.filterIsInstance<ActionExecutionResult.Success>().joinToString(" · ") { it.result }
            is ExecutionResult.PartialSuccess -> r.reason
            is ExecutionResult.Deferred -> r.reason
            is ExecutionResult.NotExecuted -> r.reason
        }
        return HistoryEntryUi(id, optionName, status, conflict, summary, timeLabel(triggeredAt))
    }

    private companion object {
        const val WEEK_MS = 7L * 24 * 60 * 60 * 1000
    }
}
