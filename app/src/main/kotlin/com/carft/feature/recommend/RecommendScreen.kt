package com.carft.feature.recommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carft.designsystem.component.BlockChip
import com.carft.designsystem.component.CarftButton
import com.carft.designsystem.component.CarftButtonSize
import com.carft.designsystem.component.CarftScreen
import com.carft.designsystem.component.CarftTopBar
import com.carft.designsystem.icon.CarftIcon
import com.carft.designsystem.theme.CarftTheme
import com.carft.ui.component.PlaceholderLine
import com.carft.ui.component.ScreenPlaceholder

@Composable
fun RecommendScreen(
    state: RecommendUiState,
    onBack: () -> Unit,
    onImport: (templateId: String) -> Unit
) {
    CarftScreen(topBar = { CarftTopBar("추천 옵션", subtitle = "가져온 뒤 내 상황에 맞게 고칠 수 있어요", onBack = onBack) }) {
        ScreenPlaceholder("17:1085") {
            state.templates.forEach { template ->
                PlaceholderLine("${template.tag} · ${template.title} — ${template.description}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    template.rule.all.forEach { BlockChip(it.text, it.kind, icon = it.icon) }
                    CarftButton("내 옵션으로 가져오기", { onImport(template.id) }, size = CarftButtonSize.Small, icon = CarftIcon.Plus)
                }
            }
        }
    }
}

@Preview(widthDp = 1700, heightDp = 900)
@Composable
private fun RecommendScreenPreview() {
    CarftTheme { RecommendScreen(RecommendViewModel().state.value, {}, {}) }
}
