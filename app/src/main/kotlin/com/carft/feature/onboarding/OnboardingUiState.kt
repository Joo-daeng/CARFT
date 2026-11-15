package com.carft.feature.onboarding

import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.icon.CarftIcon
import com.carft.ui.model.RuleUi

data class LegendUi(val kind: BlockKind, val icon: CarftIcon, val description: String)

data class NoticeUi(val icon: CarftIcon, val title: String, val description: String)

data class OnboardingUiState(
    val step: Int = 0,
    val legend: List<LegendUi>,
    val exampleName: String,
    val exampleRule: RuleUi,
    val exampleVoice: String,
    val notices: List<NoticeUi>
)

enum class StartPoint(val label: String) {
    FirstRun("시연 시작점 · 처음 설치한 상태"),
    Returning("시연 시작점 · 옵션 3개를 쓰고 있는 상태")
}
