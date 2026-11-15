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
    PaletteItem(BoxTab.Situation, "정한 시각이 되면", CarftIcon.Clock) { TriggerBlock.TimeOfDay(hour = 7, minute = 30) },
    PaletteItem(BoxTab.Condition, "실내 온도가 ○°C", CarftIcon.Thermo) { ConditionBlock.TemperatureRange(minTemp = -40f, maxTemp = 10f) },
    PaletteItem(BoxTab.Condition, "요일", CarftIcon.Calendar) { ConditionBlock.Weekday(days = "weekday") },
    PaletteItem(BoxTab.Condition, "시간대", CarftIcon.Clock) { ConditionBlock.TimeSlot(slot = "morning") },
    PaletteItem(BoxTab.Condition, "와이퍼 꺼짐", CarftIcon.Wiper) { ConditionBlock.WiperOff() },
    PaletteItem(BoxTab.Action, "공조", CarftIcon.Fan) { ActionBlock.SetHvacTemperature(targetTemp = 24f) },
    PaletteItem(BoxTab.Action, "운전석 열선", CarftIcon.Heat) { ActionBlock.SetSeatHeat(seat = "driver", level = 3) },
    PaletteItem(BoxTab.Action, "핸들 열선", CarftIcon.Steering) { ActionBlock.SteeringHeat() },
    PaletteItem(BoxTab.Action, "음성 안내", CarftIcon.Speaker) { ActionBlock.PlayTextToSpeech(text = "옵션을 실행했어요.") },
    PaletteItem(BoxTab.Action, "실내등 켜기", CarftIcon.Bulb) { ActionBlock.CabinLight() },
    PaletteItem(BoxTab.Action, "내비 목적지", CarftIcon.Nav) { ActionBlock.NaviDestination() }
)

const val MAX_CONDITIONS = 3
const val MAX_ACTIONS = 5

enum class EditorPanel { Sentence, Block, Logic }

data class RemovedBlock(val block: Block, val index: Int)

data class EditorUiState(
    val draft: Draft = Draft(),
    val tab: BoxTab = BoxTab.Situation,
    val editing: Block? = null,
    val panel: EditorPanel = EditorPanel.Sentence,
    val removed: RemovedBlock? = null,
    val confirmQuit: Boolean = false
) {
    val conditionsFull: Boolean get() = draft.conditions.size >= MAX_CONDITIONS
    val actionsFull: Boolean get() = draft.actions.size >= MAX_ACTIONS
}

class EditorViewModel(private val container: AppContainer) : ViewModel() {
    private data class Local(
        val tab: BoxTab = BoxTab.Situation,
        val editingId: String? = null,
        val logicOpen: Boolean = false,
        val logicAsked: Boolean = false,
        val removed: RemovedBlock? = null,
        val confirmQuit: Boolean = false
    )

    private val local = MutableStateFlow(Local())
    private val store get() = container.draft

    val state: StateFlow<EditorUiState> = combine(store.draft, local) { draft, ui ->
        val blocks: List<Block> = listOfNotNull(draft.trigger) + draft.conditions + draft.actions
        val editing = blocks.firstOrNull { it.id == ui.editingId }
        val panel = when {
            editing != null -> EditorPanel.Block
            ui.logicOpen && draft.conditions.size >= 2 -> EditorPanel.Logic
            else -> EditorPanel.Sentence
        }
        EditorUiState(draft, ui.tab, editing, panel, ui.removed, ui.confirmQuit)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EditorUiState(store.draft.value))

    fun selectTab(tab: BoxTab) = local.update { it.copy(tab = tab) }

    fun add(item: PaletteItem) {
        val draft = store.draft.value
        val block = item.create()
        if (block is ConditionBlock && draft.conditions.size >= MAX_CONDITIONS) return
        if (block is ActionBlock && draft.actions.size >= MAX_ACTIONS) return
        store.update { d ->
            when (block) {
                is TriggerBlock -> d.copy(trigger = block)
                is ConditionBlock -> d.copy(conditions = d.conditions + block)
                is ActionBlock -> d.copy(actions = d.actions + block)
                else -> d
            }
        }
        local.update { it.copy(editingId = block.id, logicOpen = false, removed = null) }
    }

    fun edit(block: Block) = local.update { it.copy(editingId = block.id, logicOpen = false) }

    fun openLogic() = local.update { it.copy(editingId = null, logicOpen = true) }

    fun closeEdit() = local.update { it.copy(editingId = null, logicOpen = false) }

    fun confirm() {
        val ui = local.value
        val draft = store.draft.value
        val isCondition = draft.conditions.any { it.id == ui.editingId }
        if (isCondition && draft.conditions.size == 2 && !ui.logicAsked) {
            local.update { it.copy(editingId = null, logicOpen = true, logicAsked = true) }
        } else {
            closeEdit()
        }
    }

    fun replace(block: Block) = store.update { d ->
        when (block) {
            is TriggerBlock -> d.copy(trigger = block)
            is ConditionBlock -> d.copy(conditions = d.conditions.map { if (it.id == block.id) block else it })
            is ActionBlock -> d.copy(actions = d.actions.map { if (it.id == block.id) block else it })
            else -> d
        }
    }

    fun remove(block: Block) {
        val d = store.draft.value
        val index = when (block) {
            is ConditionBlock -> d.conditions.indexOfFirst { it.id == block.id }
            is ActionBlock -> d.actions.indexOfFirst { it.id == block.id }
            else -> 0
        }
        store.update {
            it.copy(
                trigger = it.trigger.takeUnless { t -> t?.id == block.id },
                conditions = it.conditions.filterNot { c -> c.id == block.id },
                actions = it.actions.filterNot { a -> a.id == block.id }
            )
        }
        local.update { it.copy(editingId = null, logicOpen = false, removed = RemovedBlock(block, index)) }
    }

    fun undoRemove() {
        val removed = local.value.removed ?: return
        val block = removed.block
        store.update { d ->
            when (block) {
                is TriggerBlock -> d.copy(trigger = block)
                is ConditionBlock -> d.copy(conditions = d.conditions.toMutableList().apply { add(removed.index.coerceIn(0, size), block) })
                is ActionBlock -> d.copy(actions = d.actions.toMutableList().apply { add(removed.index.coerceIn(0, size), block) })
                else -> d
            }
        }
        local.update { it.copy(removed = null) }
    }

    fun dismissRemoved() = local.update { it.copy(removed = null) }

    fun setLogic(logic: String) = store.update { it.copy(logic = logic) }

    fun requestQuit(): Boolean {
        val dirty = !store.draft.value.isEmpty
        if (dirty) local.update { it.copy(confirmQuit = true) }
        return !dirty
    }

    fun cancelQuit() = local.update { it.copy(confirmQuit = false) }
}
