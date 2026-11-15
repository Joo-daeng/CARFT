package com.carft.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.carft.appContainer
import com.carft.data.RecommendedTemplates
import com.carft.domain.model.ExecutionRecord
import com.carft.feature.demopanel.DemoPanelRoute
import com.carft.feature.demopanel.DemoPanelViewModel
import com.carft.feature.onboarding.StartPoint
import com.carft.ui.component.ExecutionToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

@Composable
fun AppRoot() {
    val container = LocalContext.current.appContainer
    val navController = rememberNavController()
    val demoPanel = carftViewModel { DemoPanelViewModel(it) }
    var toast by remember { mutableStateOf<ExecutionRecord?>(null) }
    val start = remember { if (container.settings.settings.value.onboardingDone) Routes.HOME else Routes.ONBOARDING }

    LaunchedEffect(Unit) {
        container.runner.events.collectLatest { record ->
            toast = record
            delay(6_000)
            toast = null
        }
    }

    Box(Modifier.fillMaxSize()) {
        AppNavHost(
            navController = navController,
            startDestination = start,
            onImportTemplate = { id ->
                RecommendedTemplates.find(id)?.let { container.options.upsert(it.option.copy(id = UUID.randomUUID().toString())) }
            },
            onOpenDemoPanel = demoPanel::show
        )
        toast?.let { record ->
            ExecutionToast(
                record = record,
                onDismiss = { toast = null },
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
            )
        }
        DemoPanelRoute(
            viewModel = demoPanel,
            onFirstRun = { navController.navigate(Routes.startPoint(StartPoint.FirstRun.name)) { popUpTo(0) } },
            onReturning = { navController.navigate(Routes.startPoint(StartPoint.Returning.name)) { popUpTo(0) } }
        )
    }
}
