package com.carft.feature.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carft.designsystem.theme.CarftColors
import com.carft.designsystem.theme.CarftShapes
import com.carft.designsystem.theme.CarftType
import com.carft.domain.model.clockText
import kotlinx.coroutines.launch
import kotlin.math.abs

private val ROW = 56.dp
private const val VISIBLE = 5
private const val HALF = VISIBLE / 2
private const val LOOPS = 1000

@Composable
fun TimeWheelEditor(hour: Int, minute: Int, onPick: (Int, Int) -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(CarftColors.App, CarftShapes.Button)
                .border(1.dp, CarftColors.Stroke, CarftShapes.Button)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    Text("시", style = CarftType.Caption, color = CarftColors.TextTertiary)
                    Text("분", style = CarftType.Caption, color = CarftColors.TextTertiary)
                }
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .background(CarftColors.SurfaceRaised, CarftShapes.Small)
                            .border(1.dp, CarftColors.StrokeStrong, CarftShapes.Small)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Wheel(24, hour) { onPick(it, minute) }
                        Text(":", style = CarftType.Metric, color = CarftColors.TextPrimary, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
                        Wheel(60, minute) { onPick(hour, it) }
                    }
                }
            }
        }
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(CarftColors.Surface, CarftShapes.Small)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("선택한 시간", style = CarftType.BodyS, color = CarftColors.TextSecondary)
                Text(clockText(hour, minute), style = CarftType.LabelS.copy(fontWeight = FontWeight.Bold), color = CarftColors.TextPrimary)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Wheel(count: Int, value: Int, onPick: (Int) -> Unit) {
    val start = count * (LOOPS / 2) + value - HALF
    val state = rememberLazyListState(initialFirstVisibleItemIndex = start)
    val scope = rememberCoroutineScope()
    val rowPx = with(LocalDensity.current) { ROW.toPx() }
    val pick by rememberUpdatedState(onPick)
    val current by rememberUpdatedState(value)
    val center by remember {
        derivedStateOf { state.firstVisibleItemIndex + HALF + if (state.firstVisibleItemScrollOffset > rowPx / 2) 1 else 0 }
    }

    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress) {
            val picked = center % count
            if (picked != current) pick(picked)
        }
    }
    LaunchedEffect(value) {
        if (!state.isScrollInProgress && center % count != value) {
            state.scrollToItem(center - center % count + value - HALF)
        }
    }

    LazyColumn(
        state = state,
        flingBehavior = rememberSnapFlingBehavior(state),
        modifier = Modifier.width(120.dp).height(ROW * VISIBLE),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count * LOOPS) { index ->
            val distance = abs(index - center)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(ROW)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        scope.launch { state.animateScrollToItem(index - HALF) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "%02d".format(index % count),
                    style = when (distance) {
                        0 -> CarftType.Metric
                        1 -> CarftType.CardTitle.copy(fontWeight = FontWeight.Medium)
                        else -> CarftType.BodyS
                    },
                    color = when (distance) {
                        0 -> CarftColors.TextPrimary
                        1 -> CarftColors.TextSecondary
                        else -> CarftColors.TextTertiary
                    }
                )
            }
        }
    }
}
