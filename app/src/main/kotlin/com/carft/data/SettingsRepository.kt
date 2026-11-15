package com.carft.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ConflictPolicy { AskEveryTime, FollowPriority }

data class AppSettings(
    val watching: Boolean = true,
    val voiceWhileDriving: Boolean = true,
    val conflictPolicy: ConflictPolicy = ConflictPolicy.AskEveryTime,
    val onboardingDone: Boolean = false
)

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun update(transform: (AppSettings) -> AppSettings)
}

class InMemorySettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {
    private val _settings = MutableStateFlow(initial)
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    override fun update(transform: (AppSettings) -> AppSettings) = _settings.update(transform)
}
