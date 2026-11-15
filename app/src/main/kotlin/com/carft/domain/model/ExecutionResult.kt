package com.carft.domain.model

sealed class ExecutionResult {
    data class Success(val actions: List<ActionExecutionResult>) : ExecutionResult() {
        override fun toString() = "✅ 동작 ${actions.size}개 모두 반영됨"
    }

    data class PartialSuccess(
        val actions: List<ActionExecutionResult>,
        val reason: String = "주행 중 일부만 실행"
    ) : ExecutionResult() {
        val successCount = actions.count { it is ActionExecutionResult.Success }
        override fun toString() = "⚠️ 일부만 실행 ($successCount/${actions.size}) · $reason"
    }

    data class NotExecuted(val reason: String) : ExecutionResult() {
        override fun toString() = "✗ 실행 안 됨: $reason"
    }

    data class Deferred(
        val reason: String,
        val unknownConditions: List<Pair<String, TriState>> = emptyList()
    ) : ExecutionResult() {
        override fun toString() = "❓ 확인 불가: $reason"
    }
}

sealed class ActionExecutionResult {
    abstract val actionId: String

    data class Success(override val actionId: String, val result: String) : ActionExecutionResult()
    data class Conflict(override val actionId: String, val reason: String) : ActionExecutionResult()
    data class Deferred(override val actionId: String, val reason: String) : ActionExecutionResult()
}

data class ExecutionRecord(
    val id: String,
    val optionId: String,
    val optionName: String,
    val triggeredAt: Long,
    val result: ExecutionResult,
    val conditionStates: Map<String, TriState> = emptyMap(),
    val actionResults: List<ActionExecutionResult> = emptyList()
) {
    val status: String = when (result) {
        is ExecutionResult.Success -> "실행"
        is ExecutionResult.PartialSuccess -> "일부만 실행"
        is ExecutionResult.NotExecuted -> "미실행"
        is ExecutionResult.Deferred -> "확인 불가"
    }
}
