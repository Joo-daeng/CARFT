package com.hyundai.carlego.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyundai.carlego.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class EditorViewModel : ViewModel() {
    private val _blocks = MutableStateFlow<List<Block>>(emptyList())
    val blocks: StateFlow<List<Block>> = _blocks.asStateFlow()

    private val _selectedBlockId = MutableStateFlow<String?>(null)
    val selectedBlockId: StateFlow<String?> = _selectedBlockId.asStateFlow()

    private val _combinationLogic = MutableStateFlow<CombinationLogic>(CombinationLogic("AND"))
    val combinationLogic: StateFlow<CombinationLogic> = _combinationLogic.asStateFlow()

    private val _currentOption = MutableStateFlow<Option?>(null)
    val currentOption: StateFlow<Option?> = _currentOption.asStateFlow()

    private val _optionName = MutableStateFlow("새 옵션")
    val optionName: StateFlow<String> = _optionName.asStateFlow()

    fun addBlock(block: Block) {
        viewModelScope.launch {
            val current = _blocks.value.toMutableList()
            current.add(block)
            _blocks.value = current
        }
    }

    fun removeBlock(blockId: String) {
        viewModelScope.launch {
            _blocks.value = _blocks.value.filter { it.id != blockId }
            if (_selectedBlockId.value == blockId) {
                _selectedBlockId.value = null
            }
        }
    }

    fun selectBlock(blockId: String?) {
        _selectedBlockId.value = blockId
    }

    fun updateBlock(updatedBlock: Block) {
        viewModelScope.launch {
            val current = _blocks.value.toMutableList()
            val index = current.indexOfFirst { it.id == updatedBlock.id }
            if (index >= 0) {
                current[index] = updatedBlock
                _blocks.value = current
            }
        }
    }

    fun setCombinationLogic(logic: String) {
        _combinationLogic.value = CombinationLogic(logic)
    }

    fun setOptionName(name: String) {
        _optionName.value = name
    }

    fun buildOption(): Option? {
        val blocks = _blocks.value
        val triggers = blocks.filterIsInstance<TriggerBlock>()
        val conditions = blocks.filterIsInstance<ConditionBlock>()
        val actions = blocks.filterIsInstance<ActionBlock>()

        if (triggers.isEmpty() || actions.isEmpty()) {
            return null // 트리거와 동작은 필수
        }

        val rule = Rule(
            trigger = triggers.first(),
            conditions = conditions,
            combinationLogic = _combinationLogic.value,
            actions = actions
        )

        return Option(
            name = _optionName.value,
            rule = rule,
            isEnabled = true
        )
    }

    fun saveOption(): Boolean {
        val option = buildOption()
        if (option != null) {
            _currentOption.value = option
            return true
        }
        return false
    }

    fun clear() {
        viewModelScope.launch {
            _blocks.value = emptyList()
            _selectedBlockId.value = null
            _optionName.value = "새 옵션"
            _combinationLogic.value = CombinationLogic("AND")
        }
    }
}
