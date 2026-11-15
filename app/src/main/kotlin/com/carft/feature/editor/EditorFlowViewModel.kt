package com.carft.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.app.AppContainer
import com.carft.data.Draft
import com.carft.domain.model.isStopOnly
import com.carft.domain.rule.ValidationResult
import com.carft.domain.rule.Validator
import com.carft.feature.home.DrivingRuleUi
import com.carft.ui.model.RuleUi
import com.carft.ui.model.toChip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class EditorFlowUiState(
    val draft: Draft = Draft(),
    val rule: RuleUi = RuleUi(emptyList(), emptyList(), emptyList()),
    val suggestions: List<String> = emptyList(),
    val validation: ValidationResult = ValidationResult(emptyList()),
    val drivingRules: List<DrivingRuleUi> = emptyList()
)

class EditorFlowViewModel(private val container: AppContainer) : ViewModel() {
    val state: StateFlow<EditorFlowUiState> = combine(container.draft.draft, container.options.options) { draft, options ->
        EditorFlowUiState(
            draft = draft,
            rule = RuleUi(
                situation = listOfNotNull(draft.trigger?.toChip()),
                conditions = draft.conditions.map { it.toChip() },
                actions = draft.actions.map { it.toChip() }
            ),
            suggestions = (listOf("하린이 아침", "출근 모드", "비 오는 등원", "퇴근길", "나의 옵션") - options.map { it.name }.toSet()).take(5),
            validation = Validator.validate(draft.optionId, draft.trigger, draft.conditions, draft.logic, draft.actions, options),
            drivingRules = draft.actions.map { DrivingRuleUi(it.toChip(), !it.isStopOnly()) }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EditorFlowUiState(container.draft.draft.value))

    fun setName(name: String) = container.draft.update { it.copy(name = name.take(MAX_NAME)) }

    fun save(): String? {
        val option = container.draft.draft.value.toOption() ?: return null
        val existing = container.options.get(option.id)
        container.options.upsert(option.copy(isEnabled = existing?.isEnabled ?: option.isEnabled))
        return option.id
    }

    private companion object {
        const val MAX_NAME = 20
    }
}
