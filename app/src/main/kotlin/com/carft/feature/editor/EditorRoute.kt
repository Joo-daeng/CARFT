package com.carft.feature.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carft.app.carftViewModel
import com.carft.appContainer
import com.carft.ui.model.toUi

@Composable
fun EditorRoute(onNext: () -> Unit, onQuit: () -> Unit) {
    val viewModel = carftViewModel { EditorViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val back = { if (viewModel.requestQuit()) onQuit() }

    BackHandler(onBack = back)
    Box(Modifier.fillMaxSize()) {
        EditorScreen(
            state = state,
            onBack = back,
            onNext = onNext,
            onSelectTab = viewModel::selectTab,
            onAdd = viewModel::add,
            onEditBlock = viewModel::edit,
            onOpenLogic = viewModel::openLogic,
            onDeselect = viewModel::closeEdit,
            onUndoRemove = viewModel::undoRemove,
            onDismissRemoved = viewModel::dismissRemoved
        ) { modifier ->
            val editing = state.editing
            if (editing != null) {
                BlockPanel(editing, viewModel::replace, { viewModel.remove(editing) }, viewModel::confirm, modifier)
            } else {
                LogicPanel(state.draft, viewModel::setLogic, viewModel::closeEdit, modifier)
            }
        }
        if (state.confirmQuit) {
            QuitSheet(onKeep = viewModel::cancelQuit, onQuit = {
                viewModel.cancelQuit()
                onQuit()
            })
        }
    }
}

@Composable
fun NamingRoute(onBack: () -> Unit, onNext: () -> Unit) {
    val viewModel = carftViewModel(key = "flow") { EditorFlowViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    NamingScreen(state = state, onBack = onBack, onNameChange = viewModel::setName, onNext = onNext)
}

@Composable
fun ValidationRoute(onBack: () -> Unit, onFix: () -> Unit, onSimulate: () -> Unit, onSaved: (optionId: String) -> Unit) {
    val viewModel = carftViewModel(key = "flow") { EditorFlowViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ValidationScreen(
        state = state,
        onBack = onBack,
        onFix = onFix,
        onSimulate = onSimulate,
        onSave = { viewModel.save()?.let(onSaved) }
    )
}

@Composable
fun SavedRoute(optionId: String, onMore: () -> Unit, onHome: () -> Unit) {
    val options by LocalContext.current.appContainer.options.options.collectAsStateWithLifecycle()
    val option = options.firstOrNull { it.id == optionId }
    BackHandler(onBack = onHome)
    SavedScreen(
        name = option?.name ?: "옵션",
        rank = option?.priority ?: 0,
        chips = option?.rule?.toUi()?.all.orEmpty(),
        onMore = onMore,
        onHome = onHome
    )
}
