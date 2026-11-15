package com.carft.feature.onboarding

import androidx.lifecycle.ViewModel
import com.carft.app.AppContainer
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.icon.CarftIcon
import com.carft.domain.model.SampleOptions
import com.carft.ui.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel(private val container: AppContainer) : ViewModel() {
    private val example = SampleOptions.all().first()

    private val _state = MutableStateFlow(
        OnboardingUiState(
            legend = listOf(
                LegendUi(BlockKind.Situation, CarftIcon.Belt, "언제 시작할까요"),
                LegendUi(BlockKind.Condition, CarftIcon.Thermo, "어떤 때만 할까요"),
                LegendUi(BlockKind.Logic, CarftIcon.Logic, "조건을 모두 / 하나라도"),
                LegendUi(BlockKind.Action, CarftIcon.Fan, "무엇을 할까요")
            ),
            exampleName = example.name,
            exampleRule = example.rule.toUi(),
            exampleVoice = "“하린이가 탔어요. 히터를 켰어요.”",
            notices = listOf(
                NoticeUi(CarftIcon.Eye, "차 상태를 읽어요", "벨트 · 좌석 · 실내 온도 · 와이퍼를 확인해 상황이 생겼는지 봐요"),
                NoticeUi(CarftIcon.Fan, "공조 · 열선 · 조명을 바꿔요", "내가 만든 옵션에 넣은 동작만 실행해요"),
                NoticeUi(CarftIcon.Car, "주행 중엔 안전한 동작만", "실내등 · 내비 목적지처럼 정차 중에만 되는 동작은 멈춘 뒤 실행해요"),
                NoticeUi(CarftIcon.History, "시동을 다시 걸어도 이어서", "앱을 닫아도 옵션은 계속 살아 있어요")
            )
        )
    )
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun next() = _state.update { it.copy(step = 1) }

    fun previous() = _state.update { it.copy(step = 0) }

    fun finish() = container.settings.update { it.copy(onboardingDone = true) }

    fun applyStartPoint(point: StartPoint) = when (point) {
        StartPoint.FirstRun -> container.resetToFirstRun()
        StartPoint.Returning -> container.resetToReturningUser()
    }
}
