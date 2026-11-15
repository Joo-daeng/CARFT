package com.hyundai.carlego.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.carlego.domain.model.Option

@Composable
fun HomeScreen(
    options: List<Option>,
    onCreateOption: () -> Unit,
    onEditOption: (Int) -> Unit,
    onTestOption: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헤더
        Text(
            text = "Car Lego",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 옵션 생성 버튼
        Button(
            onClick = onCreateOption,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F75FE))
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Create")
            Spacer(modifier = Modifier.width(8.dp))
            Text("새 옵션 만들기")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 옵션 리스트
        if (options.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("옵션이 없습니다. 새 옵션을 만들어보세요!")
            }
        } else {
            Text("저장된 옵션 (${options.size}개)", fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
            LazyColumn {
                itemsIndexed(options) { index, option ->
                    OptionCard(
                        option = option,
                        onEdit = { onEditOption(index) },
                        onTest = { onTestOption(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun OptionCard(
    option: Option,
    onEdit: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onEdit() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.name,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = option.rule.toNaturalLanguage(),
                        fontSize = 12.sp,
                        maxLines = 2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onTest,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("테스트", fontSize = 12.sp)
                        }
                        Button(
                            onClick = onEdit,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("수정", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
