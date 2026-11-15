package com.carft.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel

@Composable
fun HomeRoute(
    onCreateOption: () -> Unit,
    onOpenDetail: (optionId: String) -> Unit,
    onOpenEditor: (optionId: String) -> Unit,
    onOpenSimulation: (optionId: String) -> Unit,
    onOpenRecommend: () -> Unit,
    onOpenPriority: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDemoPanel: () -> Unit
) {
    val viewModel = carftViewModel { HomeViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        HomeScreen(
            state = state,
            onOpenRecommend = onOpenRecommend,
            onOpenPriority = onOpenPriority,
            onOpenHistory = onOpenHistory,
            onOpenSettings = onOpenSettings,
            onOpenDemoPanel = onOpenDemoPanel,
            onOpenDetail = onOpenDetail,
            onToggle = viewModel::setEnabled,
            onOpenMenu = viewModel::openMenu,
            onCreate = onCreateOption
        )
        state.menuFor?.let { option ->
            OptionMenuSheet(
                option = option,
                onDismiss = viewModel::closeMenu,
                onEdit = {
                    viewModel.closeMenu()
                    onOpenEditor(option.id)
                },
                onSimulate = {
                    viewModel.closeMenu()
                    onOpenSimulation(option.id)
                },
                onPriority = {
                    viewModel.closeMenu()
                    onOpenPriority()
                },
                onToggle = {
                    viewModel.setEnabled(option.id, !option.enabled)
                    viewModel.closeMenu()
                },
                onDelete = { viewModel.askDelete(option.id) }
            )
        }
        state.confirmDeleteFor?.let { option ->
            DeleteConfirmSheet(option, onCancel = viewModel::cancelDelete, onConfirm = viewModel::confirmDelete)
        }
        state.undo?.let { UndoToast(it, onUndo = viewModel::undoDelete) }
    }
}
