package com.carft.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.app.AppContainer
import com.carft.designsystem.component.OptionStatus
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.ExecutionResult
import com.carft.ui.model.BlockChipUi
import com.carft.ui.model.toChip
import com.carft.ui.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class DrivingRuleUi(val chip: BlockChipUi, val whileDriving: Boolean)

data class OptionDetailUiState(
    val card: OptionCardUi? = null,
    val rank: Int = 0,
    val drivingRules: List<DrivingRuleUi> = emptyList(),
    val menuOpen: Boolean = false,
    val confirmDelete: Boolean = false
)

class OptionDetailViewModel(private val container: AppContainer, private val optionId: String) : ViewModel() {
    private data class Local(val menuOpen: Boolean = false, val confirmDelete: Boolean = false)

    private val local = MutableStateFlow(Local())

    val state: StateFlow<OptionDetailUiState> = combine(
        container.options.options,
        container.history.records,
        local
    ) { options, records, ui ->
        val option = options.firstOrNull { it.id == optionId } ?: return@combine OptionDetailUiState()
        val last = records.firstOrNull { it.optionId == optionId }
        val running = option.isEnabled && last?.result is ExecutionResult.Success &&
            System.currentTimeMillis() - last.triggeredAt < RUNNING_WINDOW_MS
        val status = when {
            !option.isEnabled -> OptionStatus.Off
            running -> OptionStatus.Running
            else -> OptionStatus.Waiting
        }
        OptionDetailUiState(
            card = OptionCardUi(option.id, option.name, status, option.isEnabled, option.rule.toUi()),
            rank = option.priority,
            drivingRules = option.rule.actions.map { DrivingRuleUi(it.toChip(), !it.stopOnly()) },
            menuOpen = ui.menuOpen,
            confirmDelete = ui.confirmDelete
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OptionDetailUiState())

    fun setEnabled(enabled: Boolean) = container.options.setEnabled(optionId, enabled)

    fun openMenu() = local.update { it.copy(menuOpen = true) }

    fun closeMenu() = local.update { it.copy(menuOpen = false) }

    fun askDelete() = local.update { Local(confirmDelete = true) }

    fun cancelDelete() = local.update { it.copy(confirmDelete = false) }

    fun delete() {
        local.update { Local() }
        container.options.delete(optionId)
    }

    private fun ActionBlock.stopOnly() = when (this) {
        is ActionBlock.SetHvacTemperature -> requiresStationary
        is ActionBlock.SetSeatHeat -> requiresStationary
        is ActionBlock.SetAmbientLight -> requiresStationary
        is ActionBlock.PlayTextToSpeech -> requiresStationary
    }

    private companion object {
        const val RUNNING_WINDOW_MS = 60_000L
    }
}
