package com.carft.feature.recommend

import androidx.lifecycle.ViewModel
import com.carft.data.RecommendedTemplates
import com.carft.domain.model.isStopOnly
import com.carft.ui.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecommendViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        RecommendUiState(
            templates = RecommendedTemplates.all().map { t ->
                TemplateUi(
                    id = t.id,
                    tag = t.tag,
                    popular = t.popular,
                    title = t.option.name,
                    description = t.description,
                    rule = t.option.rule.toUi(),
                    runsWhileDriving = t.option.rule.actions.none { it.isStopOnly() }
                )
            }
        )
    )
    val state: StateFlow<RecommendUiState> = _state.asStateFlow()
}
