package com.hyundai.carlego.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyundai.carlego.domain.model.*
import com.hyundai.carlego.ui.viewmodel.EditorViewModel

@Composable
fun EditorScreen(
    onSave: (Option) -> Unit,
    onCancel: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    val blocks by viewModel.blocks.collectAsState()
    val selectedBlockId by viewModel.selectedBlockId.collectAsState()
    val optionName by viewModel.optionName.collectAsState()
    val combinationLogic by viewModel.combinationLogic.collectAsState()

    var showBlockPicker by remember { mutableStateOf(false) }
    var selectedBlock by remember { mutableStateOf<Block?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("옵션 만들기", fontSize = 24.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                    Text("취소")
                }
                Button(
                    onClick = {
                        if (viewModel.saveOption()) {
                            viewModel.currentOption.value?.let { option ->
                                onSave(option.copy(name = optionName))
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("저장")
                }
            }
        }

        // 옵션 이름
        TextField(
            value = optionName,
            onValueChange = { viewModel.setOptionName(it) },
            label = { Text("옵션 이름") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // 조합 로직
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("조건 조합:")
            listOf("AND", "OR").forEach { logic ->
                Button(
                    onClick = { viewModel.setCombinationLogic(logic) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (combinationLogic.operator == logic) Color.Blue else Color.LightGray
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(logic, fontSize = 12.sp)
                }
            }
        }

        // 조립판
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, Color.Gray)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (blocks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("여기에 브릭을 추가하세요")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(blocks.size) { index ->
                            BrickItem(
                                block = blocks[index],
                                isSelected = blocks[index].id == selectedBlockId,
                                onSelect = { viewModel.selectBlock(blocks[index].id) },
                                onDelete = { viewModel.removeBlock(blocks[index].id) },
                                onPropertyClick = {
                                    selectedBlock = blocks[index]
                                    showBlockPicker = true
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 블록 추가 버튼들
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "상황: 버튼" to { viewModel.addBlock(TriggerBlock.Manual()) },
                "상황: 뒷좌석 벨트" to { viewModel.addBlock(TriggerBlock.SeatBeltBuckled(seat = "rearRight")) },
                "상황: 운전석 착좌" to { viewModel.addBlock(TriggerBlock.SeatOccupied(seat = "driver")) },
                "상황: 와이퍼" to { viewModel.addBlock(TriggerBlock.WiperOn()) },
                "조건: 온도" to { viewModel.addBlock(ConditionBlock.TemperatureRange()) },
                "조건: 착좌" to { viewModel.addBlock(ConditionBlock.IsSeated()) },
                "동작: 공조" to { viewModel.addBlock(ActionBlock.SetHvacTemperature()) },
                "동작: 열선" to { viewModel.addBlock(ActionBlock.SetSeatHeat()) },
                "동작: 조명" to { viewModel.addBlock(ActionBlock.SetAmbientLight()) },
                "동작: 음성" to { viewModel.addBlock(ActionBlock.PlayTextToSpeech()) }
            ).forEach { (label, action) ->
                Button(
                    onClick = action,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(label, fontSize = 11.sp)
                }
            }
        }

        // 미리보기
        if (blocks.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("미리보기:", fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(
                        text = viewModel.buildOption()?.rule?.toNaturalLanguage() ?: "규칙을 구성하세요",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    // 속성 편집 다이얼로그
    if (showBlockPicker && selectedBlock != null) {
        PropertyEditDialog(
            block = selectedBlock!!,
            onUpdate = { updatedBlock ->
                viewModel.updateBlock(updatedBlock)
                selectedBlock = null
                showBlockPicker = false
            },
            onDismiss = {
                selectedBlock = null
                showBlockPicker = false
            }
        )
    }
}

@Composable
fun BrickItem(
    block: Block,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    onPropertyClick: () -> Unit
) {
    val bgColor = when (block) {
        is TriggerBlock -> Color(0xFFFFA500) // 주황색
        is ConditionBlock -> Color(0xFF90EE90) // 초록색
        is ActionBlock -> Color(0xFFFFB6C6) // 핑크색
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color.Blue else Color.Gray
            ),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = block.displayName(),
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = onPropertyClick,
                    modifier = Modifier.size(32.dp),
                    contentPadding = PaddingValues(2.dp)
                ) {
                    Text("⚙️", fontSize = 12.sp)
                }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                    contentPadding = PaddingValues(2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("✕", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PropertyEditDialog(
    block: Block,
    onUpdate: (Block) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("속성 편집") },
        text = {
            Column {
                when (block) {
                    is ConditionBlock.TemperatureRange -> {
                        var min by remember { mutableStateOf(block.minTemp.toString()) }
                        var max by remember { mutableStateOf(block.maxTemp.toString()) }
                        TextField(value = min, onValueChange = { min = it }, label = { Text("최소 온도") })
                        TextField(value = max, onValueChange = { max = it }, label = { Text("최대 온도") })
                    }
                    is ActionBlock.SetHvacTemperature -> {
                        var temp by remember { mutableStateOf(block.targetTemp.toString()) }
                        TextField(value = temp, onValueChange = { temp = it }, label = { Text("목표 온도") })
                    }
                    is ActionBlock.PlayTextToSpeech -> {
                        var text by remember { mutableStateOf(block.text) }
                        TextField(value = text, onValueChange = { text = it }, label = { Text("음성 텍스트") })
                    }
                    else -> Text("편집 불가")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // 간단한 버전: 그냥 업데이트
                onUpdate(block)
            }) {
                Text("확인")
            }
        }
    )
}
