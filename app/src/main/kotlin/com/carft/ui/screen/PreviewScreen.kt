package com.carft.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carft.domain.model.ExecutionResult
import com.carft.domain.model.Option
import com.carft.ui.viewmodel.PreviewViewModel

@Composable
fun PreviewScreen(
    option: Option,
    onClose: () -> Unit,
    viewModel: PreviewViewModel = viewModel()
) {
    val fakeState by viewModel.fakeVehicleState.collectAsState()
    val simulationResult by viewModel.simulationResult.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("가상 테스트: ${option.name}", fontSize = 20.sp)
            Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("닫기")
            }
        }

        // 규칙 설명
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)) {
            Text(
                text = option.rule.toNaturalLanguage(),
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }

        // 가짜 차 상태 입력
        Text("차 상태 시뮬레이션", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))

        // 실내 온도
        Text("실내 온도: ${fakeState.interiorTemp?.toInt() ?: "없음"}°C")
        Slider(
            value = fakeState.interiorTemp ?: 20f,
            onValueChange = { viewModel.setFakeInteriorTemp(it) },
            valueRange = -10f..40f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // 벨트 상태
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("운전석 벨트")
                Button(
                    onClick = { viewModel.setFakeDriverBelt(!fakeState.driverBeltBuckled!!) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fakeState.driverBeltBuckled == true) Color.Green else Color.Red
                    )
                ) {
                    Text(if (fakeState.driverBeltBuckled == true) "착용" else "미착용")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("뒷좌석 오른쪽 벨트")
                Button(
                    onClick = { viewModel.setFakeRearRightBelt(!fakeState.rearRightBeltBuckled!!) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fakeState.rearRightBeltBuckled == true) Color.Green else Color.Red
                    )
                ) {
                    Text(if (fakeState.rearRightBeltBuckled == true) "착용" else "미착용")
                }
            }
        }

        // 착좌 상태
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("운전석 착좌")
                Button(
                    onClick = { viewModel.setFakeSeatOccupied("driver", !(fakeState.driverSeatOccupied ?: false)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fakeState.driverSeatOccupied == true) Color.Green else Color.Gray
                    )
                ) {
                    Text(if (fakeState.driverSeatOccupied == true) "착좌" else "공석")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("뒷좌석 오른쪽 착좌")
                Button(
                    onClick = { viewModel.setFakeSeatOccupied("rearRight", !(fakeState.rearRightSeatOccupied ?: false)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fakeState.rearRightSeatOccupied == true) Color.Green else Color.Gray
                    )
                ) {
                    Text(if (fakeState.rearRightSeatOccupied == true) "착좌" else "공석")
                }
            }
        }

        // 주행 상태
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("주행 상태:")
            Button(
                onClick = { viewModel.setFakeDriving(!fakeState.isDriving) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (fakeState.isDriving) Color.Red else Color.Green
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text(if (fakeState.isDriving) "주행 중" else "정차 중")
            }
        }

        // 시뮬레이션 버튼
        Button(
            onClick = { viewModel.simulate(option) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isSimulating,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(if (isSimulating) "테스트 중..." else "시뮬레이션 실행", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 결과
        if (simulationResult != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("결과:", fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(
                        text = simulationResult.toString(),
                        fontSize = 14.sp,
                        color = when (simulationResult) {
                            is ExecutionResult.Success -> Color.Green
                            is ExecutionResult.PartialSuccess -> Color(0xFFFF9800)
                            is ExecutionResult.NotExecuted -> Color.Red
                            is ExecutionResult.Deferred -> Color.Gray
                            null -> Color.Black
                        }
                    )

                    if (simulationResult is ExecutionResult.Success) {
                        Spacer(modifier = Modifier.height(8.dp))
                        (simulationResult as ExecutionResult.Success).actions.forEach { action ->
                            Text("  ✓ ${action.actionId}", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
