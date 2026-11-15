package com.carft.data

import com.carft.domain.model.Option
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface OptionRepository {
    val options: StateFlow<List<Option>>
    fun get(id: String): Option?
    fun upsert(option: Option)
    fun delete(id: String): DeletedOption?
    fun restore(deleted: DeletedOption)
    fun setEnabled(id: String, enabled: Boolean)
    fun move(id: String, offset: Int)
    fun reset(options: List<Option>)
}

data class DeletedOption(val option: Option, val index: Int)

class InMemoryOptionRepository(initial: List<Option>) : OptionRepository {
    private val _options = MutableStateFlow(ranked(initial))
    override val options: StateFlow<List<Option>> = _options.asStateFlow()

    override fun get(id: String): Option? = _options.value.firstOrNull { it.id == id }

    override fun upsert(option: Option) = _options.update { list ->
        val index = list.indexOfFirst { it.id == option.id }
        ranked(if (index >= 0) list.toMutableList().also { it[index] = option } else list + option)
    }

    override fun delete(id: String): DeletedOption? {
        val list = _options.value
        val index = list.indexOfFirst { it.id == id }
        if (index < 0) return null
        _options.value = ranked(list.filterIndexed { i, _ -> i != index })
        return DeletedOption(list[index], index)
    }

    override fun restore(deleted: DeletedOption) = _options.update { list ->
        ranked(list.toMutableList().also { it.add(deleted.index.coerceIn(0, it.size), deleted.option) })
    }

    override fun setEnabled(id: String, enabled: Boolean) = _options.update { list ->
        list.map { if (it.id == id) it.copy(isEnabled = enabled) else it }
    }

    override fun move(id: String, offset: Int) = _options.update { list ->
        val from = list.indexOfFirst { it.id == id }
        if (from < 0) return@update list
        val to = (from + offset).coerceIn(0, list.lastIndex)
        ranked(list.toMutableList().also { it.add(to, it.removeAt(from)) })
    }

    override fun reset(options: List<Option>) {
        _options.value = ranked(options)
    }

    private fun ranked(list: List<Option>) = list.mapIndexed { i, option -> option.copy(priority = i + 1) }
}
