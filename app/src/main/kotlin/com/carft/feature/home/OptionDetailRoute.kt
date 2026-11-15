package com.carft.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel

@Composable
fun OptionDetailRoute(
    optionId: String,
    onBack: () -> Unit,
    onEdit: (optionId: String) -> Unit,
    onSimulate: (optionId: String) -> Unit,
    onPriority: () -> Unit
) {
    val viewModel = carftViewModel(key = "detail-$optionId") { OptionDetailViewModel(it, optionId) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        OptionDetailScreen(
            state = state,
            onBack = onBack,
            onEdit = { onEdit(optionId) },
            onSimulate = { onSimulate(optionId) },
            onOpenMenu = viewModel::openMenu,
            onToggle = viewModel::setEnabled,
            onPriority = onPriority,
            onDelete = viewModel::askDelete
        )
        val card = state.card
        if (card != null && state.menuOpen) {
            OptionMenuSheet(
                option = card,
                onDismiss = viewModel::closeMenu,
                onEdit = {
                    viewModel.closeMenu()
                    onEdit(optionId)
                },
                onSimulate = {
                    viewModel.closeMenu()
                    onSimulate(optionId)
                },
                onPriority = {
                    viewModel.closeMenu()
                    onPriority()
                },
                onToggle = {
                    viewModel.setEnabled(!card.enabled)
                    viewModel.closeMenu()
                },
                onDelete = viewModel::askDelete
            )
        }
        if (card != null && state.confirmDelete) {
            DeleteConfirmSheet(
                option = card,
                onCancel = viewModel::cancelDelete,
                onConfirm = {
                    viewModel.delete()
                    onBack()
                }
            )
        }
    }
}
