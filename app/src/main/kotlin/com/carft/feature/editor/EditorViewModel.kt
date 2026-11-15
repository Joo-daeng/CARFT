package com.carft.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.app.AppContainer
import com.carft.data.Draft
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.icon.CarftIcon
import com.carft.domain.model.ActionBlock
import com.carft.domain.model.Block
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.TriggerBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class BoxTab(val label: String, val kind: BlockKind) {
    Situation("상황", BlockKind.Situation),
    Condition("조건", BlockKind.Condition),
    Action("동작", BlockKind.Action)
}

data class PaletteItem(val tab: BoxTab, val label: String, val icon: CarftIcon, val create: () -> Block)

val PALETTE = listOf(
    PaletteItem(BoxTab.Situation, "벨트를 매면", CarftIcon.Belt) { TriggerBlock.SeatBeltBuckled(seat = "rearRight") },
    PaletteItem(BoxTab.Situation, "좌석에 앉으면", CarftIcon.Seat) { TriggerBlock.SeatOccupied(seat = "driver") },
    PaletteItem(BoxTab.Situation, "와이퍼가 켜지면", CarftIcon.Wiper) { TriggerBlock.WiperOn() },
    PaletteItem(BoxTab.Situation, "실내 온도가 떨어지면", CarftIcon.Thermo) { TriggerBlock.TemperatureThreshold(threshold = 10f) },
    PaletteItem(BoxTab.Condition, "실내 온도가 ○°C 이하", CarftIcon.Thermo) { ConditionBlock.TemperatureRange(minTemp = -40f, maxTemp = 10f) },
    PaletteItem(BoxTab.Condition, "좌석에 사람이 있으면", CarftIcon.Seat) { ConditionBlock.IsSeated(seat = "rearRight") },
    PaletteItem(BoxTab.Condition, "아침 시간이면", CarftIcon.Clock) { ConditionBlock.IsDay(hour = 7) },
    PaletteItem(BoxTab.Action, "공조 온도", CarftIcon.Fan) { ActionBlock.SetHvacTemperature(targetTemp = 24f) },
    PaletteItem(BoxTab.Action, "좌석 열선", CarftIcon.Heat) { ActionBlock.SetSeatHeat(seat = "driver", level = 3) },
    PaletteItem(BoxTab.Action, "앰비언트 조명", CarftIcon.Bulb) { ActionBlock.SetAmbientLight(color = "blue") },
    PaletteItem(BoxTab.Action, "음성 안내", CarftIcon.Speaker) { ActionBlock.PlayTextToSpeech(text = "옵션을 실행했어요.") }
)

data class EditorUiState(
    val draft: Draft = Draft(),
    val tab: BoxTab = BoxTab.Situation,
    val editing: Block? = null,
    val confirmQuit: Boolean = false
)

class EditorViewModel(private val container: AppContainer) : ViewModel() {
    private data class Local(val tab: BoxTab = BoxTab.Situation, val editingId: String? = null, val confirmQuit: Boolean = false)

    private val local = MutableStateFlow(Local())
    private val store get() = container.draft

    val state: StateFlow<EditorUiState> = combine(store.draft, local) { draft, ui ->
        val blocks: List<Block> = listOfNotNull(draft.trigger) + draft.conditions + draft.actions
        EditorUiState(draft, ui.tab, blocks.firstOrNull { it.id == ui.editingId }, ui.confirmQuit)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EditorUiState(store.draft.value))

    fun selectTab(tab: BoxTab) = local.update { it.copy(tab = tab) }

    fun add(item: PaletteItem) {
        val block = item.create()
        store.update { d ->
            when (block) {
                is TriggerBlock -> d.copy(trigger = block)
                is ConditionBlock -> d.copy(conditions = d.conditions + block)
                is ActionBlock -> d.copy(actions = d.actions + block)
                else -> d
            }
        }
        local.update { it.copy(tab = nextTab(item.tab)) }
    }

    fun edit(block: Block) = local.update { it.copy(editingId = block.id) }

    fun closeEdit() = local.update { it.copy(editingId = null) }

    fun replace(block: Block) = store.update { d ->
        when (block) {
            is TriggerBlock -> d.copy(trigger = block)
            is ConditionBlock -> d.copy(conditions = d.conditions.map { if (it.id == block.id) block else it })
            is ActionBlock -> d.copy(actions = d.actions.map { if (it.id == block.id) block else it })
            else -> d
        }
    }

    fun remove(block: Block) {
        store.update { d ->
            d.copy(
                trigger = d.trigger.takeUnless { it?.id == block.id },
                conditions = d.conditions.filterNot { it.id == block.id },
                actions = d.actions.filterNot { it.id == block.id }
            )
        }
        closeEdit()
    }

    fun setLogic(logic: String) = store.update { it.copy(logic = logic) }

    fun requestQuit(): Boolean {
        val dirty = !store.draft.value.isEmpty
        if (dirty) local.update { it.copy(confirmQuit = true) }
        return !dirty
    }

    fun cancelQuit() = local.update { it.copy(confirmQuit = false) }

    private fun nextTab(current: BoxTab) = when (current) {
        BoxTab.Situation -> BoxTab.Condition
        BoxTab.Condition -> BoxTab.Action
        BoxTab.Action -> BoxTab.Action
    }
}
