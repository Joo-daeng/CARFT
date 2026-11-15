package com.carft.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carft.app.AppContainer
import com.carft.data.ConflictPolicy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(private val container: AppContainer) : ViewModel() {
    val state: StateFlow<SettingsUiState> = container.settings.settings.map {
        SettingsUiState(
            watching = it.watching,
            voiceWhileDriving = it.voiceWhileDriving,
            conflictPolicy = it.conflictPolicy
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setWatching(value: Boolean) = container.settings.update { it.copy(watching = value) }

    fun setVoiceWhileDriving(value: Boolean) = container.settings.update { it.copy(voiceWhileDriving = value) }

    fun setConflictPolicy(value: ConflictPolicy) = container.settings.update { it.copy(conflictPolicy = value) }

    fun resetAll() = container.resetToFirstRun()
}
