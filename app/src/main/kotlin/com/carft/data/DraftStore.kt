package com.carft.data

import com.carft.domain.model.ActionBlock
import com.carft.domain.model.CombinationLogic
import com.carft.domain.model.ConditionBlock
import com.carft.domain.model.Option
import com.carft.domain.model.Rule
import com.carft.domain.model.TriggerBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class Draft(
    val optionId: String = UUID.randomUUID().toString(),
    val isNew: Boolean = true,
    val name: String = "",
    val trigger: TriggerBlock? = null,
    val conditions: List<ConditionBlock> = emptyList(),
    val logic: String = "AND",
    val actions: List<ActionBlock> = emptyList(),
    val enabled: Boolean = true
) {
    val isEmpty: Boolean get() = trigger == null && conditions.isEmpty() && actions.isEmpty()

    val canAssemble: Boolean get() = trigger != null && actions.isNotEmpty()

    fun toRule(): Rule? = trigger?.let { Rule(trigger = it, conditions = conditions, combinationLogic = CombinationLogic(logic), actions = actions) }

    fun toOption(): Option? = toRule()?.let { Option(id = optionId, name = name.ifBlank { "새 옵션" }, rule = it, isEnabled = enabled) }

    companion object {
        fun from(option: Option, isNew: Boolean) = Draft(
            optionId = option.id,
            isNew = isNew,
            name = option.name,
            trigger = option.rule.trigger,
            conditions = option.rule.conditions,
            logic = option.rule.combinationLogic.operator,
            actions = option.rule.actions,
            enabled = option.isEnabled
        )
    }
}

class DraftStore {
    private val _draft = MutableStateFlow(Draft())
    val draft: StateFlow<Draft> = _draft.asStateFlow()

    fun startNew() {
        _draft.value = Draft()
    }

    fun startFrom(option: Option, isNew: Boolean) {
        _draft.value = Draft.from(option, isNew)
    }

    fun update(transform: (Draft) -> Draft) = _draft.update(transform)
}
