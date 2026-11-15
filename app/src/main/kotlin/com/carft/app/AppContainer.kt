package com.carft.app

import android.content.Context
import com.carft.data.DraftStore
import com.carft.data.HistoryRepository
import com.carft.data.InMemoryHistoryRepository
import com.carft.data.InMemoryOptionRepository
import com.carft.data.InMemorySettingsRepository
import com.carft.data.OptionRepository
import com.carft.data.SampleHistory
import com.carft.data.SettingsRepository
import com.carft.domain.model.SampleOptions
import com.carft.domain.rule.ConditionEvaluator
import com.carft.domain.rule.RuleEngine
import com.carft.infrastructure.pleos.PleosSpeaker
import com.carft.infrastructure.pleos.PleosVehicle
import com.carft.infrastructure.pleos.PleosVehicleAdapter
import com.carft.runtime.OptionRunner
import kotlinx.coroutines.CoroutineScope

class AppContainer(context: Context, scope: CoroutineScope) {
    val vehicle = PleosVehicle(context)
    val speaker = PleosSpeaker(context)
    val options: OptionRepository = InMemoryOptionRepository(SampleOptions.all())
    val history: HistoryRepository = InMemoryHistoryRepository(SampleHistory.all())
    val settings: SettingsRepository = InMemorySettingsRepository()
    val draft = DraftStore()
    val vehicleAdapter = PleosVehicleAdapter(vehicle, speaker)
    val runner = OptionRunner(
        scope = scope,
        state = vehicle.state,
        engine = RuleEngine(ConditionEvaluator(vehicleAdapter), vehicleAdapter),
        options = options,
        history = history,
        settings = settings
    )

    fun start() {
        vehicle.start()
        speaker.start()
        runner.start()
    }

    fun resetToFirstRun() {
        options.reset(emptyList())
        history.reset(emptyList())
        settings.update { it.copy(onboardingDone = false) }
    }

    fun resetToReturningUser() {
        options.reset(SampleOptions.all())
        history.reset(SampleHistory.all())
        settings.update { it.copy(onboardingDone = true) }
    }
}
