package com.carft.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.carft.feature.editor.EditorRoute
import com.carft.feature.editor.NamingRoute
import com.carft.feature.editor.SavedRoute
import com.carft.feature.editor.ValidationRoute
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
import com.carft.appContainer
import com.carft.data.RecommendedTemplates
import java.util.UUID

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    onOpenDemoPanel: () -> Unit
) {
    val container = LocalContext.current.appContainer
    val back: () -> Unit = { navController.popBackStack() }
    val goHome: () -> Unit = {
        navController.navigate(Routes.HOME) { popUpTo(0) }
    }
    val createNew: () -> Unit = {
        container.draft.startNew()
        navController.navigate(Routes.EDITOR)
    }
    val editExisting: (String) -> Unit = { id ->
        container.options.get(id)?.let {
            container.draft.startFrom(it, isNew = false)
            navController.navigate(Routes.EDITOR)
        }
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
                onCreateOption = createNew,
                onOpenDetail = { navController.navigate(Routes.detail(it)) },
                onOpenEditor = editExisting,
                onOpenSimulation = { navController.navigate(Routes.simulation(it)) },
                onOpenRecommend = { navController.navigate(Routes.RECOMMEND) },
                onOpenPriority = { navController.navigate(Routes.PRIORITY) },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenDemoPanel = onOpenDemoPanel
            )
        }
        composable(Routes.DETAIL) { entry ->
            OptionDetailRoute(
                optionId = entry.arguments?.getString("optionId").orEmpty(),
                onBack = back,
                onEdit = editExisting,
                onSimulate = { navController.navigate(Routes.simulation(it)) },
                onPriority = { navController.navigate(Routes.PRIORITY) }
            )
        }
        composable(Routes.EDITOR) {
            EditorRoute(onNext = { navController.navigate(Routes.NAMING) }, onQuit = back)
        }
        composable(Routes.NAMING) {
            NamingRoute(onBack = back, onNext = { navController.navigate(Routes.VALIDATION) })
        }
        composable(Routes.VALIDATION) {
            ValidationRoute(
                onBack = back,
                onFix = {
                    if (!navController.popBackStack(Routes.EDITOR, inclusive = false)) {
                        navController.navigate(Routes.EDITOR) { popUpTo(Routes.HOME) }
                    }
                },
                onSimulate = { navController.navigate(Routes.simulation(Routes.DRAFT)) },
                onSaved = { id -> navController.navigate(Routes.saved(id)) { popUpTo(Routes.HOME) } }
            )
        }
        composable(Routes.SAVED) { entry ->
            SavedRoute(
                optionId = entry.arguments?.getString("optionId").orEmpty(),
                onMore = {
                    container.draft.startNew()
                    navController.navigate(Routes.EDITOR) { popUpTo(Routes.HOME) }
                },
                onHome = goHome
            )
        }
        composable(Routes.SIMULATION) { entry ->
            SimulationRoute(optionId = entry.arguments?.getString("optionId").orEmpty(), onClose = back)
        }
        composable(Routes.PRIORITY) { PriorityRoute(onBack = back) }
        composable(Routes.CONFLICT) { ConflictRoute(onBack = back) }
        composable(Routes.RECOMMEND) {
            RecommendRoute(onBack = back, onImport = { id ->
                RecommendedTemplates.find(id)?.let {
                    container.draft.startFrom(it.option.copy(id = UUID.randomUUID().toString()), isNew = true)
                    navController.navigate(Routes.NAMING)
                }
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
