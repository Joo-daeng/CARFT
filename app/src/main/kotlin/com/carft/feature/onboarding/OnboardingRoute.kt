package com.carft.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel
import kotlinx.coroutines.delay

@Composable
fun OnboardingRoute(onDone: () -> Unit) {
    val viewModel = carftViewModel { OnboardingViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    OnboardingScreen(
        state = state,
        onStart = viewModel::next,
        onBackStep = viewModel::previous,
        onConfirm = {
            viewModel.finish()
            onDone()
        }
    )
}

@Composable
fun StartPointRoute(point: StartPoint, onFirstRun: () -> Unit, onReturning: () -> Unit) {
    val viewModel = carftViewModel(key = "start") { OnboardingViewModel(it) }
    LaunchedEffect(point) {
        delay(1_200)
        viewModel.applyStartPoint(point)
        if (point == StartPoint.FirstRun) onFirstRun() else onReturning()
    }
    StartPointScreen(point = point)
}
