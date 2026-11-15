package com.carft.feature.recommend

import com.carft.ui.model.RuleUi

data class TemplateUi(
    val id: String,
    val tag: String,
    val popular: Boolean,
    val title: String,
    val description: String,
    val rule: RuleUi,
    val runsWhileDriving: Boolean
)

data class RecommendUiState(val templates: List<TemplateUi> = emptyList())
