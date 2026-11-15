package com.hyundai.carlego.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hyundai.carlego.designsystem.component.CarftToast
import com.hyundai.carlego.designsystem.component.StatusTone
import com.hyundai.carlego.designsystem.icon.CarftIcon
import com.hyundai.carlego.domain.model.ActionExecutionResult
import com.hyundai.carlego.domain.model.ExecutionRecord
import com.hyundai.carlego.domain.model.ExecutionResult

@Composable
fun ExecutionToast(record: ExecutionRecord, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val name = record.optionName
    when (val result = record.result) {
        is ExecutionResult.Success -> CarftToast(
            "'$name' 실행했어요",
            result.actions.filterIsInstance<ActionExecutionResult.Success>().joinToString(" · ") { it.result },
            StatusTone.Success, modifier, CarftIcon.Check, onClick = onDismiss
        )
        is ExecutionResult.PartialSuccess -> CarftToast("'$name' 일부만 실행했어요", result.reason, StatusTone.Warning, modifier, CarftIcon.Pause, onClick = onDismiss)
        is ExecutionResult.Deferred -> CarftToast("'$name' 확인할 수 없어 멈췄어요", result.reason, StatusTone.Neutral, modifier, CarftIcon.Info, onClick = onDismiss)
        is ExecutionResult.NotExecuted -> CarftToast("'$name' 실행하지 않았어요", result.reason, StatusTone.Danger, modifier, CarftIcon.Warning, onClick = onDismiss)
    }
}
