package com.carft.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.carft.feature.editor.EditorRoute
import com.carft.feature.history.HistoryRoute
import com.carft.feature.home.HomeRoute
import com.carft.feature.home.OptionDetailRoute
import com.carft.feature.onboarding.OnboardingRoute
import com.carft.feature.onboarding.StartPoint
import com.carft.feature.onboarding.StartPointRoute
import com.carft.feature.priority.ConflictRoute
import com.carft.feature.priority.PriorityRoute
import com.carft.feature.recommend.RecommendRoute
import com.carft.feature.settings.SettingsRoute
import com.carft.feature.simulation.SimulationRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    onImportTemplate: (templateId: String) -> Unit,
    onOpenDemoPanel: () -> Unit
) {
    val back: () -> Unit = { navController.popBackStack() }
    val goHome: () -> Unit = {
        navController.navigate(Routes.HOME) { popUpTo(0) }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.START_POINT) { entry ->
            val point = StartPoint.valueOf(entry.arguments?.getString("point") ?: StartPoint.Returning.name)
            StartPointRoute(
                point = point,
                onFirstRun = { navController.navigate(Routes.ONBOARDING) { popUpTo(0) } },
                onReturning = goHome
            )
        }
        composable(Routes.ONBOARDING) { OnboardingRoute(onDone = goHome) }
        composable(Routes.HOME) {
            HomeRoute(
                onCreateOption = { navController.navigate(Routes.EDITOR) },
                onOpenEditor = { navController.navigate(Routes.EDITOR) },
                onOpenSimulation = { navController.navigate(Routes.simulation(it)) },
                onOpenRecommend = { navController.navigate(Routes.RECOMMEND) },
                onOpenPriority = { navController.navigate(Routes.PRIORITY) },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenDemoPanel = onOpenDemoPanel
            )
        }
        composable(Routes.DETAIL) { entry ->
            OptionDetailRoute(optionId = entry.arguments?.getString("optionId").orEmpty(), onBack = back)
        }
        composable(Routes.EDITOR) { EditorRoute(onDone = goHome, onCancel = back) }
        composable(Routes.SIMULATION) { entry ->
            SimulationRoute(optionId = entry.arguments?.getString("optionId").orEmpty(), onClose = back)
        }
        composable(Routes.PRIORITY) { PriorityRoute(onBack = back) }
        composable(Routes.CONFLICT) { ConflictRoute(onBack = back) }
        composable(Routes.RECOMMEND) {
            RecommendRoute(onBack = back, onImport = { id ->
                onImportTemplate(id)
                goHome()
            })
        }
        composable(Routes.HISTORY) { HistoryRoute(onBack = back) }
        composable(Routes.SETTINGS) {
            SettingsRoute(
                onBack = back,
                onReplayTutorial = { navController.navigate(Routes.ONBOARDING) },
                onResetDone = { navController.navigate(Routes.ONBOARDING) { popUpTo(0) } }
            )
        }
    }
}
