package com.carft.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.SampleOptions
import com.carft.ui.component.ExecutionToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import com.carft.ui.viewmodel.HomeViewModel

enum class AppScreen {
    HOME, EDITOR, PREVIEW
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedOptionIndex by remember { mutableStateOf(-1) }
    val savedOptions = remember { mutableStateListOf(*SampleOptions.all().toTypedArray()) }
    val homeViewModel: HomeViewModel = viewModel()
    var banner by remember { mutableStateOf<ExecutionRecord?>(null) }

    LaunchedEffect(Unit) {
        snapshotFlow { savedOptions.toList() }.collect { homeViewModel.syncOptions(it) }
    }
    LaunchedEffect(Unit) {
        homeViewModel.events.collectLatest { record ->
            banner = record
            delay(6_000)
            banner = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AppScreen.HOME -> {
                HomeScreen(
                    options = savedOptions,
                    viewModel = homeViewModel,
                    onCreateOption = { currentScreen = AppScreen.EDITOR },
                    onEditOption = { index ->
                        selectedOptionIndex = index
                        currentScreen = AppScreen.EDITOR
                    },
                    onTestOption = { index ->
                        selectedOptionIndex = index
                        currentScreen = AppScreen.PREVIEW
                    }
                )
            }
            AppScreen.EDITOR -> {
                EditorScreen(
                    onSave = { option ->
                        if (selectedOptionIndex >= 0) {
                            savedOptions[selectedOptionIndex] = option
                            selectedOptionIndex = -1
                        } else {
                            savedOptions.add(option)
                        }
                        currentScreen = AppScreen.HOME
                    },
                    onCancel = {
                        selectedOptionIndex = -1
                        currentScreen = AppScreen.HOME
                    }
                )
            }
            AppScreen.PREVIEW -> {
                if (selectedOptionIndex >= 0 && selectedOptionIndex < savedOptions.size) {
                    PreviewScreen(
                        option = savedOptions[selectedOptionIndex],
                        onClose = {
                            selectedOptionIndex = -1
                            currentScreen = AppScreen.HOME
                        }
                    )
                } else {
                    currentScreen = AppScreen.HOME
                }
            }
        }
        banner?.let { record ->
            ExecutionToast(
                record = record,
                onDismiss = { banner = null },
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
            )
        }
    }
}
