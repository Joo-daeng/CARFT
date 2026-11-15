package com.hyundai.carlego.domain.rule

import com.hyundai.carlego.domain.model.*
import com.hyundai.carlego.infrastructure.vehicle.VehicleAdapter

class RuleEngine(
    private val conditionEvaluator: ConditionEvaluator,
    private val adapter: VehicleAdapter
) {
    suspend fun evaluate(rule: Rule, state: VehicleState): ExecutionResult {
        // 1. 트리거 확인
        val triggerMet = evaluateTrigger(rule.trigger, state)
        if (!triggerMet) {
            return ExecutionResult.NotExecuted("트리거 미충족")
        }

        // 2. 조건 평가 (3값 논리)
        if (rule.conditions.isEmpty()) {
            // 조건이 없으면 바로 동작 실행
            return executeActions(rule.actions, state)
        }

        val conditionResults = rule.conditions.map { condition ->
            condition.id to conditionEvaluator.evaluate(condition, state)
        }

        // 3. 조건 조합 (AND/OR)
        val combinedResult = applyCombinationLogic(conditionResults, rule.combinationLogic)

        when (combinedResult) {
            TriState.UNKNOWN -> {
                val unknownConditions = conditionResults.filter { it.second == TriState.UNKNOWN }
                return ExecutionResult.Deferred(
                    reason = "조건 정보 부족",
                    unknownConditions = unknownConditions
                )
            }
            TriState.FALSE -> {
                val failedConditions = conditionResults.filter { it.second == TriState.FALSE }
                return ExecutionResult.NotExecuted(
                    reason = "조건 미충족 (${failedConditions.size}개)"
                )
            }
            TriState.TRUE -> {} // 계속 진행
        }

        // 4. 동작 실행
        return executeActions(rule.actions, state)
    }

    private fun evaluateTrigger(trigger: TriggerBlock, state: VehicleState): Boolean {
        return when (trigger) {
            is TriggerBlock.Manual -> true
            is TriggerBlock.SeatOccupied -> {
                when (trigger.seat) {
                    "driver" -> state.driverSeatOccupied ?: false
                    "rearRight" -> state.rearRightSeatOccupied ?: false
                    else -> false
                }
            }
            is TriggerBlock.SeatBeltBuckled -> {
                when (trigger.seat) {
                    "driver" -> state.driverBeltBuckled ?: false
                    "rearRight" -> state.rearRightBeltBuckled ?: false
                    else -> false
                }
            }
            is TriggerBlock.TemperatureThreshold -> {
                (state.interiorTemp ?: Float.MAX_VALUE) <= trigger.threshold
            }
            is TriggerBlock.WiperOn -> state.wiperOn ?: false
        }
    }

    private fun applyCombinationLogic(
        results: List<Pair<String, TriState>>,
        logic: CombinationLogic
    ): TriState {
        if (results.isEmpty()) return TriState.TRUE

        return when (logic.operator.uppercase()) {
            "AND" -> {
                results.fold(TriState.TRUE as TriState) { acc, (_, result) ->
                    acc and result
                }
            }
            "OR" -> {
                results.fold(TriState.FALSE as TriState) { acc, (_, result) ->
                    acc or result
                }
            }
            else -> TriState.FALSE
        }
    }

    private suspend fun executeActions(
        actions: List<ActionBlock>,
        state: VehicleState
    ): ExecutionResult {
        if (actions.isEmpty()) {
            return ExecutionResult.NotExecuted("동작 없음")
        }

        val results = mutableListOf<ActionExecutionResult>()

        for (action in actions) {
            // 주행 제한 확인
            val requiresStationary = when (action) {
                is ActionBlock.SetHvacTemperature -> action.requiresStationary
                is ActionBlock.SetSeatHeat -> action.requiresStationary
                is ActionBlock.SetAmbientLight -> action.requiresStationary
                is ActionBlock.PlayTextToSpeech -> action.requiresStationary
            }
            if (requiresStationary && state.isDriving) {
                results.add(ActionExecutionResult.Deferred(action.id, "주행 중"))
                continue
            }

            try {
                val result = adapter.executeAction(action)
                results.add(ActionExecutionResult.Success(action.id, result))
            } catch (e: Exception) {
                results.add(ActionExecutionResult.Conflict(action.id, e.message ?: "오류"))
            }
        }

        val successCount = results.count { it is ActionExecutionResult.Success }
        val deferredCount = results.count { it is ActionExecutionResult.Deferred }
        val failed = results.filterIsInstance<ActionExecutionResult.Conflict>()

        if (failed.isNotEmpty()) {
            val reasons = failed.joinToString(", ") { it.reason }
            return if (successCount == 0) {
                ExecutionResult.NotExecuted("동작 실패: $reasons")
            } else {
                ExecutionResult.PartialSuccess(results, "${failed.size}개 실패: $reasons")
            }
        }

        return when {
            deferredCount > 0 && successCount == 0 -> {
                ExecutionResult.PartialSuccess(
                    results,
                    "정차 중만 실행 가능한 동작 ${deferredCount}개 미뤄짐"
                )
            }
            deferredCount > 0 -> {
                ExecutionResult.PartialSuccess(
                    results,
                    "${deferredCount}개 미뤄짐"
                )
            }
            else -> ExecutionResult.Success(results)
        }
    }
}
