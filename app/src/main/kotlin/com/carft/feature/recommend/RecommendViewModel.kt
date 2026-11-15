package com.carft.feature.recommend

import androidx.lifecycle.ViewModel
import com.carft.data.RecommendedTemplates
import com.carft.domain.model.ActionBlock
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
                    runsWhileDriving = t.option.rule.actions.none { it.stopOnly() }
                )
            }
        )
    )
    val state: StateFlow<RecommendUiState> = _state.asStateFlow()

    private fun ActionBlock.stopOnly() = when (this) {
        is ActionBlock.SetHvacTemperature -> requiresStationary
        is ActionBlock.SetSeatHeat -> requiresStationary
        is ActionBlock.SetAmbientLight -> requiresStationary
        is ActionBlock.PlayTextToSpeech -> requiresStationary
    }
}
