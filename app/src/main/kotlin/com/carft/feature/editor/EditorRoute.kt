package com.carft.feature.editor

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.carft.app.AppContainer
import com.carft.app.carftViewModel
import com.carft.domain.model.Option

class EditorSaveViewModel(private val container: AppContainer) : ViewModel() {
    fun save(option: Option) = container.options.upsert(option)
}

@Composable
fun EditorRoute(onDone: () -> Unit, onCancel: () -> Unit) {
    val saver = carftViewModel { EditorSaveViewModel(it) }
    EditorScreen(
        onSave = { option ->
            saver.save(option)
            onDone()
        },
        onCancel = onCancel
    )
}
