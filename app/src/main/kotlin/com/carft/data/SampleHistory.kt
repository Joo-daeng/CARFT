package com.carft.data

import com.carft.domain.model.ActionExecutionResult
import com.carft.domain.model.ExecutionRecord
import com.carft.domain.model.ExecutionResult
import java.util.Calendar
import java.util.UUID

object SampleHistory {
    fun all(now: Long = System.currentTimeMillis()): List<ExecutionRecord> = listOf(
        record("하린이 아침", at(now, daysAgo = 1, 7, 42), ExecutionResult.Success(listOf(
            ok("공조 24°C"), ok("운전석 열선 3단"), ok("음성 안내")
        ))),
        record("출근 모드", at(now, daysAgo = 1, 8, 5), ExecutionResult.Success(listOf(
            ok("공조 22°C"), ok("핸들 열선")
        ))),
        record("비 오는 등원", at(now, daysAgo = 3, 8, 12), ExecutionResult.PartialSuccess(
            listOf(ok("음성 안내"), ActionExecutionResult.Deferred("nav", "주행 중")),
            "내비 목적지는 정차 후 실행"
        )),
        record("하린이 아침", at(now, daysAgo = 3, 7, 40), ExecutionResult.Deferred("실내 온도를 알 수 없어 실행하지 않았어요")),
        record("하린이 아침 ↔ 출근 모드", at(now, daysAgo = 4, 7, 52), ExecutionResult.PartialSuccess(
            listOf(ok("운전석 온도 24°C"), ActionExecutionResult.Conflict("temp", "출근 모드 22°C")),
            "충돌 · 하린이 아침 값으로 맞춤"
        ))
    )

    private fun ok(text: String) = ActionExecutionResult.Success(UUID.randomUUID().toString(), text)

    private fun record(name: String, at: Long, result: ExecutionResult) = ExecutionRecord(
        id = UUID.randomUUID().toString(),
        optionId = name,
        optionName = name,
        triggeredAt = at,
        result = result
    )

    private fun at(now: Long, daysAgo: Int, hour: Int, minute: Int): Long = Calendar.getInstance().apply {
        timeInMillis = now
        add(Calendar.DAY_OF_YEAR, -daysAgo)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }.timeInMillis
}
