package com.carft.data

import com.carft.domain.model.ExecutionRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface HistoryRepository {
    val records: StateFlow<List<ExecutionRecord>>
    fun add(record: ExecutionRecord)
    fun reset(records: List<ExecutionRecord>)
}

class InMemoryHistoryRepository(initial: List<ExecutionRecord> = emptyList()) : HistoryRepository {
    private val _records = MutableStateFlow(initial.sortedByDescending { it.triggeredAt })
    override val records: StateFlow<List<ExecutionRecord>> = _records.asStateFlow()
    override fun add(record: ExecutionRecord) = _records.update { listOf(record) + it }
    override fun reset(records: List<ExecutionRecord>) {
        _records.value = records.sortedByDescending { it.triggeredAt }
    }
}
