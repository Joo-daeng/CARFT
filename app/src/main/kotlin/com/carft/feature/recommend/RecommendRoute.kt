package com.carft.feature.recommend

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel

@Composable
fun RecommendRoute(onBack: () -> Unit, onImport: (templateId: String) -> Unit) {
    val viewModel = carftViewModel { RecommendViewModel() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    RecommendScreen(state = state, onBack = onBack, onImport = onImport)
}
