package com.carft.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftButtonStyle
import com.carft.designsystem.component.CarftSheet
import com.carft.designsystem.component.CarftToast
import com.carft.designsystem.component.SheetMenuItem
import com.carft.designsystem.component.StatusTone
import com.carft.designsystem.icon.CarftIcon

@Composable
fun OptionMenuSheet(
    option: OptionCardUi,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onSimulate: () -> Unit,
    onPriority: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    CarftSheet(title = option.name, subtitle = "무엇을 할까요?", onDismiss = onDismiss) {
        SheetMenuItem("수정하기", "블록을 바꾸거나 값을 고쳐요", CarftIcon.Gear, onEdit)
        SheetMenuItem("가상 테스트", "실제 차 없이 미리 돌려 봐요", CarftIcon.Sparkle, onSimulate)
        SheetMenuItem("우선순위 바꾸기", "다른 옵션과의 순서를 정해요", CarftIcon.List, onPriority)
        SheetMenuItem(if (option.enabled) "끄기" else "켜기", "잠시 쉬게 하거나 다시 켜요", CarftIcon.Power, onToggle)
        SheetMenuItem("삭제", "10초 안에는 되돌릴 수 있어요", CarftIcon.Warning, onDelete, danger = true)
    }
}

@Composable
fun DeleteConfirmSheet(option: OptionCardUi, onCancel: () -> Unit, onConfirm: () -> Unit) {
    CarftSheet(
        title = "'${option.name}' 옵션을 지울까요?",
        subtitle = "지운 뒤 10초 안에는 되돌릴 수 있어요",
        onDismiss = onCancel,
        leading = CarftIcon.Warning,
        leadingTone = StatusTone.Danger
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CarftButton("취소", onCancel, Modifier.weight(1f), style = CarftButtonStyle.Ghost)
            CarftButton("삭제", onConfirm, Modifier.weight(1f), style = CarftButtonStyle.Danger, icon = CarftIcon.Warning)
        }
    }
}

@Composable
fun UndoToast(undo: UndoUi, onUndo: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(bottom = 48.dp), contentAlignment = Alignment.BottomCenter) {
        CarftToast(
            title = "'${undo.name}' 옵션을 지웠어요",
            message = "${undo.secondsLeft}초 안에 되살릴 수 있어요",
            tone = StatusTone.Neutral,
            icon = CarftIcon.History,
            action = { CarftButton("되돌리기", onUndo, size = CarftButtonSize.Small, icon = CarftIcon.History) }
        )
    }
}
