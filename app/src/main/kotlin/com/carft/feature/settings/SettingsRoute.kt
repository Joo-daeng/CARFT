package com.carft.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel

@Composable
fun SettingsRoute(onBack: () -> Unit, onReplayTutorial: () -> Unit, onResetDone: () -> Unit) {
    val viewModel = carftViewModel { SettingsViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onBack = onBack,
        onToggleWatching = viewModel::setWatching,
        onToggleVoice = viewModel::setVoiceWhileDriving,
        onSelectConflictPolicy = viewModel::setConflictPolicy,
        onReplayTutorial = onReplayTutorial,
        onResetAll = {
            viewModel.resetAll()
            onResetDone()
        }
    )
}
