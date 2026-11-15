package com.hyundai.carlego.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.carlego.domain.model.ActionExecutionResult
import com.hyundai.carlego.domain.model.ExecutionRecord
import com.hyundai.carlego.domain.model.ExecutionResult

@Composable
fun ExecutionToast(record: ExecutionRecord, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val (accent, mark, title) = when (record.result) {
        is ExecutionResult.Success -> Triple(Color(0xFF34C759), "✓", "'${record.optionName}' 실행했어요")
        is ExecutionResult.PartialSuccess -> Triple(Color(0xFFFFC53D), "!", "'${record.optionName}' 일부만 실행했어요")
        is ExecutionResult.Deferred -> Triple(Color(0xFFA3A8B2), "?", "'${record.optionName}' 확인할 수 없어 멈췄어요")
        is ExecutionResult.NotExecuted -> Triple(Color(0xFFFF5A5A), "×", "'${record.optionName}' 실행하지 않았어요")
    }
    Surface(
        modifier = modifier.widthIn(min = 640.dp, max = 1100.dp).clickable(onClick = onDismiss),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF323742),
        border = BorderStroke(1.5.dp, accent),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(accent.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(mark, color = accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(18.dp))
            Column {
                Text(title, color = accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(detail(record.result), color = Color(0xFFA3A8B2), fontSize = 16.sp)
            }
        }
    }
}

private fun detail(result: ExecutionResult): String = when (result) {
    is ExecutionResult.Success -> result.actions.joinToString(" · ") { (it as? ActionExecutionResult.Success)?.result ?: "" }
    is ExecutionResult.PartialSuccess -> result.reason
    is ExecutionResult.Deferred -> result.reason
    is ExecutionResult.NotExecuted -> result.reason
}
