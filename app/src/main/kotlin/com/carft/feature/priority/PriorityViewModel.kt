package com.carft.feature.priority

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.app.AppContainer
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.Option
import com.carft.ui.model.BlockChipUi
import com.carft.ui.model.toChip
import com.carft.ui.model.toUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PriorityRowUi(val id: String, val rank: Int, val name: String, val chips: List<BlockChipUi>, val enabled: Boolean)

data class PriorityExampleUi(
    val topName: String,
    val topValue: BlockChipUi,
    val lowName: String,
    val lowValue: BlockChipUi,
    val outcome: String
)

data class PriorityUiState(
    val rows: List<PriorityRowUi> = emptyList(),
    val example: PriorityExampleUi? = null,
    val locked: Boolean = false
)

class PriorityViewModel(private val container: AppContainer) : ViewModel() {
    val state: StateFlow<PriorityUiState> = combine(container.options.options, container.vehicle.state) { options, car ->
        PriorityUiState(
            rows = options.map { PriorityRowUi(it.id, it.priority, it.name, it.rule.toUi().all, it.isEnabled) },
            example = example(options),
            locked = car.isDriving
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PriorityUiState())

    fun moveUp(id: String) = move(id, -1)

    fun moveDown(id: String) = move(id, 1)

    private fun move(id: String, offset: Int) {
        if (!state.value.locked) container.options.move(id, offset)
    }

    private fun example(options: List<Option>): PriorityExampleUi? {
        val withHvac = options.mapNotNull { o -> o.rule.actions.filterIsInstance<ActionBlock.SetHvacTemperature>().firstOrNull()?.let { o to it } }
        if (withHvac.size < 2) return null
        val (top, topAction) = withHvac[0]
        val (low, lowAction) = withHvac[1]
        val value = topAction.toChip().text.removePrefix("공조 ")
        return PriorityExampleUi(top.name, topAction.toChip(), low.name, lowAction.toChip(), "운전석은 ${value}로 맞춰져요")
    }
}
