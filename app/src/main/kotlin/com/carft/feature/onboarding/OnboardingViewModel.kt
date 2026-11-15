package com.carft.feature.onboarding

import androidx.lifecycle.ViewModel
import com.carft.app.AppContainer
import com.carft.designsystem.component.BlockKind
import com.carft.designsystem.icon.CarftIcon
import com.carft.domain.model.SampleOptions
import com.carft.ui.model.BlockChipUi
import com.carft.ui.model.RuleUi
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
            // 온보딩 예시는 설명용 그림이라 Figma(11:663) 문구 그대로 고정한다
            exampleRule = RuleUi(
                situation = listOf(BlockChipUi("뒷좌석 벨트를 매면", BlockKind.Situation, CarftIcon.Belt)),
                conditions = listOf(BlockChipUi("실내 10°C 이하이면", BlockKind.Condition, CarftIcon.Thermo)),
                actions = listOf(
                    BlockChipUi("공조 24°C", BlockKind.Action, CarftIcon.Fan),
                    BlockChipUi("열선 3단", BlockKind.Action, CarftIcon.Heat)
                )
            ),
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
