package com.hyundai.carlego.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyundai.carlego.ui.viewmodel.EditorViewModel
import com.hyundai.carlego.ui.viewmodel.PreviewViewModel

enum class AppScreen {
    HOME, EDITOR, PREVIEW
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedOptionIndex by remember { mutableStateOf(-1) }
    val savedOptions = remember { mutableStateListOf<com.hyundai.carlego.domain.model.Option>() }

    when (currentScreen) {
        AppScreen.HOME -> {
            HomeScreen(
                options = savedOptions,
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
}
