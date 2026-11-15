package com.carft.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.app.AppContainer
import com.carft.data.DeletedOption
import com.carft.designsystem.component.OptionStatus
import com.carft.designsystem.component.StatusTone
import com.carft.domain.model.ActionExecutionResult
import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.ExecutionResult
import com.carft.domain.model.Option
import com.carft.domain.model.VehicleState
import com.carft.ui.format.timeLabel
import com.carft.ui.model.toUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val container: AppContainer) : ViewModel() {
    private data class Local(
        val menuFor: String? = null,
        val confirmDeleteFor: String? = null,
        val undo: Pair<DeletedOption, Int>? = null
    )

    private val local = MutableStateFlow(Local())
    private var undoJob: Job? = null

    val state: StateFlow<HomeUiState> = combine(
        container.options.options,
        container.vehicle.state,
        container.history.records,
        container.settings.settings,
        local
    ) { options, car, records, settings, ui ->
        val cards = options.map { it.toCard(records) }
        HomeUiState(
            options = cards,
            enabledCount = options.count { it.isEnabled },
            watching = settings.watching,
            car = car.toUi(),
            recent = records.firstOrNull()?.toRecent(),
            menuFor = cards.firstOrNull { it.id == ui.menuFor },
            confirmDeleteFor = cards.firstOrNull { it.id == ui.confirmDeleteFor },
            undo = ui.undo?.let { (deleted, seconds) -> UndoUi(deleted.option.name, seconds) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun setEnabled(id: String, enabled: Boolean) = container.options.setEnabled(id, enabled)

    fun openMenu(id: String) = local.update { it.copy(menuFor = id) }

    fun closeMenu() = local.update { it.copy(menuFor = null) }

    fun askDelete(id: String) = local.update { it.copy(menuFor = null, confirmDeleteFor = id) }

    fun cancelDelete() = local.update { it.copy(confirmDeleteFor = null) }

    fun confirmDelete() {
        val id = local.value.confirmDeleteFor ?: return
        val deleted = container.options.delete(id) ?: return
        local.update { it.copy(confirmDeleteFor = null, undo = deleted to UNDO_SECONDS) }
        undoJob?.cancel()
        undoJob = viewModelScope.launch {
            for (left in UNDO_SECONDS - 1 downTo 0) {
                delay(1_000)
                local.update { it.copy(undo = if (left == 0) null else it.undo?.copy(second = left)) }
            }
        }
    }

    fun undoDelete() {
        val deleted = local.value.undo?.first ?: return
        undoJob?.cancel()
        container.options.restore(deleted)
        local.update { it.copy(undo = null) }
    }

    private fun Option.toCard(records: List<ExecutionRecord>): OptionCardUi {
        val last = records.firstOrNull { it.optionId == id }
        val running = isEnabled && last != null && last.result is ExecutionResult.Success &&
            System.currentTimeMillis() - last.triggeredAt < RUNNING_WINDOW_MS
        val status = when {
            !isEnabled -> OptionStatus.Off
            running -> OptionStatus.Running
            else -> OptionStatus.Waiting
        }
        return OptionCardUi(id, name, status, isEnabled, rule.toUi())
    }

    private fun VehicleState.toUi(): CarStateUi {
        val temp = interiorTemp
        val (caption, tone) = when {
            temp == null -> null to StatusTone.Neutral
            temp <= 12f -> "쌀쌀해요" to StatusTone.Info
            temp >= 27f -> "더워요" to StatusTone.Danger
            else -> "쾌적해요" to StatusTone.Success
        }
        return CarStateUi(
            interiorTemp = temp?.let { if (it % 1f == 0f) it.toInt().toString() else "%.1f".format(it) },
            tempCaption = caption,
            tempTone = tone,
            rearBelt = when (rearRightBeltBuckled) { true -> "맴"; false -> "안 맴"; null -> "모름" },
            driverSeat = when (driverSeatOccupied) { true -> "앉음"; false -> "비어 있음"; null -> "모름" },
            driving = isDriving
        )
    }

    private fun ExecutionRecord.toRecent(): RecentRunUi {
        val (summary, tone) = when (val r = result) {
            is ExecutionResult.Success ->
                ("정상 실행 · " + r.actions.filterIsInstance<ActionExecutionResult.Success>().joinToString(" · ") { it.result }) to StatusTone.Success
            is ExecutionResult.PartialSuccess -> r.reason to StatusTone.Warning
            is ExecutionResult.Deferred -> r.reason to StatusTone.Neutral
            is ExecutionResult.NotExecuted -> r.reason to StatusTone.Danger
        }
        return RecentRunUi(optionName, summary, timeLabel(triggeredAt), tone)
    }

    private companion object {
        const val UNDO_SECONDS = 10
        const val RUNNING_WINDOW_MS = 60_000L
    }
}
