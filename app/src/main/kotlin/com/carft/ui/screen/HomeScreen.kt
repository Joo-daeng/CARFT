package com.carft.ui.screen

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
import com.carft.domain.model.Option
import com.carft.domain.model.VehicleState
import com.carft.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    options: List<Option>,
    viewModel: HomeViewModel,
    onCreateOption: () -> Unit,
    onEditOption: (Int) -> Unit,
    onTestOption: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val vehicle by viewModel.vehicleState.collectAsState()
        val sdkError by viewModel.sdkError.collectAsState()
        val records by viewModel.records.collectAsState()

        Text(
            text = "CARFT",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        VehicleStateCard(vehicle, sdkError, records.firstOrNull()?.let { "${it.optionName} · ${it.status} · ${it.result}" })

        Spacer(modifier = Modifier.height(16.dp))

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
                        onTest = { onTestOption(index) },
                        onRunNow = { viewModel.runNow(option) }
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
    onTest: () -> Unit,
    onRunNow: () -> Unit
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
                            onClick = onRunNow,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
                        ) {
                            Text("지금 실행", fontSize = 12.sp)
                        }
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

@Composable
fun VehicleStateCard(state: VehicleState, error: String?, lastRun: String?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("지금 차 상태 · Pleos Vehicle SDK", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StateItem("실내 온도", state.interiorTemp?.let { "%.1f°C".format(it) })
                StateItem("운전석", state.driverSeatOccupied?.let { if (it) "앉음" else "비어 있음" })
                StateItem("운전석 벨트", state.driverBeltBuckled?.let { if (it) "맴" else "안 맴" })
                StateItem("뒷좌석 오른쪽", state.rearRightSeatOccupied?.let { if (it) "앉음" else "비어 있음" })
                StateItem("뒷좌석 벨트", state.rearRightBeltBuckled?.let { if (it) "맴" else "안 맴" })
                StateItem("와이퍼", state.wiperOn?.let { if (it) "켜짐" else "꺼짐" })
                StateItem("주행", if (state.isDriving) "주행 중" else "정차 중")
            }
            if (lastRun != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("최근 실행: $lastRun", fontSize = 12.sp)
            }
            if (error != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("SDK 오류: $error", fontSize = 12.sp, color = Color(0xFFFF5A5A))
            }
        }
    }
}

@Composable
private fun StateItem(label: String, value: String?) {
    Column {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(value ?: "모름", fontSize = 16.sp)
    }
}
